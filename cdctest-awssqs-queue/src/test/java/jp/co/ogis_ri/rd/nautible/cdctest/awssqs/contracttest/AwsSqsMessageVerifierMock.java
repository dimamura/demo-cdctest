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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.stereotype.Component;

@Component
public class AwsSqsMessageVerifierMock implements MessageVerifier<Object> {

    // AWS SQSは標準設定だとFIFOではない => Setでダミーを構成する
    private Set<Object> queue = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void send(Object message, String destination) {
        send(message, null, destination);
    }

    @Override
    public <T> void send(T payload, Map<String, Object> headers, String destination) {

        if (Objects.isNull(destination) || "".equals(destination)) {
            throw new IllegalQueueException(payload, destination, "destination is null or empty!");
        }

        if (Objects.isNull(payload)) {
            throw new IllegalQueueException(payload, destination, "message is null!");
        }

        // headersは簡略化のため試験対象外とする

        queue.add(payload);

        System.out.println("successflly send " + payload.toString() + " to " + destination + "!");

    }

    @Override
    public Object receive(String destination, long timeout, TimeUnit timeUnit) {

        if (Objects.isNull(destination) || "".equals(destination)) {
            throw new IllegalQueueException(destination, "destination is null or empty!");
        }

        if (this.queue.isEmpty()) {
            return null;
        }

        Object message;
        List<Object> queueAsList = new ArrayList<Object>(this.queue);

        synchronized (this.queue) {
            message = queueAsList.get(0);
            this.queue.remove(message); // AWS SQSの場合、本来はReceiptHandleで削除するが割愛
        }

        System.out.println("successflly receive " + message.toString() + " from " + destination + "!");

        return message;

    }

    @Override
    public Object receive(String destination) {
        return receive(destination, 0, null);
    }

    public static class IllegalQueueException extends RuntimeException {

        private Object queueMessage;

        private String destination;

        public IllegalQueueException(Object queueMessage, String destination, String exceptionMessgae, Exception cause) {
            super(exceptionMessgae, cause);
            this.queueMessage = queueMessage;
            this.destination = destination;
        }

        public IllegalQueueException(Object queueMessage, String destination, String exceptionMessgae) {
            super(exceptionMessgae);
            this.queueMessage = queueMessage;
            this.destination = destination;
        }

        public IllegalQueueException(String destination, String exceptionMessgae) {
            super(exceptionMessgae);
            this.destination = destination;
        }

        public Object getQueueMessage() {
            return queueMessage;
        }

        public String getDestination() {
            return destination;
        }

    }

}
