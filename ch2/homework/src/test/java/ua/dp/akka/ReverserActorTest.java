package ua.dp.akka;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.assertEquals;
import static scala.compat.java8.FutureConverters.toJava;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import scala.concurrent.Future;

public class ReverserActorTest {

	public static final int TIMEOUT_MILLIS = 100;
	ActorSystem system = ActorSystem.create();

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
//
//	@Test
//	public void actorShouldReturnExceptionIfUnknownMessageWasSend() throws Exception {
//		//given
//		TestActorRef<ReverserActor> actorRef = TestActorRef.create(system, Props.create(ReverserActor.class));
//
//		//when
//		Future sFuture = ask(actorRef, new StringBuilder("Test message"), TIMEOUT_MILLIS);
//		CompletableFuture<String> resultFuture = (CompletableFuture<String>)toJava(sFuture);
////		resultFuture.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
//
//		//then exception
//
//		resultFuture
//				.handle((x, t) -> {
//					System.out.println("==> " + x + " === " + t);
//					if (t != null) {
//						assertEquals(IllegalArgumentException.class, t.getClass());
//						assertEquals("Test", t.getMessage());
//					}
//					fail("Exception should happend");
//
//					return null;
//				});
//
//		Thread.sleep(100);
//	}
}