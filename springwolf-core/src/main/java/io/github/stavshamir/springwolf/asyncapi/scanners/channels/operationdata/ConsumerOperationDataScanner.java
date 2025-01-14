package io.github.stavshamir.springwolf.asyncapi.scanners.channels.operationdata;

import io.github.stavshamir.springwolf.asyncapi.types.OperationData;
import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConsumerOperationDataScanner extends AbstractOperationDataScanner {

    private final AsyncApiDocket docket;
    private final SchemasService schemasService;

    @Override
    protected SchemasService getSchemaService() {
        return this.schemasService;
    }

    @Override
    protected List<OperationData> getOperationData() {
        return new ArrayList<>(docket.getConsumers());
    }

    @Override
    protected OperationData.OperationType getOperationType() {
        return OperationData.OperationType.PUBLISH;
    }
}
