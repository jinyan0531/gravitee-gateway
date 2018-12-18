package io.gravitee.gateway.core.processor.chain;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface ProcessorChainFactory<PC extends ProcessorChain> {

    PC create();
}
