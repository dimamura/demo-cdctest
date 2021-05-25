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
package jp.co.ogis_ri.rd.nautible.cdctest.awssqs.consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.PurgeQueueInProgressException;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.PurgeQueueResult;

import jp.co.ogis_ri.rd.nautible.cdctest.awssqs.consumer.ConsumerApplicationContractTest.OrderQueueTestConfiguration;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@AutoConfigureStubRunner(
        stubsMode = StubRunnerProperties.StubsMode.CLASSPATH,
        ids = "jp.co.ogis_ri.rd.nautible.cdctest:cdctest-awssqs-queue:0.1.0-SNAPSHOT:stubs"
)
@SpringBootTest(classes = {ConsumerApplication.class, OrderQueueTestConfiguration.class})
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class ConsumerApplicationContractTest {

    @Configuration
    static class OrderQueueTestConfiguration {

        @Autowired
        AmazonSQSAsync amazonSqs;

        @Bean
        public QueueMessagingTemplate queueMessagingTemplate() {
            return new QueueMessagingTemplate(amazonSqs);
        }

        @Bean
        public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs) {
            SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
            factory.setAmazonSqs(amazonSqs);
            factory.setWaitTimeOut(3);
            factory.setAutoStartup(false);
            factory.setMaxNumberOfMessages(1);
            factory.setVisibilityTimeout(10 * 60);
            return factory;
        }

    }

    @Autowired
    AmazonSQSAsync amazonSqs;

    @Autowired
    SimpleMessageListenerContainer messageListenerContainer;

    @Autowired
    StubFinder stubFinder;

    @MockBean
    OrderRepository orderRepository;

    @BeforeEach
    public void setUp() throws Exception {

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

    @Test
    public void test_case_01_01() throws Exception {

        doNothing().when(orderRepository).save(any());
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        stubFinder.trigger("test_case_01_01");

        messageListenerContainer.start("cdctest-awssqs-queue");

        log.info("Consumer Application is now queuing... this test will sleep few seconds soon, please wait...");
        Thread.sleep(3 * 1000);

        messageListenerContainer.stop("cdctest-awssqs-queue");

        verify(orderRepository, times(1)).save(captor.capture());

        Order savedOrder = captor.getValue();

        assertThat(savedOrder.getId()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
        assertThat(savedOrder.getProduct()).isEqualTo("プロダクト①");
        assertThat(savedOrder.getOrderer()).isEqualTo("町田町蔵");
        assertThat(savedOrder.getCharge()).isEqualTo(1000);
        assertThat(savedOrder.getComment()).isEqualTo("備考");

    }

    @Test
    public void test_case_01_02() throws Exception {

        doNothing().when(orderRepository).save(any());
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        stubFinder.trigger("test_case_01_02");

        messageListenerContainer.start("cdctest-awssqs-queue");

        log.info("Consumer Application is now queuing... this test will sleep few seconds soon, please wait...");
        Thread.sleep(3 * 1000);

        messageListenerContainer.stop("cdctest-awssqs-queue");

        verify(orderRepository, times(1)).save(captor.capture());

        Order savedOrder = captor.getValue();

        assertThat(savedOrder.getId()).isEqualTo("110e8400-e29b-41d4-a716-446655440099");
        assertThat(savedOrder.getProduct()).isEqualTo("プロダクト②");
        assertThat(savedOrder.getOrderer()).isEqualTo("町田町蔵");
        assertThat(savedOrder.getCharge()).isEqualTo(1000);
        assertThat(savedOrder.getComment()).isNull();

    }

}
