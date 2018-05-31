package com.here.owc;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = "com.here.owc")
public class ApplicationConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public AmazonSQS sqs() {
        String region = env.getRequiredProperty("aws.region");
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();

        return AmazonSQSClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();
    }
}
