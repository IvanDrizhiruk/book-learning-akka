package ua.dp.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import java.util.concurrent.CompletableFuture;

public class CallBackActor extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private CompletableFuture<String> resultFuture;

    public CallBackActor (CompletableFuture<String> resultFuture) {
        log.info("CallBackActor constructor future {}", resultFuture);
        this.resultFuture = resultFuture;
    }

    @Override
    public Receive createReceive() {

        return ReceiveBuilder.create()
                .match(String.class, this::handleString)
                .matchAny(this::handleUnknownMessages)
                .build();
    }

    private void handleString(String data) {
        log.info("CallBackActor handleString: {}", data);

        resultFuture.complete(data);
    }

    private void handleUnknownMessages(Object data) throws Exception {
        log.info("CallBackActor handleUnknownMessages: {}", data);

        Exception exception = data instanceof Exception
            ? (Exception)data
            : new IllegalArgumentException("Unknown result");

        resultFuture.completeExceptionally(exception);
    }
}