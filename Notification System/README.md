# Notification System

This project explores a low-level design (LLD) of a notification delivery system using core OOP principles like encapsulation, dependency injection, and modular architecture.

## 🎯 Objective

Design a system that:
- Stores user-specific notifications
- Delivers them over multiple channels (email, SMS, push)
- Respects per-user delivery preferences
- Uses extensible and testable object-oriented design

## ✅ What the First Design Did Well

- **Entity Modeling**: Users, preferences, inbox, and notifications were logically broken down  
- **Interface Use**: `NotificationService` abstraction introduced early  
- **Basic Dispatcher**: Centralized multi-channel delivery mechanism  
- **Preferences Logic**: Each service respected individual delivery toggles

## ❌ Issues in the First Design

1. **Singleton Abuse**  
   `UserManager` was a static singleton — poor lifecycle control, untestable.

2. **Hidden Side Effects**  
   `Inbox.addNotification()` internally triggered dispatching — mixed responsibilities.

3. **Tight Coupling**  
   Services hardcoded dependencies (e.g., accessed `UserManager` directly).

4. **No Dependency Injection**  
   Notification services pulled data instead of being fed dependencies.

| Problem                            | Why It’s a Problem                         |
|------------------------------------|--------------------------------------------|
| ❌ Singleton `UserManager`         | Global state, anti-pattern, untestable     |
| ❌ Dispatcher hidden inside Inbox  | Blurs responsibilities (violates SRP)      |
| ❌ Services fetched users directly | Breaks DI, unclear ownership               |
| ❌ No orchestration layer          | Dispatch was implicit; no clear coordinator|

## 🔧 How Final Design Solves It

1. **Replaced Singleton with Repository Interface**  
   Introduced `UserRepository` abstraction and `InMemoryUserRepository` implementation.

2. **Isolated Side Effects**  
   Added `NotificationSender` to explicitly handle inboxing and dispatching.

3. **Dependency Injection**  
   Services take `UserRepository` via constructor — fully testable and modular.

4. **Decoupled Dispatcher**  
   Dispatcher accepts a list of services externally; fully pluggable.

| Fix                         | Value in Interview Context                    |
|-----------------------------|-----------------------------------------------|
| ✅ `UserRepository`         | Decouples data access, supports mocking       |
| ✅ `NotificationSender`     | Introduces orchestration layer                |
| ✅ DI across services       | Clean ownership, easier to test               |
| ✅ Dispatcher as argument   | Pluggable, swappable logic, easier to extend  |
| ✅ Responsibility separation| Inbox stores; Dispatcher sends — no confusion |

## 📊 Interview-Oriented Design Rating

| Category                  | First Design | Final Design |
|---------------------------|--------------|--------------|
| Entity Modeling           | 8/10         | 8/10         |
| Separation of Concerns    | 5/10         | 9/10         |
| Object Relationships      | 6/10         | 9/10         |
| Extensibility             | 6/10         | 8/10         |
| Testability / Mockability | 4/10         | 9/10         |
| **Overall**               | **6.2/10**   | **8.6/10**   |

## 🧠 FAQs

### 🧨 How would you handle retries / failures?

Use a decorator (`RetryingNotificationService`) around any `NotificationService`.\
It retries failed sends with a fixed or exponential backoff.

```java
class RetryingNotificationService implements NotificationService {
    private final NotificationService delegate;
    private final int maxRetries;

    public boolean sendNotification(Notification notification) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                return delegate.sendNotification(notification);
            } catch (Exception e) {
                // Retry
            }
        }
        return false;
    }
}
```

### ⚖️ How to support partial delivery?

Each `NotificationService` is independent. One channel failing (e.g. email) won’t block others (e.g. SMS, push).  
Failures are isolated — no shared state — so partial delivery works out of the box.

Each service can internally log or retry on failure using a simple `try/catch`:

```java
public boolean sendNotification(Notification notification) {
    try {
        // Send logic
        return true;
    } catch (Exception e) {
        System.out.println("Failed to send via Email");
        return false;
    }
}
```

If tracking is required, we can later modify `Dispatcher.dispatch()` to return a `Map<Channel, Boolean>` result per notification.

### 🌀 What if preferences change mid-dispatch?
Take a snapshot of `Preferences` when creating the `Notification`.
Services read from the snapshot, not the current user state, avoiding mid-flight changes.

### 🌍 How to scale to millions of users?
Swap `InMemoryUserRepository` with a `DatabaseUserRepository`.
No changes to core logic --- interfaces ensure pluggability.

### ⏰ How to add future scheduling?
Add a `scheduledAt` field to `Notification`.
Use a `NotificationQueue` class to poll and dispatch when the time arrives.
