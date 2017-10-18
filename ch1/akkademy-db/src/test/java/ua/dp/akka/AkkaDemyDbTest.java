package ua.dp.akka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import ua.dp.akka.messages.SetRequest;

public class AkkaDemyDbTest {

	private ActorSystem system = ActorSystem.create();;

	@Test
	public void itShouldPlaceKeyValueFromSetMessageIntoMap() {
		TestActorRef<AkkaDemyDb> actorRef = TestActorRef.create(system, Props.create(AkkaDemyDb.class));
		actorRef.tell(new SetRequest("key", "value"), ActorRef.noSender());

		AkkaDemyDb akkaDemyDb = actorRef.underlyingActor();
		assertEquals(akkaDemyDb.map.get("key"), "value");
	}

	@Test
	public void nothingShouldBeHappendIfUnprocessableMessageWillBeSend() {
		//given
		TestActorRef<AkkaDemyDb> actorRef = TestActorRef.create(system, Props.create(AkkaDemyDb.class));

		//when
		actorRef.tell(new SetRequestMock("key", "value"), ActorRef.noSender());
		Map<String, Object> actual = actorRef.underlyingActor().map;

		//then
		assertTrue(actual.isEmpty());
	}

	@Test
	public void messageInheritFromProcessibleShouldBeRegistered() {
		//given
		TestActorRef<AkkaDemyDb> actorRef = TestActorRef.create(system, Props.create(AkkaDemyDb.class));

		HashMap<String, String> expected = new HashMap<>();
		expected.put("key-ext", "value-ext");

		//when
		actorRef.tell(new SetRequestExtMock("key-ext", "value-ext"), ActorRef.noSender());
		Map<String, Object> actual = actorRef.underlyingActor().map;

		//then
		ReflectionAssert.assertReflectionEquals(expected, actual);
	}


	@Test
	public void fewMessageShouldBeProcessed() {
		//given
		TestActorRef<AkkaDemyDb> actorRef = TestActorRef.create(system, Props.create(AkkaDemyDb.class));

		HashMap<String, String> expected = new HashMap<>();
		expected.put("key1", "interesting value");
		expected.put("key2", "test value");

		//when
		actorRef.tell(new SetRequest("key1", "value"), ActorRef.noSender());
		actorRef.tell(new SetRequest("key2", "test value"), ActorRef.noSender());
		actorRef.tell(new SetRequest("key1", "interesting value"), ActorRef.noSender());
		Map<String, Object> actual = actorRef.underlyingActor().map;

		//then
		ReflectionAssert.assertReflectionEquals(expected, actual);
	}

	private class SetRequestMock {

		String key;
		Object value;

		public SetRequestMock(String key, Object value) {
			this.key = key;
			this.value = value;
		}
	}

	private class SetRequestExtMock extends SetRequest {

		public SetRequestExtMock(String key, Object value) {
			super(key, value);
		}
	}
}
