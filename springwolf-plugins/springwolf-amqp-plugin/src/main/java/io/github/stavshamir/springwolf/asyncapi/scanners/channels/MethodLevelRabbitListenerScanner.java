package io.github.stavshamir.springwolf.asyncapi.scanners.channels;

import com.asyncapi.v2.binding.OperationBinding;
import com.asyncapi.v2.binding.amqp.AMQPOperationBinding;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.StringValueResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Service
public class MethodLevelRabbitListenerScanner extends AbstractMethodLevelListenerScanner<RabbitListener>
        implements ChannelsScanner, EmbeddedValueResolverAware {

    private StringValueResolver resolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected Class<RabbitListener> getListenerAnnotationClass() {
        return RabbitListener.class;
    }

    @Override
    protected String getChannelName(RabbitListener annotation) {
        if (annotation.queues().length > 0) {
            return getChannelNameFromQueues(annotation);
        }

        return getChannelNameFromBindings(annotation);
    }

    private String getChannelNameFromQueues(RabbitListener annotation) {
        return Arrays.stream(annotation.queues())
                .map(resolver::resolveStringValue)
                .peek(queue -> log.debug("Resolved queue name: {}", queue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No queue name was found in @RabbitListener annotation"));
    }

    private String getChannelNameFromBindings(RabbitListener annotation) {
        return Arrays.stream(annotation.bindings())
                .map(binding -> binding.value().name())
                .map(resolver::resolveStringValue)
                .peek(queue -> log.debug("Resolved queue name: {}", queue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No queue name was found in @RabbitListener annotation"));
    }

    @Override
    protected Map<String, ? extends OperationBinding> buildOperationBinding(RabbitListener annotation) {
        return ImmutableMap.of("amqp", new AMQPOperationBinding());
    }

    @Override
    protected Class<?> getPayloadType(Method method) {
        String methodName = String.format("%s::%s", method.getDeclaringClass().getSimpleName(), method.getName());
        log.debug("Finding payload type for {}", methodName);

        Class<?>[] parameterTypes = method.getParameterTypes();
        switch (parameterTypes.length) {
            case 0:
                throw new IllegalArgumentException("Listener methods must not have 0 parameters: " + methodName);
            case 1:
                return parameterTypes[0];
            default:
                return getPayloadType(parameterTypes, method.getParameterAnnotations(), methodName);
        }
    }

    private Class<?> getPayloadType(Class<?>[] parameterTypes, Annotation[][] parameterAnnotations, String methodName) {
        int payloadAnnotatedParameterIndex = getPayloadAnnotatedParameterIndex(parameterAnnotations);

        if (payloadAnnotatedParameterIndex == -1) {
            String msg = "Multi-parameter RabbitListener methods must have one parameter annotated with @Payload, "
                    + "but none was found: "
                    + methodName;

            throw new IllegalArgumentException(msg);
        }

        return parameterTypes[payloadAnnotatedParameterIndex];
    }

    private int getPayloadAnnotatedParameterIndex(Annotation[][] parameterAnnotations) {
        for (int i = 0, length = parameterAnnotations.length; i < length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            boolean hasPayloadAnnotation = Arrays.stream(annotations)
                    .anyMatch(annotation -> annotation instanceof Payload);

            if (hasPayloadAnnotation) {
                return i;
            }
        }

        return -1;
    }

}
