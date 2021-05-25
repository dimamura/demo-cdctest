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
package jp.co.ogis_ri.rd.nautible.cdctest.awssqs.contracttest;

import java.util.Objects;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMessageVerifier
public class OrderQueueBase {

    @SpringBootConfiguration
    static class OrderQueueStubConfiguration {

        @Bean
        public MessageVerifier<?> queue() {
            return new AwsSqsMessageVerifierMock();
        }

    }

    @Autowired
    MessageVerifier<Object> queue;

    protected void executeOrder(String testCase) {

        switch (testCase) {
        case "test_case_01_01":
            this.queue.send("{\"id\":\"7792cae7-a57d-4dab-ba4e-f25139351071\", \"product\":\"プロダクト①\", \"orderer\":\"町田町蔵\", \"charge\":1000, \"comment\":\"備考\"}", "cdctest-awssqs-queue");
            break;
        case "test_case_01_02":
            this.queue.send("{\"id\":\"7792cae7-a57d-4dab-ba4e-f25139351071\", \"product\":\"プロダクト②\", \"orderer\":\"町田町蔵\", \"charge\":1000}", "cdctest-awssqs-queue");
            break;
        default:
            throw new UnknownTestcaseException(testCase, this.getClass());
        }

    }

    static class UnknownTestcaseException extends RuntimeException {

        private String testCase;

        private Class<? extends OrderQueueBase> testClass;

        public UnknownTestcaseException(String testCase, Class<? extends OrderQueueBase> testClass) {
            super("Unknown testcase of order queue contract test found! unknown testcase:" + (Objects.nonNull(testCase) ? testCase : "(null)") + ", executed class:" + testClass.getName());
            this.testCase = testCase;
            this.testClass = testClass;
        }

        public String getTestCase() {
            return testCase;
        }

        public Class<? extends OrderQueueBase> getTestClass() {
            return testClass;
        }

    }

}
