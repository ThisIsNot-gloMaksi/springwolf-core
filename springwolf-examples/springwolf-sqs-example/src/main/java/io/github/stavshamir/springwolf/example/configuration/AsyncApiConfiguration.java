package io.github.stavshamir.springwolf.example.configuration;

import com.asyncapi.v2.model.info.Contact;
import com.asyncapi.v2.model.info.Info;
import com.asyncapi.v2.model.info.License;
import com.asyncapi.v2.model.server.Server;
import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import io.github.stavshamir.springwolf.configuration.EnableAsyncApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAsyncApi
public class AsyncApiConfiguration {

    @Bean
    public AsyncApiDocket asyncApiDocket() {
        Contact contact = Contact.builder()
                .name("springwolf")
                .url("https://github.com/springwolf/springwolf-core")
                .email("example@example.com")
                .build();

        Info info = Info.builder()
                .version("1.0.0")
                .title("Springwolf example project - Kafka")
                .contact(contact)
                .description("Springwolf example project to demonstrate springwolf's abilities")
                .license(License.builder().name("Apache License 2.0").build())
                .build();

        return AsyncApiDocket.builder()
                .basePackage("io.github.stavshamir.springwolf.example.consumers")
                .info(info)
                .server("sqs", Server.builder().protocol("sqs").url("").build()) // TODO add url
                .build();
    }

}
