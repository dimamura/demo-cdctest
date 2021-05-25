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
package jp.co.ogis_ri.rd.nautible.cdctest.rest.producer;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiIntegrationTest {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate client;

    @BeforeEach
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/userservice/user");
    }

    @Test
    public void get() throws Exception {

        Map<String, String> queryString = new HashMap<>();
        queryString.put("id", "machidamachizo");

        URI targetResource = UriComponentsBuilder.fromHttpUrl(base.toString()).queryParam("id", "machidamachizo").build().toUri();

        ResponseEntity<String> response = client.getForEntity(targetResource, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"id\":\"machidamachizo\",\"name\":\"町田町蔵\",\"organization\":\"INU\"}");

    }

}
