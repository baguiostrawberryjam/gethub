// File: com.example.gethub.requests.RequestsViewModel.java
package com.example.gethub.requests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gethub.data.DataRepository;
import com.example.gethub.models.RequestTicket;

import java.util.Collections;
import java.util.List;

public class RequestsViewModel extends ViewModel {

    private final MutableLiveData<List<RequestTicket>> _requestList = new MutableLiveData<>(Collections.emptyList());
    public LiveData<List<RequestTicket>> getRequestList() { return _requestList; }

    /**
     * Loads all request tickets for the logged-in user from the repository.
     * @param studentId The ID of the student.
     */
    public void loadRequests(String studentId) {
        if (studentId == null || studentId.isEmpty()) return;

        List<RequestTicket> tickets = DataRepository.getTicketsByStudentId(studentId);
        _requestList.setValue(tickets);
    }
}