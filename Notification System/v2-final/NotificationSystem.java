import java.util.*;
import java.time.Instant;

interface UserRepository {
    User getUser(String userId);
}

class InMemoryUserRepository implements UserRepository {
    private Map<String, User> users = new HashMap<>();

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

    public Inbox() {
        this.notifications = new ArrayList<>();
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }
}

interface NotificationService {
    void sendNotification(Notification notification);
}

class EmailNotificationService implements NotificationService {
    private UserRepository userRepository;

    public EmailNotificationService(UserRepository repo) {
        this.userRepository = repo;
    }

    public void sendNotification(Notification notification) {
        User user = userRepository.getUser(notification.getUserId());
        if (user.getPreferences().isEmailNotificationsEnabled()) {
            System.out.println("Sending email notification: " + notification.getTitle());
        }
    }
}

class PushNotificationService implements NotificationService {
    private UserRepository userRepository;

    public PushNotificationService(UserRepository repo) {
        this.userRepository = repo;
    }

    public void sendNotification(Notification notification) {
        User user = userRepository.getUser(notification.getUserId());
        if (user.getPreferences().isPushNotificationsEnabled()) {
            System.out.println("Sending push notification: " + notification.getTitle());
        }
    }
}

class SmsNotificationService implements NotificationService {
    private UserRepository userRepository;

    public SmsNotificationService(UserRepository repo) {
        this.userRepository = repo;
    }

    public void sendNotification(Notification notification) {
        User user = userRepository.getUser(notification.getUserId());
        if (user.getPreferences().isSmsNotificationsEnabled()) {
            System.out.println("Sending SMS notification: " + notification.getTitle());
        }
    }
}

class Dispatcher {
    private List<NotificationService> notificationServices;

    public Dispatcher(List<NotificationService> services) {
        this.notificationServices = services;
    }

    public void dispatch(Notification notification) {
        for (NotificationService service : notificationServices) {
            service.sendNotification(notification);
        }
    }
}

class NotificationSender {
    private Dispatcher dispatcher;
    private UserRepository userRepository;

    public NotificationSender(Dispatcher dispatcher, UserRepository userRepository) {
        this.dispatcher = dispatcher;
        this.userRepository = userRepository;
    }

    public void send(Notification notification) {
        User user = userRepository.getUser(notification.getUserId());
        user.getInbox().addNotification(notification);
        dispatcher.dispatch(notification);
    }
}

class Notification {
    private String title;
    private String message;
    private String iconUrl;
    private Instant timestamp;
    private String userId;

    public Notification(String title, String message, String iconUrl, String userId) {
        this.title = title;
        this.message = message;
        this.iconUrl = iconUrl;
        this.userId = userId;
        this.timestamp = Instant.now();
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
        // Create user repository
        InMemoryUserRepository userRepo = new InMemoryUserRepository();

        // Setup notification services
        NotificationService emailService = new EmailNotificationService(userRepo);
        NotificationService pushService = new PushNotificationService(userRepo);
        NotificationService smsService = new SmsNotificationService(userRepo);
        List<NotificationService> services = Arrays.asList(emailService, pushService, smsService);

        // Create dispatcher
        Dispatcher dispatcher = new Dispatcher(services);

        // Create sender
        NotificationSender sender = new NotificationSender(dispatcher, userRepo);

        // Create inbox
        Inbox inbox = new Inbox();

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
        userRepo.setUser(user);

        // Create notification
        Notification notif = new Notification(
                "⚠️ System Alert",
                "Your CPU is on fire.",
                "🔥",
                "u123"
        );
        // Send notification
        sender.send(notif);
    }
}
