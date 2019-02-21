package com.optimizer.threadpool;

import com.optimizer.util.OptimizerUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.optimizer.util.OptimizerUtils.QUERY;
import static com.optimizer.util.OptimizerUtils.STATUS_OK_RANGE_END;
import static com.optimizer.util.OptimizerUtils.STATUS_OK_RANGE_START;

/***
 Created by mudit.g on Feb, 2019
 ***/
public class Service {
    private static final Logger logger = LoggerFactory.getLogger(Service.class.getSimpleName());
    private static final String SERVICE_LIST = "SHOW MEASUREMENTS with measurement = /phonepe.prod.*.jvm.threads.count/";
    private static final String SERVICE_LIST_PATTERN = "phonepe.prod.(.*).jvm.threads.count";

    private HttpClient client;

    public Service(HttpClient client) {
        this.client = client;
    }

    public List<String> getAllServices() throws Exception {
        List<String> services = new ArrayList<>();
        String query = String.format(QUERY, SERVICE_LIST);
        HttpResponse response;
        try {
            response = OptimizerUtils.executeGetRequest(client, query);
            int status = response.getStatusLine().getStatusCode();
            if (status < STATUS_OK_RANGE_START || status >= STATUS_OK_RANGE_END) {
                logger.error("Error in Http get, Status Code: " + response.getStatusLine().getStatusCode() + " received Response: " + response);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Error in Http get: " + e.getMessage(), e);
            return Collections.emptyList();
        }
        String data = EntityUtils.toString(response.getEntity());
        JSONArray serviceJSONArray = OptimizerUtils.getValuesFromMeasurementResponseData(data);
        if(serviceJSONArray == null) {
            logger.error("Error in getting value from data: " + data);
            return Collections.emptyList();
        }
        Pattern pattern = Pattern.compile(SERVICE_LIST_PATTERN);
        for(int i = 0; i < serviceJSONArray.length(); i++) {
            String metrics = ((JSONArray) serviceJSONArray.get(i)).get(0).toString();
            Matcher matcher = pattern.matcher(metrics);
            if(matcher.find()) {
                services.add(matcher.group(1));
            } else {
                logger.error("Match not found for: " + metrics);
            }
        }
        return services;
    }
}
