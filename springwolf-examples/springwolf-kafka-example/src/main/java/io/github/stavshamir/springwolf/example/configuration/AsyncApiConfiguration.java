package io.github.stavshamir.springwolf.example.configuration;

import com.asyncapi.v2.model.info.Contact;
import com.asyncapi.v2.model.info.Info;
import com.asyncapi.v2.model.info.License;
import com.asyncapi.v2.model.server.Server;
import io.github.stavshamir.springwolf.asyncapi.types.KafkaConsumerData;
import io.github.stavshamir.springwolf.asyncapi.types.KafkaProducerData;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.message.header.AsyncHeaders;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.message.header.AsyncHeadersForCloudEventsBuilder;
import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import io.github.stavshamir.springwolf.configuration.EnableAsyncApi;
import io.github.stavshamir.springwolf.example.dtos.AnotherPayloadDto;
import io.github.stavshamir.springwolf.example.dtos.ExamplePayloadDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import static io.github.stavshamir.springwolf.example.configuration.KafkaConfiguration.CONSUMER_TOPIC;
import static io.github.stavshamir.springwolf.example.configuration.KafkaConfiguration.PRODUCER_TOPIC;

@Configuration
@EnableAsyncApi
public class AsyncApiConfiguration {

    private final String BOOTSTRAP_SERVERS;

    public AsyncApiConfiguration(@Value("${kafka.bootstrap.servers}") String bootstrapServers) {
        this.BOOTSTRAP_SERVERS = bootstrapServers;
    }

    @Bean
    public AsyncApiDocket asyncApiDocket() {
        Info info = Info.builder()
                .version("1.0.0")
                .title("Springwolf example project - Kafka")
                .contact(Contact.builder().name("springwolf").url("https://github.com/springwolf/springwolf-core").email("example@example.com").build())
                .description("Springwolf example project to demonstrate springwolfs abilities")
                .license(License.builder().name("Apache License 2.0").build())
                .build();

        KafkaProducerData anotherProducerData = KafkaProducerData.kafkaProducerDataBuilder()
                .topicName(PRODUCER_TOPIC)
                .description("Custom, optional description for this produced to topic")
                .payloadType(AnotherPayloadDto.class)
                .headers(createCloudEventHeaders())
                .build();

        KafkaConsumerData manuallyConfiguredConsumer = KafkaConsumerData.kafkaConsumerDataBuilder()
                .topicName(CONSUMER_TOPIC)
                .description("Custom, optional description for this consumed topic")
                .payloadType(ExamplePayloadDto.class)
                .build();

        return AsyncApiDocket.builder()
                .basePackage("io.github.stavshamir.springwolf.example")
                .info(info)
                .server("kafka", Server.builder().protocol("kafka").url(BOOTSTRAP_SERVERS).build())
                .producer(anotherProducerData)
                .consumer(manuallyConfiguredConsumer)
                .build();
    }

    private static AsyncHeaders createCloudEventHeaders() {
        AsyncHeaders ceBaseHeaders = createCloudEventsBaseHeaders();

        return new AsyncHeadersForCloudEventsBuilder("CloudEventHeadersForAnotherPayloadDtoEndpoint", ceBaseHeaders)
                .withTypeHeader("io.github.stavshamir.springwolf.CloudEventHeadersForAnotherPayloadDtoEndpoint")
                .withSourceHeader("springwolf-kafka-example/anotherPayloadDtoEndpoint")
                .withSubjectHeader("Test Subject")
                .build();
    }

    private static AsyncHeaders createCloudEventsBaseHeaders() {
        return new AsyncHeadersForCloudEventsBuilder()
                .withContentTypeHeader(MediaType.APPLICATION_JSON)
                .withSpecVersionHeader("1.0")
                .withIdHeader("1234-1234-1234")
                .withTimeHeader("2015-07-20T15:49:04-07:00")
                .build();
    }

}
