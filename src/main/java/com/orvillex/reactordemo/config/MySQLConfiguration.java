package com.orvillex.reactordemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import io.r2dbc.spi.ConnectionFactory;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.orvillex.reactordemo.repository.mysql")
public class MySQLConfiguration {
    
    @Bean
	ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

		Resource schemaResource = new ClassPathResource("db/schema.sql");
		Resource dataResource = new ClassPathResource("db/data.sql");

		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new ResourceDatabasePopulator(schemaResource, dataResource));

		return initializer;
	}
}
