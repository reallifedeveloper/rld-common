/**
 * Code to notify other systems about domain events that have occurred in this system and that have
 * been saved in an {@link com.reallifedeveloper.common.application.eventstore.EventStore}.
 * <p>
 * The main class is {@link com.reallifedeveloper.common.application.notification.NotificationService},
 * which contains methods to find historical events
 * ({@link com.reallifedeveloper.common.application.notification.NotificationService#currentNotificationLog(int)} and
 * {@link com.reallifedeveloper.common.application.notification.NotificationService#notificationLog(NotificationLogId)})
 * and to publish notifications to a messaging system
 * ({@link com.reallifedeveloper.common.application.notification.NotificationService#publishNotifications(String)}).
 * <p>
 * The {@link com.reallifedeveloper.common.application.notification.Notification} class is used to hold
 * information about the events that have occurred. To work with notifications in other systems, a
 * {@link com.reallifedeveloper.common.application.notification.NotificationReader} can be used to read
 * event information without having access to the event classes themselves.
 *
 * @author RealLifeDeveloper
 */
package com.reallifedeveloper.common.application.notification;
