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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException.NotFound;

@ExtendWith(SpringExtension.class)
@AutoConfigureStubRunner(
        stubsMode = StubRunnerProperties.StubsMode.CLASSPATH,
        ids = "jp.co.ogis_ri.rd.nautible.cdctest:cdctest-rest-api:0.1.0-SNAPSHOT:stubs:80"
        )
@SpringBootTest
public class ConsumerApplicationContractTest {

    @Test
    public void test001_200_OK() throws Exception {
        assertDoesNotThrow(() -> ConsumerApplication.main("machidamachizo"));
    }

    @Test
    public void test002_404_NotFound() throws Exception {
        assertThrows(NotFound.class, () -> ConsumerApplication.main("imawanokiyoshiro"));
    }

}
