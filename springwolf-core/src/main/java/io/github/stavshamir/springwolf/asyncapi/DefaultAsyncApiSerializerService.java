package io.github.stavshamir.springwolf.asyncapi;

import com.asyncapi.v2.binding.ChannelBinding;
import com.asyncapi.v2.binding.OperationBinding;
import com.asyncapi.v2.binding.kafka.KafkaChannelBinding;
import com.asyncapi.v2.binding.kafka.KafkaOperationBinding;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.stavshamir.springwolf.asyncapi.serializers.ChannelBindingSerializer;
import io.github.stavshamir.springwolf.asyncapi.serializers.KafkaChannelBindingSerializer;
import io.github.stavshamir.springwolf.asyncapi.serializers.KafkaOperationBindingSerializer;
import io.github.stavshamir.springwolf.asyncapi.serializers.OperationBindingSerializer;
import io.github.stavshamir.springwolf.asyncapi.types.AsyncAPI;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DefaultAsyncApiSerializerService implements AsyncApiSerializerService {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private PrettyPrinter printer = new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter("  ", DefaultIndenter.SYS_LF));

    @PostConstruct
    void postConstruct() {
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        registerKafkaOperationBindingSerializer();
    }

    private void registerKafkaOperationBindingSerializer() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ChannelBinding.class, new ChannelBindingSerializer());
        module.addSerializer(OperationBinding.class, new OperationBindingSerializer());
        module.addSerializer(KafkaChannelBinding.class, new KafkaChannelBindingSerializer());
        module.addSerializer(KafkaOperationBinding.class, new KafkaOperationBindingSerializer());
        jsonMapper.registerModule(module);
    }

    @Override
    public String toJsonString(AsyncAPI asyncAPI) throws JsonProcessingException {
        return jsonMapper.writer(printer).writeValueAsString(asyncAPI);
    }

    /**
     * Allows to customize the used objectMapper
     */
    public ObjectMapper getObjectMapper() {
        return jsonMapper;
    }

    /**
     * Allows to override the used PrettyPrinter
     */
    public void setPrettyPrinter(PrettyPrinter prettyPrinter) {
        printer = prettyPrinter;
    }

}
