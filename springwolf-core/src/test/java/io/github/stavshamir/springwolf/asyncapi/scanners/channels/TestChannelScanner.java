package io.github.stavshamir.springwolf.asyncapi.scanners.channels;

import com.asyncapi.v2.binding.OperationBinding;
import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;
import java.util.Map;

public class TestChannelScanner extends AbstractMethodLevelListenerScanner<AbstractMethodLevelListenerScannerTest.TestChannelListener> {

    @Override
    protected Class<AbstractMethodLevelListenerScannerTest.TestChannelListener> getListenerAnnotationClass() {
        return AbstractMethodLevelListenerScannerTest.TestChannelListener.class;
    }

    @Override
    protected String getChannelName(AbstractMethodLevelListenerScannerTest.TestChannelListener annotation) {
        return "test-channel";
    }

    @Override
    protected Map<String, ? extends OperationBinding> buildOperationBinding(AbstractMethodLevelListenerScannerTest.TestChannelListener annotation) {
        return ImmutableMap.of("test", new TestBinding());
    }

    @Override
    protected Class<?> getPayloadType(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException("Only single parameter listener methods are supported");
        }

        return parameterTypes[0];
    }


    @EqualsAndHashCode(callSuper = true)
    public static class TestBinding extends OperationBinding {
    }

}
