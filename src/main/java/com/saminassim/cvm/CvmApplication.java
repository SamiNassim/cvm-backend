package com.saminassim.cvm;

import com.saminassim.cvm.config.StorageProperties;
import com.saminassim.cvm.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class CvmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CvmApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            //  storageService.deleteAll();
              storageService.init();
        };
    }

}
