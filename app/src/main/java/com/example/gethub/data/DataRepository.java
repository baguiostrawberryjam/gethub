// File: com.example.gethub.data.DataRepository.java (FINALIZED FIX)
package com.example.gethub.data;

import com.example.gethub.models.Appointment;
import com.example.gethub.models.Notification;
import com.example.gethub.models.ProfileSettings;
import com.example.gethub.models.RequestTicket;
import com.example.gethub.models.SystemDocument;
import com.example.gethub.models.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataRepository {

    // --- Core Data Stores ---
    private static final Map<String, User> userStore = new HashMap<>();
    private static final Map<String, SystemDocument> documentStore = new HashMap<>();
    private static final Map<String, RequestTicket> ticketStore = new HashMap<>();
    private static final Map<String, Appointment> appointmentStore = new HashMap<>();
    private static final Map<String, Notification> notificationStore = new HashMap<>();
    private static final Map<String, ProfileSettings> settingsStore = new HashMap<>();

    // --- Static Initializer Block (Populating the Mock Database) ---
    static {
        // 1. Mock Users
        String studentId = "1234567890";
        User defaultUser = new User();
        defaultUser.setStudentId(studentId);
        defaultUser.setPassword("Password123!");
        defaultUser.setFirstName("Juan");
        defaultUser.setLastName("Dela Cruz");
        defaultUser.setCollege("College of Engineering");
        defaultUser.setCourseProgram("Bachelor of Science in Computer Engineering");
        userStore.put(studentId, defaultUser);

        // 2. Mock Profile Settings
        ProfileSettings defaultSettings = new ProfileSettings(studentId);
        defaultSettings.setProfilePictureUrl("https://placehold.co/100x100/38bdf8/ffffff?text=JD");
        defaultSettings.setTwoFactorEnabled(false);
        settingsStore.put(studentId, defaultSettings);

        // 3. Mock System Documents (Complete List)
        // Digital & Pick-Up (Both)
        SystemDocument cor = new SystemDocument("COR", "Certificate of Registration (COR, per semester)", "Instant", 0.00, "Both", true);
        SystemDocument cog = new SystemDocument("COG", "Certificate of Grades (per semester)", "Instant", 0.00, "Both", true);
        SystemDocument tor = new SystemDocument("TOR", "Transcript of Records", "5 business days", 100.00, "Both", false);
        SystemDocument cograd = new SystemDocument("COGRAD", "Certificate of Graduation / Completion", "2 business days", 50.00, "Both", false);
        SystemDocument goodMoral = new SystemDocument("GM", "Good Moral Certificate", "1 business day", 30.00, "Both", false);

        // Pick-Up Only
        SystemDocument diploma = new SystemDocument("DIPLOMA", "Diploma (original/duplicate)", "10 business days", 200.00, "Pick-up Only", false);
        SystemDocument hd = new SystemDocument("HD", "Honorable Dismissal / Transfer Credentials", "5 business days", 100.00, "Pick-up Only", false);
        SystemDocument clearance = new SystemDocument("CLEARANCE", "Student Clearance", "3 business days", 50.00, "Pick-up Only", false);
        SystemDocument form137 = new SystemDocument("F137", "Form-137", "5 business days", 50.00, "Pick-up Only", false);

        // Add all documents to the store
        documentStore.put(cor.getDocCode(), cor);
        documentStore.put(cog.getDocCode(), cog);
        documentStore.put(tor.getDocCode(), tor);
        documentStore.put(cograd.getDocCode(), cograd);
        documentStore.put(goodMoral.getDocCode(), goodMoral);
        documentStore.put(diploma.getDocCode(), diploma);
        documentStore.put(hd.getDocCode(), hd);
        documentStore.put(clearance.getDocCode(), clearance);
        documentStore.put(form137.getDocCode(), form137);

        // 4. Mock Appointments
        String mockAppointmentId = "APPT-2025-001";
        Long tomorrow = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        Appointment mockAppointment = new Appointment(mockAppointmentId, studentId, null, tomorrow);
        appointmentStore.put(mockAppointmentId, mockAppointment);

        /* 5. Mock Request Tickets
        String ticketId1 = "TR-2025-001";
        RequestTicket ticket1 = new RequestTicket(ticketId1, studentId, tor.getDocName(), "Pick-up", tor.getServiceFee(), tor.isInstant());
        ticket1.setStatus("Approved");
        ticket1.setAppointmentId(mockAppointmentId);
        mockAppointment.setTicketId(ticketId1);
        ticketStore.put(ticketId1, ticket1);

        // FIX: Replaced 'certEnrollment' with 'cor' for the digital completed ticket
        String ticketId2 = "TR-2025-002";
        RequestTicket ticket2 = new RequestTicket(ticketId2, studentId, cor.getDocName(), "Digital", cor.getServiceFee(), cor.isInstant());
        ticket2.setStatus("Completed");
        ticket2.setCompletionDate(System.currentTimeMillis() - 86400000);
        ticketStore.put(ticketId2, ticket2);

        // 6. Mock Notifications
        Notification notif1 = new Notification(
                "N-2025-001", studentId,
                "Your request " + ticketId1 + " has been Approved!",
                ticketId1, "STATUS_UPDATE");
        Notification notif2 = new Notification(
                "N-2025-002", studentId,
                "Your request " + ticketId2 + " is Completed and the file is ready for download.",
                ticketId2, "STATUS_UPDATE");

        notif1.setRead(true);

        notificationStore.put(notif1.getNotificationId(), notif1);
        notificationStore.put(notif2.getNotificationId(), notif2);
         */

    }

    // --- Public Access Methods: Users ---
    public static User getUserByStudentId(String studentId) {
        return userStore.get(studentId);
    }

    public static void registerNewUser(User user) {
        userStore.put(user.getStudentId(), user);
        settingsStore.put(user.getStudentId(), new ProfileSettings(user.getStudentId()));
    }

    // --- Public Access Methods: Profile Settings (NEW METHODS) ---
    public static ProfileSettings getProfileSettingsByStudentId(String studentId) {
        return settingsStore.get(studentId);
    }

    public static void updateProfileSettings(ProfileSettings settings) {
        settingsStore.put(settings.getStudentId(), settings);
    }

    // --- Public Access Methods: Documents ---
    public static List<SystemDocument> getSystemDocuments() {
        return new ArrayList<>(documentStore.values());
    }

    public static SystemDocument getSystemDocumentByCode(String docCode) {
        return documentStore.get(docCode);
    }

    // --- Public Access Methods: Tickets ---
    public static void addRequestTicket(RequestTicket ticket) {
        ticketStore.put(ticket.getTicketId(), ticket);
    }

    public static List<RequestTicket> getTicketsByStudentId(String studentId) {
        return ticketStore.values().stream()
                .filter(ticket -> ticket.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public static RequestTicket getTicketById(String ticketId) {
        return ticketStore.get(ticketId);
    }

    // --- Public Access Methods: Appointments ---
    public static void addAppointment(Appointment appointment) {
        appointmentStore.put(appointment.getAppointmentId(), appointment);
    }

    public static List<Appointment> getAppointmentsByStudentId(String studentId) {
        return appointmentStore.values().stream()
                .filter(appointment -> appointment.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public static Appointment getAppointmentById(String appointmentId) {
        return appointmentStore.get(appointmentId);
    }

    // --- Public Access Methods: Notifications ---
    public static void addNotification(Notification notification) {
        notificationStore.put(notification.getNotificationId(), notification);
    }

    public static List<Notification> getNotificationsByStudentId(String studentId) {
        return notificationStore.values().stream()
                .filter(notification -> notification.getStudentId().equals(studentId))
                .sorted((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()))
                .collect(Collectors.toList());
    }

    public static Notification getNotificationById(String notificationId) {
        return notificationStore.get(notificationId);
    }
}