package com.asyncworking.appsyncClient;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AppsyncClient {
    private String apiUrl="https://afqqhpza2nbxrog73lqzju5r4y.appsync-api.ap-southeast-2.amazonaws.com/graphql";
    private String apiKey="da2-kdtkgdgovjhqdhraumaptqi5bu";
    WebClient.RequestBodySpec requestBodySpec = WebClient
            .builder()
            .baseUrl(apiUrl)
            .defaultHeader("x-api-key", apiKey)
            .build()
            .method(HttpMethod.POST)
            .uri("/graphql");

    public void listQuery(){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "query ListEvents {"
                + " listEvents {"
                + "   items {"
                + "     id"
                + "     name"
                + "     where"
                + "     when"
                + "     description"
                + "   }"
                + " }"
                + "}");

        WebClient.ResponseSpec response = requestBodySpec
                .body(BodyInserters.fromValue(requestBody))
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve();
    }
}
