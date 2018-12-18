/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.core.processor.chain;

import io.gravitee.gateway.core.processor.AbstractProcessor;
import io.gravitee.gateway.core.processor.Processor;

import java.util.Iterator;
import java.util.List;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class ProcessorChain<S extends Processor<T>, T> extends AbstractProcessor<T> {

    protected final List<S> processors;
    private final Iterator<S> processorIterator;

    public ProcessorChain(List<S> processors) {
        this.processors = processors;
        this.processorIterator = iterator();
    }

    @Override
    public void handle(T data) {
        if (processorIterator.hasNext()) {
            Processor<T> processor = processorIterator.next();

            processor
                    .handler(__ -> handle(data))
                    .errorHandler(failure -> errorHandler.handle(failure))
                    .exitHandler(stream -> exitHandler.handle(null));

            processor.handle(data);
        } else {
            doOnSuccess(data);
        }
    }

    protected void doOnSuccess(T data) {
        next.handle(data);
    }

    protected abstract Iterator<S> iterator();
}
