package com.acuitybotting.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/19/2018.
 */
public class HttpUtil {

    public static Map<String, String> addBasicAuthHeader(Map<String, String> headers, String username, String password){
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
        headers.put("Authorization", basicAuth);
        return headers;
    }

    public static String get(Map<String, String> headers, String url, Map<String, String> queryParams) throws Exception {
        return makeRequest("GET", headers, url, queryParams, null);
    }

    public static String makeRequest(String method, Map<String, String> headers, String url, Map<String, String> queryParams, String body) throws Exception {
        if (queryParams != null) url += "?" + queryParams.entrySet().stream().map(entry -> entry.getKey() + "=" + encode(entry.getValue())).collect(Collectors.joining("&"));

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(method);

        if (headers != null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (body != null){
            con.setDoOutput(true);
            con.getOutputStream().write(body.getBytes("UTF8"));
        }

        int responseCode = con.getResponseCode();

        StringBuilder response = new StringBuilder();
        if (responseCode == 200){
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))){
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        }
        else {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()))){
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        }


        return response.toString();
    }

    private static String encode(Object param) {
        try {
            return URLEncoder.encode(String.valueOf(param), "UTF-8");
        } catch (Exception e) {
            return URLEncoder.encode(String.valueOf(param));
        }
    }
}

