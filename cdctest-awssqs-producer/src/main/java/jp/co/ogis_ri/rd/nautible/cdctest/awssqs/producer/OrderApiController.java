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

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderApiController {

    @Autowired
    OrderRepository orderRepository;    // 解説用サンプルのためService層は省略

    @PostMapping("/order")
    public ResponseEntity<?> post(@RequestBody @Validated Order order, BindingResult bindingResult) {

        log.info("received request... order:" + (Objects.nonNull(order) ? order.toString() : "null"));

        try {

            if (Objects.isNull(order) || bindingResult.hasErrors()) {

                log.info("requested order is invalid... :" + (Objects.nonNull(order) ? order.toString() : "null"));
                return new ResponseEntity<Unexpected>(new Unexpected("E00010", "invalid order"), HttpStatus.BAD_REQUEST);

            } else {

                String id = UUID.randomUUID().toString();

                order.setId(id);
                orderRepository.save(order);

                OrderResult result = new OrderResult(id);
                log.info("requested order is accepted... " + result.toString());

                return new ResponseEntity<OrderResult>(result, HttpStatus.OK);

            }

        } catch (OrderException e) {
            log.error("saving order failed!", e);
            return new ResponseEntity<Unexpected>(new Unexpected(e.getErrorCode(), "saving order failed."), HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

}
