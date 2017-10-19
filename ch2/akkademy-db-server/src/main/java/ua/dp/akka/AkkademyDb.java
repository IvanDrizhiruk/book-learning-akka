package ua.dp.akka;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import ua.dp.akka.messages.GetRequest;
import ua.dp.akka.messages.KeyNotFoundException;
import ua.dp.akka.messages.SetRequest;

public class AkkademyDb extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);
    protected final Map<String, Object> map = new HashMap<String, Object>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(SetRequest.class, this::handleSetRequest)
                .match(GetRequest.class, this::handleGetRequest)
                .matchAny(this::handleAllOther)
                .build();
    }

    private void handleSetRequest(SetRequest message) {
        log.info("Received Set request: {}", message);
        map.put(message.key, message.value);
        sender().tell(new Status.Success(message.key), self());
    }

    private void handleGetRequest(GetRequest message) {
        log.info("Received Get request: {}", message);
        Object value = map.get(message.key);
        Object response = value == null
				? new Status.Failure(new KeyNotFoundException(message.key))
				: value;
        sender().tell(response, self());
    }

    private void handleAllOther(Object data) {
        sender().tell(new Status.Failure(new ClassNotFoundException()), self());
    }
}