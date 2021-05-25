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

import java.util.Objects;

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
public class Order {

    private String id;

    private String product;

    private String orderer;

    private Integer charge;

    private String comment;

    public String getIdIfNullBlank() {
        return Objects.nonNull(this.id) ? this.id : "";
    }

    public String getProductIfNullBlank() {
        return Objects.nonNull(this.product) ? this.product : "";
    }

    public String getOrdererIfNullBlank() {
        return Objects.nonNull(this.orderer) ? this.orderer : "";
    }

    public String getChargeIfNullBlank() {
        return Objects.nonNull(this.charge) ? Integer.toString(this.charge) : "";
    }

    public String getCommentIfNullBlank() {
        return Objects.nonNull(this.comment) ? this.comment : "";
    }

}
