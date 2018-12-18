package io.gravitee.gateway.handlers.api.processor;

import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.core.processor.StreamableProcessor;
import io.gravitee.gateway.core.processor.StreamableProcessorDecorator;
import io.gravitee.gateway.core.processor.chain.StreamableProcessorChain;
import io.gravitee.gateway.handlers.api.processor.cors.CorsSimpleRequestProcessor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OnErrorProcessorChainFactory extends ApiProcessorChainFactory {

    private List<StreamableProcessor<StreamableProcessor<?, Buffer>, Buffer>> processors;

    @PostConstruct
    public void initialize() {
        if (api.getProxy().getCors() != null && api.getProxy().getCors().isEnabled()) {
            StreamableProcessorDecorator<?, Buffer> decorator = new StreamableProcessorDecorator<>(new CorsSimpleRequestProcessor(api.getProxy().getCors()));

            processors = new ArrayList<>();
            processors.add((StreamableProcessor<StreamableProcessor<?, Buffer>, Buffer>) decorator);
        } else {
            processors = Collections.emptyList();
        }
    }

    @Override
    public StreamableProcessorChain<StreamableProcessor<ExecutionContext, Buffer>, Buffer> create() {
        return new StreamableProcessorChain(processors);
    }
}