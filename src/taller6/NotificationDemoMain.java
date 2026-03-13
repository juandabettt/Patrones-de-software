package taller6;

public class NotificationDemoMain {

    public static void main(String[] args) {
        NotificationChannel emailChannel = new EmailChannel();
        NotificationChannel smsChannel = new SmsChannel();

        ExternalPushService externalPushService = new ExternalPushService();
        NotificationChannel pushChannel = new PushChannelAdapter(externalPushService, "System Notification");

        Notification urgentEmail = new UrgentNotification(emailChannel);
        Notification infoSms = new InformationalNotification(smsChannel);
        Notification urgentPush = new UrgentNotification(pushChannel);

        urgentEmail.send("alice@example.com", "Your account password was changed.");
        infoSms.send("+123456789", "Your package has been shipped.");
        urgentPush.send("device-xyz-123", "Server CPU usage is above 90%.");
    }
}

