// File: com.example.gethub.home.HomeViewModel.java
package com.example.gethub.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gethub.data.DataRepository;
import com.example.gethub.models.Notification;
import com.example.gethub.models.RequestTicket;
import com.example.gethub.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {

    // LiveData for the main UI components
    private final MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> getUser() { return _user; }
    public void setUser(User user) {
        this._user.setValue(user);
    }

    private final MutableLiveData<Integer> _totalRequests = new MutableLiveData<>(0);
    public LiveData<Integer> getTotalRequests() { return _totalRequests; }

    private final MutableLiveData<Integer> _pendingRequests = new MutableLiveData<>(0);
    public LiveData<Integer> getPendingRequests() { return _pendingRequests; }

    private final MutableLiveData<Integer> _approvedRequests = new MutableLiveData<>(0);
    public LiveData<Integer> getApprovedRequests() { return _approvedRequests; }

    private final MutableLiveData<Integer> _completedRequests = new MutableLiveData<>(0);
    public LiveData<Integer> getCompletedRequests() { return _completedRequests; }

    private final MutableLiveData<List<Notification>> _notifications = new MutableLiveData<>();
    public LiveData<List<Notification>> getNotifications() { return _notifications; }

    private final MutableLiveData<User> currentUser = new MutableLiveData<>();

    // --- ADD THIS METHOD ---

    /**
     * Initializes the ViewModel by fetching all necessary dashboard data for the user.
     * @param studentId The ID of the logged-in student.
     */
    public void loadDashboardData(String studentId) {
        // 1. Fetch User Data
        User user = DataRepository.getUserByStudentId(studentId);
        _user.setValue(user);

        // 2. Fetch Tickets and calculate status counts
        List<RequestTicket> tickets = DataRepository.getTicketsByStudentId(studentId);

        _totalRequests.setValue(tickets.size());

        // Calculate counts by status
        int pending = 0;
        int approved = 0;
        int completed = 0;

        for (RequestTicket ticket : tickets) {
            String status = ticket.getStatus();
            if ("Processing".equals(status)) {
                pending++;
            } else if ("Approved".equals(status)) {
                approved++;
            } else if ("Completed".equals(status)) {
                completed++;
            }
        }

        _pendingRequests.setValue(pending);
        _approvedRequests.setValue(approved);
        _completedRequests.setValue(completed);

        // 3. Fetch Notifications
        List<Notification> allNotifications = DataRepository.getNotificationsByStudentId(studentId);
        // Take the top 5 for the dashboard view, sorted descending by time (already done in Repository)
        List<Notification> recentNotifications = allNotifications.stream()
                .limit(5)
                .collect(Collectors.toList());

        _notifications.setValue(recentNotifications);
    }
}