package org.tstefanov.dict;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableAsync
public class DictApplication {

    public static void main(String[] args) {
        SpringApplication.run(DictApplication.class, args);
    }

}
