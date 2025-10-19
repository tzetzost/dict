package org.tstefanov.dict;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DictApplication {

    public static void main(String[] args) {
        SpringApplication.run(DictApplication.class, args);
    }

    // The problematic CommandLineRunner has been removed.
    // The init() logic should be handled by @PostConstruct in the service itself,
    // and the deleteAll() was a development-only feature that caused testing issues.
}
