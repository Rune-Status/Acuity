package com.acuitybotting.security.influxdb.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */
@RestController
@Slf4j
public class InfluxDbAuthMiddleman {

    @Value("${influx.port}")
    private String influxPort;

    @Value("${influx.username}")
    private String influxUsername;

    @Value("${influx.password}")
    private String influxPassword;

    @RequestMapping(value = "/*", method = RequestMethod.POST)
    public ResponseEntity<String> post(HttpServletRequest request, @RequestParam(value = "u", required = false) String username, @RequestParam(value = "p",  required = false) String password, @RequestBody(required = false) String body) throws IOException {
        return handle(request, username, password, body);
    }

    @RequestMapping(value = "/*", method = RequestMethod.GET)
    public ResponseEntity<String> get(HttpServletRequest request, @RequestParam(value = "u", required = false) String username, @RequestParam(value = "p", required = false) String password) throws IOException {
        return handle(request, username, password, null);
    }

    private ResponseEntity<String> handle(HttpServletRequest request, String username, String password, String body) throws IOException {
        log.info("Got request from {} {}. {}, {}", request.getRemoteHost(), request.getServletPath() + "?" + request.getQueryString(), username, password);
        if (request.getCookies() != null) log.info("Cookies: " + Arrays.stream(request.getCookies()).map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining(", ")));

        boolean authed = true;
        if (request.getRemoteHost().equals("0:0:0:0:0:0:0:1")) authed = true;
        else if (request.getRemoteHost().equals("68.46.70.47") || request.getRemoteHost().equals("139.225.128.101")) authed = true;
        else if (influxUsername.equals(username) && influxPassword.equals(password)) authed = true;

        if (!authed){
            return new ResponseEntity<>("Acuity auth failed", HttpStatus.UNAUTHORIZED);
        }

        String url = "http://localhost:" + influxPort + request.getServletPath() + "?" + request.getQueryString();

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(request.getMethod());

        if (body != null){
            log.info("Sending body {}.", body);
            con.setDoOutput(true);
            con.getOutputStream().write(body.getBytes("UTF-8"));
        }

        int responseCode = con.getResponseCode();
        log.info("Sent '{}' request to {} got response {}.", request.getMethod(), url, responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder responseString = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            responseString.append(inputLine);
        }
        in.close();

        return new ResponseEntity<>(responseString.toString(), HttpStatus.valueOf(responseCode));
    }
}
