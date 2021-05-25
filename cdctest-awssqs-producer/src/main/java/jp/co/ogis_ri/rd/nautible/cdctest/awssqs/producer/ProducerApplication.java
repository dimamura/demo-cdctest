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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;

import com.amazonaws.services.sqs.AmazonSQSAsync;

@SpringBootApplication
public class ProducerApplication {

    @Autowired
    AmazonSQSAsync amazonSqs;

    public static void main(String... args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(amazonSqs);
    }

}
