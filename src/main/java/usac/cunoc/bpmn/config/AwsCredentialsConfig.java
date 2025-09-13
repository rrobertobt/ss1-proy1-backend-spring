package usac.cunoc.bpmn.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * AWS Credentials configuration for different deployment environments
 * Provides flexible credential resolution for local development and EC2
 * deployment
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AwsCredentialsConfig {

    private final AwsS3Properties awsS3Properties;

    /**
     * Creates AWS Credentials Provider that works in multiple environments:
     * 1. EC2 with IAM Role - Uses instance profile credentials automatically
     * 2. Local development - Uses explicit access keys if provided
     * 3. Environment variables - Falls back to
     * AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY
     * 4. AWS CLI configuration - Uses ~/.aws/credentials if available
     */
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        // Check if explicit credentials are provided
        if (hasExplicitCredentials()) {
            log.info("Using explicit AWS credentials from application properties");
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                    awsS3Properties.getAccessKeyId(),
                    awsS3Properties.getSecretAccessKey());
            return StaticCredentialsProvider.create(credentials);
        }

        log.info("Using DefaultCredentialsProvider - will automatically detect:");
        log.info("  1. EC2 Instance Profile credentials (IAM Role)");
        log.info("  2. Environment variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)");
        log.info("  3. AWS CLI credentials file (~/.aws/credentials)");
        log.info("  4. Container credentials (ECS/Fargate)");

        return DefaultCredentialsProvider.create();
    }

    /**
     * Check if explicit credentials are provided in configuration
     */
    private boolean hasExplicitCredentials() {
        return awsS3Properties.getAccessKeyId() != null &&
                !awsS3Properties.getAccessKeyId().trim().isEmpty() &&
                awsS3Properties.getSecretAccessKey() != null &&
                !awsS3Properties.getSecretAccessKey().trim().isEmpty();
    }
}