package io.gravitee.gateway.handlers.api.processor;

import io.gravitee.definition.model.LoggingMode;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.core.processor.StreamableProcessor;
import io.gravitee.gateway.core.processor.StreamableProcessorDecorator;
import io.gravitee.gateway.core.processor.chain.StreamableProcessorChain;
import io.gravitee.gateway.core.processor.chain.StreamableProcessorProviderChain;
import io.gravitee.gateway.core.processor.provider.ProcessorProvider;
import io.gravitee.gateway.core.processor.provider.ProcessorSupplier;
import io.gravitee.gateway.handlers.api.policy.api.ApiPolicyChainResolver;
import io.gravitee.gateway.handlers.api.policy.plan.PlanPolicyChainResolver;
import io.gravitee.gateway.handlers.api.processor.cors.CorsPreflightRequestProcessor;
import io.gravitee.gateway.handlers.api.processor.logging.ApiLoggableRequestProcessor;
import io.gravitee.gateway.policy.PolicyChainResolver;
import io.gravitee.gateway.security.core.SecurityPolicyChainResolver;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class RequestProcessorChainFactory extends ApiProcessorChainFactory {

    private final List<ProcessorProvider<ExecutionContext, ? extends StreamableProcessor<ExecutionContext, Buffer>>> providers = new ArrayList<>();

    @PostConstruct
    public void initialize() {
        PolicyChainResolver apiPolicyResolver = new ApiPolicyChainResolver();
        PolicyChainResolver securityPolicyResolver = new SecurityPolicyChainResolver();
        PolicyChainResolver planPolicyResolver = new PlanPolicyChainResolver();

        applicationContext.getAutowireCapableBeanFactory().autowireBean(securityPolicyResolver);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(planPolicyResolver);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(apiPolicyResolver);

        if (api.getProxy().getCors() != null && api.getProxy().getCors().isEnabled()) {
            providers.add(new ProcessorSupplier(() ->
                    new StreamableProcessorDecorator<>(new CorsPreflightRequestProcessor(api.getProxy().getCors()))));
        }

        providers.add(securityPolicyResolver);

        if (api.getProxy().getLogging() != null && api.getProxy().getLogging().getMode() != LoggingMode.NONE) {
            providers.add(new ProcessorSupplier<>(() ->
                    new StreamableProcessorDecorator<>(new ApiLoggableRequestProcessor(api.getProxy().getLogging()))));
        }

        providers.add(planPolicyResolver);
        providers.add(apiPolicyResolver);
    }

    @Override
    public StreamableProcessorChain<StreamableProcessor<ExecutionContext, Buffer>, Buffer> create() {
        return new StreamableProcessorProviderChain(providers);
    }
}