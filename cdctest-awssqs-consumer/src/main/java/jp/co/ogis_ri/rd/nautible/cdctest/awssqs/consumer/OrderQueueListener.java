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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableSqs
@Slf4j
public class OrderQueueListener {

    @Autowired
    OrderRepository orderRepository;    // 解説用サンプルのためService層は省略

    @SqsListener(value = "cdctest-awssqs-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void onReceiveMessage(Order order) {

        log.info("receive new message! received order:" + order.toString());

        this.orderRepository.save(order);

        log.info("received order is successfully saved! listed order:" + order.toString());

    }

}
