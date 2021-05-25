/*
 * Copyright 2020 OGIS-RI All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.ogis_ri.rd.nautible.cdctest.awssqs.producer;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.PurgeQueueInProgressException;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.PurgeQueueResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jp.co.ogis_ri.rd.nautible.cdctest.awssqs.producer.OrderQueueBase.OrderQueueTestConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest (classes = {ProducerApplication.class, OrderApiController.class, OrderQueueTestConfiguration.class})
@AutoConfigureMessageVerifier
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class OrderQueueBase {

    @Configuration
    static class OrderQueueTestConfiguration {

        @Bean
        public CsvMapper testDataMapper() {
            return new CsvMapper();
        }

    }

    @Autowired
    AmazonSQSAsync amazonSqs;

    @Autowired
    OrderApiController target;

    private MockMvc mvc;

    private static final String TEST_DATA_CLASSPATH = "jp/co/ogis_ri/rd/nautible/cdctest/awssqs/producer/OrderQueueTest.csv";

    @Autowired
    CsvMapper testDataMapper;

    private Map<String, TestOrder> inputs;

    @BeforeAll
    public void init() throws Exception {

        inputs = new HashMap<String, OrderQueueBase.TestOrder>() {
            {

                CsvSchema testDataSchema = testDataMapper.schemaFor(TestData.class).withNullValue("null");

                Resource testDataCsvFile = new ClassPathResource(TEST_DATA_CLASSPATH);
                InputStream is = testDataCsvFile.getInputStream();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                    MappingIterator<TestData> iterator = testDataMapper.readerFor(TestData.class).with(testDataSchema).readValues(br);
                    TestData testData;
                    while (iterator.hasNext()) {
                        testData = iterator.next();
                        put(testData.getTestCase(), testData.getTestOrder());
                    }

                }

            }
        };

    }

    @BeforeEach
    public void setUp() throws Exception {

        mvc = MockMvcBuilders.standaloneSetup(target).build();

        // Note:
        // キューをクリーンナップするpurgeQueueは反映に最大60秒必要.
        // さらに、60秒に1度しか実行できない制約がある.
        // 仮に、60秒以内に複数回実行された場合、SC/403が応答され、実行時例外が投出される.

        log.info(this.getClass().getSimpleName() + " try to purge queue \"cdctest-awssqs-queue\". it takes up to 60 seconds...");

        PurgeQueueResult result = null;
        int retryCount = 0;
        final int maxRetryCount = 1;

        while (Objects.isNull(result)) {

            try {

                Future<PurgeQueueResult> purge = amazonSqs.purgeQueueAsync(new PurgeQueueRequest("cdctest-awssqs-queue"));
                result = purge.get(60, TimeUnit.SECONDS);

            } catch (ExecutionException e) {

                if (e.getCause().getClass().equals(PurgeQueueInProgressException.class) && retryCount <= maxRetryCount) {
                    Thread.sleep(60 * 1000);
                    retryCount++;
                } else {
                    throw e;
                }

            }

        }

    }

    protected void executeOrder(String testCase) throws Exception {

        TestOrder input = inputs.get(testCase);

        if (Objects.isNull(input)) {
            throw new TestDataLoadingException("missing testdata! testcase:" + testCase + " in " + TEST_DATA_CLASSPATH);
        }

        log.info("Contract Test " + this.getClass().getSimpleName() + "#" + testCase + " is start execution! test order:" + input.toString());

        mvc.perform(MockMvcRequestBuilders.post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.toString())
                .content(input.toJsonString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        log.info("Contract Test " + this.getClass().getSimpleName() + "#" + testCase + " completed execution! start queuing and wait few seconds, please wait...");
        Thread.sleep(3 * 1000);

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    @JsonPropertyOrder({"testCase", "product", "orderer", "charge", "comment"})
    static class TestData {

        private String testCase;

        private String product;

        private String orderer;

        private Integer charge;

        private String comment;

        public TestOrder getTestOrder() {
            return new TestOrder(product, orderer, charge, comment);
        }

    }

    @AllArgsConstructor
    @Getter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class TestOrder {

        @JsonIgnore
        private static final ObjectMapper MAPPER = new ObjectMapper();

        private String product;

        private String orderer;

        private Integer charge;

        private String comment;

        public String toJsonString() throws Exception {
            return MAPPER.writeValueAsString(this);
        }

    }

    static class TestDataLoadingException extends RuntimeException {

        public TestDataLoadingException(String message) {
            super(message);
        }

        public TestDataLoadingException(String message, Exception cause) {
            super(message, cause);
        }

    }

}
