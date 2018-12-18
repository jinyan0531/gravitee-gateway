package io.gravitee.gateway.core.processor.chain;

import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.stream.ReadStream;
import io.gravitee.gateway.api.stream.ReadWriteStream;
import io.gravitee.gateway.api.stream.WriteStream;
import io.gravitee.gateway.core.processor.ProcessorFailure;
import io.gravitee.gateway.core.processor.StreamableProcessor;

import java.util.Iterator;
import java.util.List;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class StreamableProcessorChain<T, S> extends ProcessorChain<StreamableProcessor<T, S>, T> implements StreamableProcessor<T, S> {

    private StreamableProcessor<T, S> streamableProcessorChain;

    public StreamableProcessorChain(List<StreamableProcessor<T, S>> processors) {
        super(processors);
    //    this.prepareProcessorChain();
    }


    public void handle(ExecutionContext executionContext) {
        this.handle(executionContext);

    }

    @Override
    public StreamableProcessor<T, S> handler(Handler<T> handler) {
        this.next = handler;
        return this;
    }

    @Override
    public StreamableProcessor<T, S> errorHandler(Handler<ProcessorFailure> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    @Override
    public StreamableProcessor<T, S> exitHandler(Handler<Void> exitHandler) {
        this.exitHandler = exitHandler;
        return this;
    }

    private void prepareProcessorChain() {
        StreamableProcessor<T, S> previousProcessorStreamer = null;
        for (StreamableProcessor<T, S> processor : processors) {
                        // An handler was never assigned to start the chain, so let's do it
                        if (streamableProcessorChain == null) {
                            streamableProcessorChain = processor;
                        }

                        // Chain policy stream using the previous one
                        if (previousProcessorStreamer != null) {
                            previousProcessorStreamer.bodyHandler(processor::write);
                            previousProcessorStreamer.endHandler(result1 -> processor.end());
                        }

                        // Previous stream is now the current policy stream
                        previousProcessorStreamer = processor;

            }


        ReadWriteStream<S> tailPolicyStreamer = previousProcessorStreamer;
        if (streamableProcessorChain != null && tailPolicyStreamer != null) {
            //((StreamableProcessor) tailPolicyStreamer).streamErrorHandler(result -> streamErrorHandler.handle((ProcessorFailure) result));
            //tailPolicyStreamer.bodyHandler(bodyPart -> {if (bodyHandler != null) bodyHandler.handle(bodyPart);});
            //tailPolicyStreamer.endHandler(result -> {if (endHandler != null) endHandler.handle(result);});
        }
    }

    @Override
    protected void doOnSuccess(T data) {
        next.handle((T) streamableProcessorChain);
    }

    @Override
    protected Iterator<StreamableProcessor<T, S>> iterator() {
        return processors.iterator();
    }

    @Override
    public ReadStream<S> bodyHandler(Handler<S> handler) {
        return streamableProcessorChain.bodyHandler(handler);
    }

    @Override
    public ReadStream<S> endHandler(Handler<Void> handler) {
        return streamableProcessorChain.endHandler(handler);
    }

    @Override
    public WriteStream<S> write(S chunk) {
        return streamableProcessorChain.write(chunk);
    }

    @Override
    public void end() {
        streamableProcessorChain.end();
    }

    @Override
    public StreamableProcessor<T, S> streamErrorHandler(Handler<ProcessorFailure> handler) {
        return streamableProcessorChain.streamErrorHandler(handler);
    }
}
