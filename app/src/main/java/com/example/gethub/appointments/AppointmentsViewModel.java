// File: com.example.gethub.appointments.AppointmentsViewModel.java (FINAL MVVM FIX)
package com.example.gethub.appointments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.gethub.data.DataRepository;
import com.example.gethub.models.Appointment;
import com.example.gethub.models.RequestTicket;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class AppointmentsViewModel extends ViewModel {

    // MutableLiveData is used internally for state modification
    private final MutableLiveData<List<Appointment>> _appointmentList = new MutableLiveData<>(Collections.emptyList());
    // LiveData is exposed publicly (read-only)
    public LiveData<List<Appointment>> getAppointmentList() { return _appointmentList; }

    // Status to communicate back to the UI (e.g., ScheduleActivity)
    private final MutableLiveData<String> _scheduleStatus = new MutableLiveData<>();
    public LiveData<String> getScheduleStatus() { return _scheduleStatus; }

    /**
     * Public method to clear the scheduling status after the View has consumed it.
     * This adheres to MVVM by keeping state modification within the ViewModel.
     */
    public void clearScheduleStatus() {
        _scheduleStatus.setValue(null);
    }

    public void loadAppointments(String studentId) {
        if (studentId == null || studentId.isEmpty()) return;

        List<Appointment> appointments = DataRepository.getAppointmentsByStudentId(studentId);
        _appointmentList.setValue(appointments);
    }

    /**
     * Handles initial scheduling or rescheduling of an appointment.
     * Business Rule: Reschedule limit is 1.
     */
    public void scheduleOrRescheduleAppointment(String ticketId, long newScheduledTimestamp) {
        RequestTicket ticket = DataRepository.getTicketById(ticketId);

        if (ticket == null) {
            _scheduleStatus.setValue("ERROR: Ticket not found.");
            return;
        }

        Appointment existingAppointment = ticket.getAppointmentId() != null
                ? DataRepository.getAppointmentById(ticket.getAppointmentId())
                : null;

        if (existingAppointment != null) {
            // --- RESCHEDULE LOGIC (Business Rule Enforcement) ---
            if (existingAppointment.getRescheduleCount() >= 1) {
                _scheduleStatus.setValue("FAILED: Reschedule limit reached (Max 1 reschedule).");
                return;
            }

            // Update existing appointment
            existingAppointment.setScheduledDate(newScheduledTimestamp);
            existingAppointment.setRescheduleCount(existingAppointment.getRescheduleCount() + 1);
            existingAppointment.setStatus("Pending");

            DataRepository.addAppointment(existingAppointment);

            _scheduleStatus.setValue("SUCCESS: Appointment rescheduled.");

        } else {
            // --- INITIAL SCHEDULE LOGIC (Auto-Creation on Approved status) ---
            String newAppointmentId = generateAppointmentId();
            Appointment newAppointment = new Appointment(
                    newAppointmentId,
                    ticket.getStudentId(),
                    ticketId,
                    newScheduledTimestamp);

            // Link appointment to ticket and set status
            ticket.setAppointmentId(newAppointmentId);
            // Ticket status stays "Approved" until physical pickup/completion

            DataRepository.addAppointment(newAppointment);
            DataRepository.addRequestTicket(ticket);

            _scheduleStatus.setValue("SUCCESS: Appointment scheduled.");
        }

        loadAppointments(ticket.getStudentId());
    }

    // Helper for Mock ID Generation (Simulation)
    private static final AtomicLong APPT_COUNTER = new AtomicLong(100);
    private String generateAppointmentId() {
        return "APPT-" + System.currentTimeMillis() + "-" + APPT_COUNTER.incrementAndGet();
    }
}