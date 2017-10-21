package ua.dp.akka.messages;

import java.io.Serializable;

public class DeleteRequest implements Serializable {
    public final String key;

    public DeleteRequest(String key) {
        this.key = key;
    }
}
