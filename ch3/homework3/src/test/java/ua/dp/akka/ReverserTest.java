package ua.dp.akka;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class ReverserTest {

    private static final int TIMEOUT_MILLIS = 100;

    @Test
    public void reversedStringShouldBeReturned() throws Exception {
        //given
        String message = "Test message";
        String expected = "egassem tseT";

        //when
        String actual = new Reverser().revers(message, TIMEOUT_MILLIS);

        //then
        assertEquals(expected, actual);
    }

    @Test(expected = ExecutionException.class)
    public void exceptionShouldBeThrowen() throws Exception {

        //when
        new Reverser().reversEny(new StringBuilder("Message"), TIMEOUT_MILLIS);

        //then exception
    }
}