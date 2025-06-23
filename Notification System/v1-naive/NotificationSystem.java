import java.util.*;

class UserManager {
    private static UserManager instance = new UserManager();
    private Map<String, User> users;

    private UserManager() {
        this.users = new HashMap<>();
    }

    public static UserManager getInstance() {
        return instance;
    }

    public void setUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }
}

class User {
    private String userId;
    private String name;
    private String email;
    private Inbox inbox;
    private Preferences preferences;

    public User(String userId, String name, String email, Preferences preferences, Inbox inbox) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.preferences = preferences;
        this.inbox = inbox;
    }

    public String getUserId() {
        return userId;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public Inbox getInbox() {
        return inbox;
    }
}

class Preferences {
    private boolean emailNotificationsEnabled;
    private boolean pushNotificationsEnabled;
    private boolean smsNotificationsEnabled;

    public Preferences(boolean emailNotificationsEnabled, boolean pushNotificationsEnabled, boolean smsNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.pushNotificationsEnabled = pushNotificationsEnabled;
        this.smsNotificationsEnabled = smsNotificationsEnabled;
    }

    public boolean isEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public boolean isPushNotificationsEnabled() {
        return pushNotificationsEnabled;
    }

    public boolean isSmsNotificationsEnabled() {
        return smsNotificationsEnabled;
    }
}

class Inbox {
    private List<Notification> notifications;
    private Dispatcher dispatcher;

    public Inbox(Dispatcher dispatcher) {
        this.notifications = new ArrayList<>();
        this.dispatcher = dispatcher;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
        dispatcher.dispatch(notification);
    }
}

interface NotificationService {
    void sendNotification(Notification notification);
}

class EmailNotificationService implements NotificationService {
    public void sendNotification(Notification notification) {
        User user = UserManager.getInstance().getUser(notification.getUserId());
        if (user.getPreferences().isEmailNotificationsEnabled()) {
            System.out.println("Sending email notification: " + notification.getTitle());
        }
    }
}

class PushNotificationService implements NotificationService {
    public void sendNotification(Notification notification) {
        User user = UserManager.getInstance().getUser(notification.getUserId());
        if (user.getPreferences().isPushNotificationsEnabled()) {
            System.out.println("Sending push notification: " + notification.getTitle());
        }
    }
}

class SmsNotificationService implements NotificationService {
    public void sendNotification(Notification notification) {
        User user = UserManager.getInstance().getUser(notification.getUserId());
        if (user.getPreferences().isSmsNotificationsEnabled()) {
            System.out.println("Sending SMS notification: " + notification.getTitle());
        }
    }
}

class Dispatcher {
    private List<NotificationService> notificationServices;

    public Dispatcher() {
        this.notificationServices = new ArrayList<>();
        this.notificationServices.add(new EmailNotificationService());
        this.notificationServices.add(new PushNotificationService());
        this.notificationServices.add(new SmsNotificationService());
    }

    public void dispatch(Notification notification) {
        for (NotificationService service : notificationServices) {
            service.sendNotification(notification);
        }
    }
}

class Notification {
    private String title;
    private String message;
    private String iconUrl;
    private String timestamp;
    private String userId;

    public Notification(String title, String message, String iconUrl, String userId) {
        this.title = title;
        this.message = message;
        this.iconUrl = iconUrl;
        this.userId = userId;
        this.timestamp = new Date().toString();
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getUserId() {
        return userId;
    }
}

public class NotificationSystem {
    public static void main(String[] args) {
        // Create dispatcher
        Dispatcher dispatcher = new Dispatcher();

        // Create inbox (with dispatcher)
        Inbox inbox = new Inbox(dispatcher);

        // Set user preferences
        Preferences prefs = new Preferences(
                false,   // email
                false,  // push
                true   // sms
        );

        // Create user
        User user = new User(
                "u123",
                "Abhishek",
                "abhi@example.com",
                prefs,
                inbox
        );

        // Register user
        UserManager.getInstance().setUser(user);

        // Create notification
        Notification notif = new Notification(
                "⚠️ System Alert",
                "Your CPU is on fire.",
                "🔥",
                "u123"
        );

        // Add notification to inbox (which triggers dispatch)
        user.getInbox().addNotification(notif);
    }
}
