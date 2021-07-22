package com.asyncworking.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

@Configuration
public class AmazonSQSConfig {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(){
        return new QueueMessagingTemplate(buildAmazonSQSAsync());
    }

    private AmazonSQSAsync buildAmazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withRegion(Regions.AP_SOUTHEAST_2)
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(accessKey,secretKey)
                        )
                )
                .build();
    }
}
