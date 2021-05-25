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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AwsSqsMessageVerifierImpl implements MessageVerifier<Message<String>> {

    @Autowired
    QueueMessagingTemplate queueMessagingTemplate;

    @PostConstruct
    public void onPostConstruct() {
        log.info(this.getClass().getSimpleName() + " of MessageVerifier implimentation is instantated!");
    }

    @Override
    public void send(Message<String> message, String destination) {
        send(message.getPayload(), message.getHeaders(), destination);
    }

    @Override
    public <T> void send(T payload, Map<String, Object> headers, String destination) {

        if (!headers.containsKey("contentType")) {
            headers.put("contentType", "application/json");
        }

        queueMessagingTemplate.send(destination, new GenericMessage<T>(payload, headers));

        log.info("send message success! destination:" + destination + ", headers:" + headers.toString() + ", payload:" + payload.toString());

    }

    @Override
    public Message<String> receive(String destination, long timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException("This is consumer application testing! No need to receive message to any queue, only sending!" + "destination:" + destination + " timeout:" + Long.toString(timeout) +  " timeUnit:" + timeUnit.toString());
    }

    @Override
    public Message<String> receive(String destination) {
        throw new UnsupportedOperationException("This is consumer application testing! No need to receive message to any queue, only sending!" + "destination:" + destination);
    }

}
