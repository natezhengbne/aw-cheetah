package com.asyncworking.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import io.awspring.cloud.messaging.config.SimpleMessageListenerContainerFactory;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class AmazonSQSConfig {
    @Value("${cloud.aws.credentials.accesskey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretkey}")
    private String secretKey;

    @Value("${cloud.aws.endpoint}")
    private String serviceEndpoint;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(buildAmazonSQSAsync());
    }

    private AmazonSQSAsync buildAmazonSQSAsync() {
        final AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClientBuilder.standard();
        if (serviceEndpoint.equals("http://localhost:4566")) {
            builder.withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, Regions.AP_SOUTHEAST_2.getName()));
        } else {
            builder.withRegion(Regions.AP_SOUTHEAST_2);
        }

        log.info("accesskey = {}, secretKey={}", accessKey, secretKey);
        builder.withCredentials(
            new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey, secretKey)
            )
        );
        return builder.build();
    }

    @Primary
    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        return buildAmazonSQSAsync();
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSqs);
        factory.setMaxNumberOfMessages(10);
        factory.setWaitTimeOut(2);
        return factory;
    }
}
