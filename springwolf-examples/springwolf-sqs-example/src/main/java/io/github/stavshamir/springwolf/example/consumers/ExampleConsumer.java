package io.github.stavshamir.springwolf.example.consumers;

import io.github.stavshamir.springwolf.example.dtos.AnotherPayloadDto;
import io.github.stavshamir.springwolf.example.dtos.ExamplePayloadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExampleConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ExampleConsumer.class);

    // TODO add SQS annotation
    public void receiveExamplePayload(ExamplePayloadDto payload) {
        logger.info("Received new message in example-topic: {}", payload.toString());
    }

    // TODO add SQS annotation
    public void receiveAnotherPayload(AnotherPayloadDto payload) {
        logger.info("Received new message in another-topic: {}", payload.toString());
    }

}
