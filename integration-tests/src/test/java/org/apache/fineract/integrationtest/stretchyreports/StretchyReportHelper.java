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
package org.apache.fineract.integrationtest.stretchyreports;

import com.google.gson.Gson;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.fineract.integrationtests.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StretchyReportHelper {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;

    private String nameOfReport = "report_test";
    private String reportType = "Table";
    private String reportSQL = null;
    private String useReport = null;
    List<String> reportParameters = Collections.<String>emptyList();
    private static final Logger LOG = LoggerFactory.getLogger(StretchyReportHelper.class);
    private static final String CREATE_REPORT_URL = "/fineract-provider/api/v1/reports?" + Utils.TENANT_IDENTIFIER;

    public StretchyReportHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public String build() {
        final HashMap<String, Object> map = new HashMap<>();

        map.put("reportName", this.nameOfReport);
        map.put("reportSql", this.reportSQL);
        map.put("reportType", this.reportType);
        map.put("useReport", this.useReport);
        map.put("reportParameters", this.reportParameters);

        String reportCreateJson = new Gson().toJson(map);
        LOG.info("{}", reportCreateJson);
        return reportCreateJson;
    }

    public LinkedHashMap getStretchyReportDetail(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String url, final String jsonReturn) {

        final LinkedHashMap response = Utils.performServerGet(requestSpec, responseSpec, url, jsonReturn);
        return response;
    }

    public Object getStretchyReportDataDetail(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String url, final String jsonReturn) {

        final Object response = Utils.performServerGet(requestSpec, responseSpec, url, jsonReturn);
        return response;
    }

}
