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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {

    @Null
    private String id;  // this param is set by producer! http clients of producer must not set it!

    @NotEmpty
    private String product;

    @NotEmpty
    private String orderer;

    @NotNull
    @Positive
    private Integer charge;

    // optional param (nullable!)
    private String comment;

}
