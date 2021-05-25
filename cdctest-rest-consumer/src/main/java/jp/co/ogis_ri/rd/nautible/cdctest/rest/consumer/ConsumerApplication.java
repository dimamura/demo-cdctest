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
package jp.co.ogis_ri.rd.nautible.cdctest.rest.consumer;

import java.net.URI;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class ConsumerApplication {

    @Autowired
    RestTemplate client;

    public static void main(String... args) throws Exception {

        try (ConfigurableApplicationContext appContext = SpringApplication.run(ConsumerApplication.class, args)) {
            ConsumerApplication app = appContext.getBean(ConsumerApplication.class);
            app.run(args);
        } catch (Exception e) {
            log.error("main thread cought something exception! exit as non happy status...", e);
            throw e;
        }

    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public void run(String... args) throws Exception {

        String id;

        if (Objects.nonNull(args) && args.length != 0 && !args[0].equals("")) {
            id = args[0];
            log.info("arg[0] is \"" + id + "\". this is search target id!");
        } else {
            id = "root";
            log.info("args is none, use default param \"root\". this is search target id!");
        }

        URI targetResource = UriComponentsBuilder.fromHttpUrl("http://localhost/userservice/user").queryParam("id", id).build().toUri();

        RequestEntity<Void> request = RequestEntity.get(targetResource).header("Accept", "application/json").build();

        log.info("try to execute http request: " + request.toString());

        ResponseEntity<String> response = client.exchange(request, String.class);

        log.info("received response! status code: " + response.getStatusCode().toString() + ", response body:" + response.getBody());

    }

}
