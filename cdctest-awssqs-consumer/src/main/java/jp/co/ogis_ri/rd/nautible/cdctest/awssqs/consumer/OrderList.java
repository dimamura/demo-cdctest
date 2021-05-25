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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OrderList {

    private Map<String, Order> orders;

    public OrderList() {
        this.orders = Collections.synchronizedMap(new LinkedHashMap<>());
    }

    public void addOrder(Order order) {

        // Note:
        // SQSは同一メッセージが複数回配信されるケースがあり得る
        // 同一メッセージ（＝同一ID）であれば上書きする

        this.orders.put(order.getId(), order);

    }

    public byte[] toExcelFile() {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); Workbook book = new XSSFWorkbook()) {

            Font font = book.createFont();
            font.setFontName("ＭＳ Ｐゴシック");

            Sheet sheet = book.createSheet("orderList");

            List<Order> ordersAsList = new ArrayList<Order>(this.orders.values());

            Row row;

            synchronized (this.orders) {

                for (int i=0;i<ordersAsList.size();i++) {
                    row = sheet.createRow(i);
                    row.createCell(0).setCellValue(ordersAsList.get(i).getIdIfNullBlank());
                    row.createCell(1).setCellValue(ordersAsList.get(i).getProductIfNullBlank());
                    row.createCell(2).setCellValue(ordersAsList.get(i).getOrdererIfNullBlank());
                    row.createCell(3).setCellValue(ordersAsList.get(i).getChargeIfNullBlank());
                    row.createCell(4).setCellValue(ordersAsList.get(i).getCommentIfNullBlank());
                }

            }

            book.write(out);

            return out.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("faild to create excel order list...", e);
        }

    }

}
