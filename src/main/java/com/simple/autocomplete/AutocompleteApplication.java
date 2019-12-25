package com.simple.autocomplete;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AutocompleteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutocompleteApplication.class, args);
    }

}
