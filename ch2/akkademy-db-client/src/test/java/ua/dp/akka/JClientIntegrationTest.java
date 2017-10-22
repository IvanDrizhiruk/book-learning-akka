package ua.dp.akka;

import java.util.concurrent.CompletableFuture;

import org.junit.Ignore;
import org.junit.Test;

import ua.dp.akka.JClient;

public class JClientIntegrationTest {
    JClient client = new JClient("127.0.0.1:2552");

    //Can be runned only when server has been started
    @Ignore
    @Test
    public void itShouldSetRecord() throws Exception {
        client.set("123", 123);
        Integer result = (Integer) ((CompletableFuture) client.get("123")).get();
        assert(result == 123);
    }
}
