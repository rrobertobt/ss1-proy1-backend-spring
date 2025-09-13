package usac.cunoc.bpmn.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * AWS S3 configuration for BPMN file upload system
 * Creates S3Client and S3Presigner beans for dependency injection
 */
@Configuration
@EnableConfigurationProperties(AwsS3Properties.class)
@RequiredArgsConstructor
@Slf4j
public class AwsS3Config {

    private final AwsS3Properties awsS3Properties;

    /**
     * Creates AWS S3 Client with configured credentials and region
     */
    @Bean
    public S3Client s3Client() {
        log.info("Creating S3Client with region: {}", awsS3Properties.getRegion());

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                awsS3Properties.getAccessKeyId(),
                awsS3Properties.getSecretAccessKey());

        return S3Client.builder()
                .region(Region.of(awsS3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    /**
     * Creates S3 Presigner for generating presigned URLs
     */
    @Bean
    public S3Presigner s3Presigner() {
        log.info("Creating S3Presigner with region: {}", awsS3Properties.getRegion());

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                awsS3Properties.getAccessKeyId(),
                awsS3Properties.getSecretAccessKey());

        return S3Presigner.builder()
                .region(Region.of(awsS3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}