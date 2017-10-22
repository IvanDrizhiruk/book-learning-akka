package ua.dp.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class ReverserActor extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Override
    public Receive createReceive() {

        return ReceiveBuilder.create()
                .match(String.class, this::handleString)
                .match(RuntimeException.class, this::handleRuntimeException)
                .matchAny(this::handleUnknownMessages)
                .build();
    }

    private void handleString(String data) {
        log.info("handleString: {}", data);

        String reversed = new StringBuilder(data).reverse().toString();
        sender().tell(reversed, ActorRef.noSender());
    }

    private void handleRuntimeException(RuntimeException e) {
        log.info("handleRuntimeException: {}", e);

        sender().tell(e, ActorRef.noSender());
    }

    private void handleUnknownMessages(Object data) throws Exception {
        log.info("handleUnknownMessages: {}", data);

        if (data instanceof Exception) {
            log.info("handleUnknownMessages: throw {}", data);

            throw (Exception) data;
        }

        sender().tell(new IllegalArgumentException("Unknown type"), ActorRef.noSender());
    }
}