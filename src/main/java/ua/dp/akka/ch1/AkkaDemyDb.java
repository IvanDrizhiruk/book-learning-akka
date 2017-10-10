package ua.dp.akka.ch1;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import ua.dp.akka.ch1.messages.SetRequest;

public class AkkaDemyDb extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(context().system(), this);

	protected final Map<String, Object> map = new HashMap<>();

	@Override
	public Receive createReceive() {
		return ReceiveBuilder
				.create()
				.match(
						SetRequest.class,
						message -> {
							log.info("Received Set request: {}", message);
							map.put(message.getKey(), message.getValue());
						})
				.matchAny(o -> log.info("received unknown message: {}", o))
				.build();
	}
}