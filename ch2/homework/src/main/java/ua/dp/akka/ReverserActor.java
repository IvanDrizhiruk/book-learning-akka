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
                .matchAny(this::handleUnknownMessages)
                .build();
    }

    private void handleString(String data) {
        String reversed = new StringBuilder(data).reverse().toString();
        sender().tell(reversed, ActorRef.noSender());
    }

    private void handleUnknownMessages(Object data) {
        sender().tell(new IllegalArgumentException("Unknown type"), ActorRef.noSender());
    }
}