package io.gravitee.gateway.core.processor;

import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.stream.ReadStream;
import io.gravitee.gateway.api.stream.WriteStream;

public class StreamableProcessorDecorator<T, S> extends AbstractStreamableProcessor<T, S> {

    private final Processor<T> processor;

    public StreamableProcessorDecorator(Processor<T> processor) {
        this.processor = processor;
    }

    @Override
    public void handle(T data) {
        this.processor.handle(data);
    }

    @Override
    public StreamableProcessor<T, S> handler(Handler<T> handler) {
        this.processor.handler(handler);
        return this;
    }

    @Override
    public StreamableProcessor<T, S> errorHandler(Handler<ProcessorFailure> errorHandler) {
        this.processor.errorHandler(errorHandler);
        return this;
    }

    @Override
    public StreamableProcessor<T, S> exitHandler(Handler<Void> exitHandler) {
        this.processor.exitHandler(exitHandler);
        return this;
    }

    @Override
    public ReadStream<S> bodyHandler(Handler<S> bodyHandler) {
        return this;
    }

    @Override
    public ReadStream<S> endHandler(Handler<Void> endHandler) {
        return this;
    }

    @Override
    public WriteStream<S> write(S content) {
        return this;
    }

    @Override
    public void end() {

    }
}
