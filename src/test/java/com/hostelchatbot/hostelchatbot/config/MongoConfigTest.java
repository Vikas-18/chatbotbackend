package com.hostelchatbot.hostelchatbot.config;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class MongoConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(MongoConfig.class)
            .withPropertyValues("spring.data.mongodb.uri=mongodb://localhost:27017/test");

    @Test
    void createsMongoClientFromConfiguredUri() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MongoClient.class);
            assertThat(context.getBean(MongoClient.class)).isNotNull();
        });
    }
}
