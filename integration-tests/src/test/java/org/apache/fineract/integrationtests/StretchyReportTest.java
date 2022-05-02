/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.integrationtests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.apache.fineract.integrationtest.stretchyreports.StretchyReportHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.GlobalConfigurationHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StretchyReportTest {

    private static final Logger LOG = LoggerFactory.getLogger(StretchyReportTest.class);
    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private StretchyReportHelper stretchyReportHelper;
    private static final String STRETCHY_GET_REPORT_URL = "/fineract-provider/api/v1/reports";
    private static final String STRETCHY_REPORT_URL = "/fineract-provider/api/v1/runreports";

    @BeforeEach
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Test
    public void testReportPagination() {
        this.stretchyReportHelper = new StretchyReportHelper(this.requestSpec, this.responseSpec);

        final ResponseSpecification errorResponse = new ResponseSpecBuilder().expectStatusCode(400).build();
        StretchyReportHelper validationErrorHelper = new StretchyReportHelper(this.requestSpec, errorResponse);
        for (int i = 0; i < 20; i++) {
            final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
            ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        }
        String url = STRETCHY_REPORT_URL + "/" + "Client Listing" + "?" + Utils.TENANT_IDENTIFIER + "&R_officeId=1";
        LinkedHashMap reportData = this.stretchyReportHelper.getStretchyReportDetail(this.requestSpec, this.responseSpec, url, "");
        ArrayList<String> rdata = (ArrayList<String>) reportData.get("data");
        Integer reportDataSize = rdata.size();
        Assertions.assertNotNull(reportDataSize);

        Boolean paginationAllowed = true;
        if (paginationAllowed) {
            Integer pageNo = 0;
            Integer orderBy = 1;
            Integer pageCount = 0;
            Integer pageContent = Math.toIntExact(
                    GlobalConfigurationHelper.getGlobalConfigurationByName(requestSpec, responseSpec, "page-content-limit").getValue());
            pageCount = reportDataSize / pageContent;
            if (reportDataSize % pageContent != 0) {
                pageCount++;
            }
            Integer resultant_report_with_limit_size = 0;
            while (pageCount > pageNo) {
                String reportDataUrl = STRETCHY_REPORT_URL + "/" + "Client Listing" + "?" + Utils.TENANT_IDENTIFIER + "&pageNo=" + pageNo
                        + "&orderby=" + orderBy + "&paginationAllowed=" + paginationAllowed + "&R_officeId=1";
                LinkedHashMap reportDataSlice = this.stretchyReportHelper.getStretchyReportDetail(this.requestSpec, this.responseSpec,
                        reportDataUrl, "");
                ArrayList<String> data = (ArrayList<String>) reportDataSlice.get("data");

                resultant_report_with_limit_size += data.size();
                pageNo++;
            }
            assertEquals(resultant_report_with_limit_size, reportDataSize);

            LOG.info("--------------------------Report with no pageNo--------------------------");

            String reportDataUrl = STRETCHY_REPORT_URL + "/" + "Client Listing" + "?" + Utils.TENANT_IDENTIFIER + "&orderby=" + orderBy
                    + "&paginationAllowed=" + paginationAllowed + "&R_officeId=1";
            ArrayList<HashMap> error = (ArrayList<HashMap>) validationErrorHelper.getStretchyReportDataDetail(this.requestSpec,
                    errorResponse, reportDataUrl, CommonConstants.RESPONSE_ERROR);
            assertEquals("validation.msg.null.pageNo.cannot.be.blank", error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
        }
    }
}
