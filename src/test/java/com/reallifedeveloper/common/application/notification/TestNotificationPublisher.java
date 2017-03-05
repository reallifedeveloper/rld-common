package com.reallifedeveloper.common.application.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestNotificationPublisher implements NotificationPublisher {

    private Map<String, List<Notification>> channels = new HashMap<>();

    @Override
    public void publish(List<Notification> notifications, String publicationChannel) {
        publicationChannel(publicationChannel).addAll(notifications);
    }

    public List<Notification> publishedNotifications(String publicationChannel) {
        return publicationChannel(publicationChannel);
    }

    private List<Notification> publicationChannel(String name) {
        List<Notification> notifications = channels.get(name);
        if (notifications == null) {
            notifications = new ArrayList<>();
            channels.put(name, notifications);
        }
        return notifications;
    }
}
