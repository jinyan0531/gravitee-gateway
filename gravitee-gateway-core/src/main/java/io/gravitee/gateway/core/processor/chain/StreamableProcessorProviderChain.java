package io.gravitee.gateway.core.processor.chain;

import io.gravitee.gateway.core.processor.StreamableProcessor;
import io.gravitee.gateway.core.processor.provider.ProcessorProvider;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class StreamableProcessorProviderChain<T, S> extends StreamableProcessorChain<T, S> {

    private final List<ProcessorProvider<T, StreamableProcessor<T, S>>> providers;

    public StreamableProcessorProviderChain(List<ProcessorProvider<T, StreamableProcessor<T, S>>> providers) {
        super(null);
        this.providers = providers;
    }

    @Override
    protected Iterator<StreamableProcessor<T, S>> iterator() {
        final ListIterator<ProcessorProvider<T, StreamableProcessor<T, S>>> listIterator = providers.listIterator(providers.size());
        return new Iterator<StreamableProcessor<T, S>>() {
            @Override
            public boolean hasNext() { return listIterator.hasNext(); }

            @Override
            public StreamableProcessor<T, S> next() { return listIterator.next().provide(null); }

            @Override
            public void remove() { listIterator.remove(); }
        };
    }
}
