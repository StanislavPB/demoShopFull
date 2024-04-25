package org.demoshop.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3")
@Data
public class S3ConfigurationProperties {

    private String accessKey;
    private String SecretKey;
    private String endpoint;
    private String region;


}
