package com.orvillex.reactordemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.orvillex.reactordemo.repository.mongodb")
public class MongoConfiguration {
        
}
