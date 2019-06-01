package com.shared.hazelcast.shared;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Nanda
 */
@Configuration
@ComponentScan
@EnableJpaRepositories
@EntityScan
public class SharedConfiguration {
}
