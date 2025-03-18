package org.itmo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


class Message {
    public String data;
    public String passcode;
    public boolean deleted;
    public Message(String data, String passcode) {
        this.data = data;
        this.passcode = passcode;
        this.deleted = false;
    }
    public void delete() {deleted = true;}
}
record UserData(ArrayList<Message> messages){};

class SpyMessenger {
    final HashMap<String, UserData> data = new HashMap<>();

    void sendMessage(String sender, String receiver, String message, String passcode) {
        if (!data.containsKey(receiver)) {
            data.put(receiver, new UserData(new ArrayList<>()));
        }

        final var messages = data.get(receiver).messages();
        final var msg = new Message(message, passcode);
        final Thread t = new Thread(() -> {
            try {
                Thread.sleep(1500);
                msg.delete();
            } catch (InterruptedException ie)  {
                throw new RuntimeException(ie);
            };
        });
        t.start();
        if (messages.size() >= 5) {
            messages.removeFirst();
        }
        messages.add(msg);
    }

    String readMessage(String user, String passcode) {
        if (!data.containsKey(user)) {
            return null;
        }
        final var messages = data.get(user).messages();
        for (final var msg : messages) {
            if (msg.passcode.equals(passcode) && !msg.deleted) {
                return msg.data;
            }
        }
        return null;
    }
}