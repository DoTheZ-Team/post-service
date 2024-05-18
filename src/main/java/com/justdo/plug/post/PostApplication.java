package com.justdo.plug.post;

import com.justdo.plug.post.global.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableFeignClients
@EnableJpaRepositories(basePackages = "com.justdo.plug.post.domain")
@EnableElasticsearchRepositories(basePackages = "com.justdo.plug.post.elastic")
@ComponentScan(basePackages = {"com.justdo.plug.post.elastic", "com.justdo.plug.post.domain",
        "com.justdo.plug.post.global.exception"})
@SpringBootApplication
@Import(SwaggerConfig.class)
public class PostApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostApplication.class, args);
    }

}
