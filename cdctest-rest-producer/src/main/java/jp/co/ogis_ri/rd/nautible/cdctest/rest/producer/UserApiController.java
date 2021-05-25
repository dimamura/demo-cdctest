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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserApiController {

    // Note:
    // 解説用のサンプル実装につき、クラスタ環境のk8sでは正常動作しません
    private Map<String, User> userRepository;

    @PostConstruct
    public void onPostConstruct() {
        userRepository = new HashMap<>();
        userRepository.put("root", new User("root", "管理者", "オージス総研"));
        userRepository.put("machidamachizo", new User("machidamachizo", "町田町蔵", "INU"));
        userRepository.put("endomichiro", new User("endomichiro", "遠藤ミチロウ", "The Starlin"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> get(String id) {

        log.info("received request... id:" + id);

        if (Objects.isNull(id) || "".equals(id)) {
            return new ResponseEntity<Unexpected>(new Unexpected("E00000", "id is required"), HttpStatus.BAD_REQUEST);
        }

        User result = userRepository.get(id);

        ResponseEntity<?> response;

        if (Objects.nonNull(result)) {
            log.info("requested user is found... " + result.toString());
            response = new ResponseEntity<User>(result, HttpStatus.OK);
        } else {
            log.info("request user is not found... id:" + id);
            response = new ResponseEntity<Unexpected>(new Unexpected("E00001", "user not found"), HttpStatus.NOT_FOUND);
        }

        return response;

    }

}
