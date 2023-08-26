package com.example.server.entities;

public class ChatMessage {
    private String content;
    private String sender;
    private long timestamp;

    public ChatMessage(String content, String sender, long timestamp) {
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

//public class MessageStore {
//    private List<Message> messages;
//
//    public MessageStore() {
//        messages = new ArrayList<>();
//    }
//
//    public void addMessage(String content, String sender, long timestamp) {
//        Message message = new Message(content, sender, timestamp);
//        messages.add(message);
//    }
//
//    public List<Message> getMessagesAfterTimestamp(long timestamp) {
//        List<Message> result = new ArrayList<>();
//        for (Message message : messages) {
//            if (message.getTimestamp() > timestamp) {
//                result.add(message);
//            }
//        }
//        return result;
//    }
//
////    public static void main(String[] args) {
////        MessageStore messageStore = new MessageStore();
////
////        // Adding messages
////        messageStore.addMessage("Hello!", "Alice", System.currentTimeMillis());
////        messageStore.addMessage("Hi there!", "Bob", System.currentTimeMillis() + 1000);
////
////        // Retrieving messages after a given timestamp
////        long timestampToQuery = System.currentTimeMillis();
////        List<Message> messagesAfterTimestamp = messageStore.getMessagesAfterTimestamp(timestampToQuery);
////
////        // Display retrieved messages
////        for (Message message : messagesAfterTimestamp) {
////            System.out.println("Sender: " + message.getSender() +
////                    ", Content: " + message.getContent() +
////                    ", Timestamp: " + message.getTimestamp());
////        }
////    }
//}
