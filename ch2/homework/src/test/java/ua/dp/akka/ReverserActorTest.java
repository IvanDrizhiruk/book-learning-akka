package ua.dp.akka;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static scala.compat.java8.FutureConverters.toJava;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import akka.pattern.AskTimeoutException;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import scala.concurrent.Future;

public class ReverserActorTest {

	private static final int TIMEOUT_MILLIS = 100;

	private ActorSystem system = ActorSystem.create();

	@Test
	public void actorShouldReturnReversedString() throws Exception {
		//given
		TestActorRef<ReverserActor> actorRef = TestActorRef.create(system, Props.create(ReverserActor.class));

		//when
		Future sFuture = ask(actorRef, "Test message", TIMEOUT_MILLIS);
		CompletableFuture<String> resultFuture = (CompletableFuture<String>)toJava(sFuture);
		String actual = resultFuture.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

		//then
		assertEquals("egassem tseT", actual);
	}

	@Test
	public void actorShouldReturnExceptionIfUnknownMessageWasSend() throws Exception {
		//given
		TestActorRef<ReverserActor> actorRef = TestActorRef.create(system, Props.create(ReverserActor.class));

		//when
		Future sFuture = ask(actorRef, new StringBuilder("Test message"), TIMEOUT_MILLIS);
		CompletableFuture<Object> resultFuture = (CompletableFuture<Object>)toJava(sFuture);
		Object actual = resultFuture.get();

		//then
		assertEquals(IllegalArgumentException.class, actual.getClass());
		assertEquals("Unknown type", ((IllegalArgumentException) actual).getMessage());
	}

	@Test
	public void timeoutExceptionShouldBeHappenedIfExceptionHappenedInActor() throws Exception {
		//given
		TestActorRef<ReverserActor> actorRef = TestActorRef.create(system, Props.create(ReverserActor.class));

		//when
		Future sFuture = ask(actorRef, new IOException("Created from test"), TIMEOUT_MILLIS);
		CompletableFuture<Object> resultFuture = (CompletableFuture<Object>)toJava(sFuture);
		Object actual = resultFuture
				.handle((x,a) -> null == x ? a : x)
				.get();

		//then
		assertEquals(AskTimeoutException.class, actual.getClass());
		assertEquals(
				"Ask timed out on [TestActor[akka://default/user/$$d]] after [100 ms]. Sender[null] sent message of type \"java.io.IOException\".",
				((AskTimeoutException) actual).getMessage());
	}


	@Test
	public void specialHandlerShouldWorkForRuntimeExceptionInstance() throws Exception {
		//given
		TestActorRef<ReverserActor> actorRef = TestActorRef.create(system, Props.create(ReverserActor.class));

		//when
		Future sFuture = ask(actorRef, new RuntimeException("Created from test"), TIMEOUT_MILLIS);
		CompletableFuture<Object> resultFuture = (CompletableFuture<Object>)toJava(sFuture);
		Object actual = resultFuture
				.get();

		//then
		assertEquals(RuntimeException.class, actual.getClass());
		assertEquals("Created from test", ((RuntimeException) actual).getMessage());
	}

	@Test
	public void specialHandlerShouldWorkForInstanceThatExtendRuntimeException() throws Exception {
		//given
		TestActorRef<ReverserActor> actorRef = TestActorRef.create(system, Props.create(ReverserActor.class));

		//when
		Future sFuture = ask(actorRef, new NullPointerException("Created from test"), TIMEOUT_MILLIS);
		CompletableFuture<Object> resultFuture = (CompletableFuture<Object>)toJava(sFuture);
		Object actual = resultFuture.get();

		//then
		assertEquals(NullPointerException.class, actual.getClass());
		assertEquals("Created from test", ((NullPointerException) actual).getMessage());
	}
}