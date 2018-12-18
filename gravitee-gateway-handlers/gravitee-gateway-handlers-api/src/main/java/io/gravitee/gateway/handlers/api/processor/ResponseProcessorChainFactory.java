package io.gravitee.gateway.handlers.api.processor;

import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.core.processor.StreamableProcessor;
import io.gravitee.gateway.core.processor.chain.StreamableProcessorChain;
import io.gravitee.gateway.core.processor.chain.StreamableProcessorProviderChain;
import io.gravitee.gateway.core.processor.provider.ProcessorProvider;
import io.gravitee.gateway.core.processor.provider.ProcessorSupplier;
import io.gravitee.gateway.handlers.api.policy.api.ApiResponsePolicyChainResolver;
import io.gravitee.gateway.handlers.api.processor.alert.AlertProcessor;
import io.gravitee.gateway.handlers.api.processor.cors.CorsSimpleRequestProcessor;
import io.gravitee.gateway.handlers.api.processor.pathmapping.PathMappingProcessor;
import io.gravitee.gateway.policy.PolicyChainResolver;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class ResponseProcessorChainFactory extends ApiProcessorChainFactory {

    private final List<ProcessorProvider<?, ? extends StreamableProcessor<?, Buffer>>> providers = new ArrayList<>();

    @PostConstruct
    public void initialize() {
        PolicyChainResolver apiResponsePolicyResolver = new ApiResponsePolicyChainResolver();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(apiResponsePolicyResolver);
        providers.add(apiResponsePolicyResolver);

        if (api.getProxy().getCors() != null && api.getProxy().getCors().isEnabled()) {
            providers.add(new ProcessorSupplier(() -> new CorsSimpleRequestProcessor(api.getProxy().getCors())));
        }

        if (api.getPathMappings() != null && !api.getPathMappings().isEmpty()) {
            providers.add(new ProcessorSupplier(() -> new PathMappingProcessor(api.getPathMappings())));
        }

        providers.add(new ProcessorSupplier(() -> new AlertProcessor()));
    }

    @Override
    public StreamableProcessorChain<StreamableProcessor<ExecutionContext, Buffer>, Buffer> create() {
        return new StreamableProcessorProviderChain(providers);
    }
}
