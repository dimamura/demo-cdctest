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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OrderRepositoryAwsS3Impl implements OrderRepository {

    @Value("${cdctest-awssqs-consumer.s3.bucket.name}")
    private String bucketName;

    private static final String OBJECT_KEY = "order-list.xlsx";

    private static final String CONTENTTYPE_VND_OPENXMLOFFICE_SPREDSHEET = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    // Note:
    // 解説用のサンプル実装につき、クラスタ環境のk8sでは正常動作しません
    private OrderList orderList;

    @Autowired
    AmazonS3 s3Client;

    @PostConstruct
    public void onPostConstruct() {
        this.orderList = new OrderList();
        log.info(this.getClass().getSimpleName() + " is instantated successfully! injected s3 backet name is \"" + bucketName + "\"" );
    }

    @Override
    public void save(Order order) {

        this.orderList.addOrder(order);

        byte[] orderListExcelFile = this.orderList.toExcelFile();

        try (ByteArrayInputStream is = new ByteArrayInputStream(orderListExcelFile); BufferedInputStream bis = new BufferedInputStream(is)) {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(CONTENTTYPE_VND_OPENXMLOFFICE_SPREDSHEET);
            metadata.setContentLength(orderListExcelFile.length);

            PutObjectRequest req = new PutObjectRequest(bucketName, OBJECT_KEY, bis, metadata);

            PutObjectResult res = s3Client.putObject(req);

            log.info("successfully put order list excel file \"order-list.xlsx\"! generated etag:" + res.getETag() + ", generated versionid:" + res.getVersionId());

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

}
