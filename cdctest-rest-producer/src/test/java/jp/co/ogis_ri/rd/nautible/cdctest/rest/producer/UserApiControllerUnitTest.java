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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class UserApiControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void get_200_ok() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/user")
                .param("id", "machidamachizo")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("machidamachizo"))
                .andExpect(jsonPath("$.name").value("町田町蔵"))
                .andExpect(jsonPath("$.organization").value("INU"));

    }

    @Test
    public void get_400_badrequest() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/user")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("E00000"))
                .andExpect(jsonPath("$.message").isNotEmpty());

    }

    @Test
    public void get_404_notfound() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/user")
                .param("id", "nakamuratatsuya")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("E00001"))
                .andExpect(jsonPath("$.message").isNotEmpty());

    }

}
