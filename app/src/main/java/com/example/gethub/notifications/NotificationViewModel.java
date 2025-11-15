// File: com.example.gethub.notifications.NotificationViewModel.java
package com.example.gethub.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gethub.data.DataRepository;
import com.example.gethub.models.Notification;

import java.util.Collections;
import java.util.List;

public class NotificationViewModel extends ViewModel {

    private final MutableLiveData<List<Notification>> _notifications = new MutableLiveData<>(Collections.emptyList());
    public LiveData<List<Notification>> getNotifications() { return _notifications; }

    private String currentStudentId;

    /**
     * Loads the notification history for the logged-in user.
     * @param studentId The ID of the student.
     */
    public void loadNotifications(String studentId) {
        if (studentId == null || studentId.isEmpty()) return;
        currentStudentId = studentId;

        // Fetch all notifications from the mock repository.
        List<Notification> notificationList = DataRepository.getNotificationsByStudentId(studentId);
        _notifications.setValue(notificationList);
    }

    /**
     * Simulates marking a notification as read in the mock database.
     * This method is local to the ViewModel state for UI updates.
     * @param notificationId The ID of the notification to mark.
     */
    public void markNotificationAsRead(String notificationId) {
        List<Notification> currentList = _notifications.getValue();
        if (currentList == null) return;

        for (Notification notification : currentList) {
            if (notification.getNotificationId().equals(notificationId)) {
                notification.setRead(true);
                break;
            }
        }
        // Trigger LiveData update manually to refresh the UI
        _notifications.setValue(currentList);
    }
}