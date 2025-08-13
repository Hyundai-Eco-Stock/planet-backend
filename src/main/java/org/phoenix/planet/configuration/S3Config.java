package org.phoenix.planet.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}") // application.yml 에 명시한 내용
    private String ACCESS_KEY;

    @Value("${cloud.aws.credentials.secret-key}")
    private String SECRET_KEY;

    @Value("${cloud.aws.region.static}")
    private String REGION;

    @Bean
    public S3Client s3Client() {

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        return S3Client.builder()
            .region(Region.of(REGION))
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .build();
    }
}