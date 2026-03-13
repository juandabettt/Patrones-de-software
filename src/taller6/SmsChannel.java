package taller6;

public class SmsChannel implements NotificationChannel {

    @Override
    public void deliver(String recipient, String content) {
        System.out.println("Sending SMS to " + recipient + " with content: " + content);
    }
}

