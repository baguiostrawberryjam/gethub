package com.example.gethub.data;

import com.example.gethub.models.SystemData;
import java.util.HashMap;
import java.util.Map;

// This simulates a database or a remote API call
public class SystemDataRepository {

    private static final Map<String, SystemData> systemDataStore = new HashMap<>();

    // Static initializer block to populate initial dummy data
    static {
        // Data for a registered user (e.g., studentId 1234567890)
        SystemData user1Data = new SystemData("1234567890");
        user1Data.addDocument("Transcript Request Form");
        user1Data.addDocument("Certificate of Enrollment");
        systemDataStore.put("1234567890", user1Data);

        // Data for the admin user
        SystemData adminData = new SystemData("admin");
        adminData.addDocument("Server Logs Report");
        systemDataStore.put("admin", adminData);
    }

    // Method to retrieve the specific SystemData object
    public static SystemData getSystemDataByStudentId(String studentId) {
        // Return the object if it exists, otherwise return a new empty one
        SystemData data = systemDataStore.get(studentId);
        return data != null ? data : new SystemData(studentId);
    }
}