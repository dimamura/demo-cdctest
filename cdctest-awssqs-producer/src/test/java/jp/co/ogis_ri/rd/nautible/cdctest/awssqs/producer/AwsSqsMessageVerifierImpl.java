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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.UnsupportedOperationException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AwsSqsMessageVerifierImpl implements MessageVerifier<Message<String>> {

    @Autowired
    AmazonSQSAsync amazonSqs;

    @Autowired
    QueueMessagingTemplate queueMessagingTemplate;

    @PostConstruct
    public void onPostConstruct() {
        log.info(this.getClass().getSimpleName() + " of MessageVerifier implimentation is instantated!");
    }

    @Override
    public void send(Message<String> message, String destination) {
        throw new UnsupportedOperationException("This is producer application testing! No need to send message to any queue, only receiving! message:" + message.toString() + " destination:" + destination);
    }

    @Override
    public <T> void send(T payload, Map<String, Object> headers, String destination) {
        throw new UnsupportedOperationException("This is producer application testing! No need to send message to any queue, only receiving! message:" + payload.toString() + " headers:" + headers.toString() +  " destination:" + destination);
    }

    @Override
    public Message<String> receive(String destination, long timeout, TimeUnit timeUnit) {
        // 実際には使用されないメソッドなのでtimeout/timeUnitの実装は割愛
        return receive(destination);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Message<String> receive(String destination) {

        Message<?> message = queueMessagingTemplate.receive(destination);

        amazonSqs.deleteMessage(amazonSqs.getQueueUrl("cdctest-awssqs-queue").getQueueUrl(), message.getHeaders().get("ReceiptHandle").toString());

        log.info("successflly receive " + message.toString() + " from " + destination + "!");

        return (Message<String>) message;

    }

}
