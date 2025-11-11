package com.example.gethub.auth;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolDataProvider {

    private static final Map<String, List<String>> collegeCoursesMap = new HashMap<>();

    static {
        // Initialize the map with all the college and course data
        collegeCoursesMap.put("College of Architecture and Fine Arts (CAFA)", Arrays.asList("Bachelor of Fine Arts Major in Visual Communication", "Bachelor of Landscape Architecture", "Bachelor of Science in Architecture"));
        collegeCoursesMap.put("College of Arts and Letters (CAL)", Arrays.asList("Bachelor of Arts in Broadcasting", "Bachelor of Arts in Journalism", "Bachelor of Performing Arts (Theater Track)", "Batsilyer ng Sining sa Malikhaing Pagsulat"));
        collegeCoursesMap.put("College of Business Education and Accountancy (CBEA)", Arrays.asList("Bachelor of Science in Accountancy", "Bachelor of Science in Business Administration Major in Business Economics", "Bachelor of Science in Business Administration Major in Financial Management", "Bachelor of Science in Business Administration Major in Marketing Management", "Bachelor of Science in Entrepreneurship"));
        collegeCoursesMap.put("College of Criminal Justice Education (CCJE)", Arrays.asList("Bachelor of Arts in Legal Management", "Bachelor of Science in Criminology"));
        collegeCoursesMap.put("College of Hospitality and Tourism Management (CHTM)", Arrays.asList("Bachelor of Science in Hospitality Management", "Bachelor of Science in Tourism Management"));
        collegeCoursesMap.put("College of Information and Communications Technology (CICT)", Arrays.asList("Bachelor of Library and Information Science", "Bachelor of Science in Information System", "Bachelor of Science in Information Technology"));
        collegeCoursesMap.put("College of Industrial Technology (CIT)", Arrays.asList("Bachelor of Industrial Technology with specialization in Automotive", "Bachelor of Industrial Technology with specialization in Computer", "Bachelor of Industrial Technology with specialization in Drafting", "Bachelor of Industrial Technology with specialization in Electrical", "Bachelor of Industrial Technology with specialization in Electronics & Communication Technology", "Bachelor of Industrial Technology with specialization in Electronics Technology", "Bachelor of Industrial Technology with specialization in Food Processing Technology", "Bachelor of Industrial Technology with specialization in Heating, Ventilation, Air Conditioning and Refrigeration Technology (HVACR)", "Bachelor of Industrial Technology with specialization in Mechanical", "Bachelor of Industrial Technology with specialization in Mechatronics Technology", "Bachelor of Industrial Technology with specialization in Welding Technology"));
        collegeCoursesMap.put("College of Law (CLaw)", Arrays.asList("Bachelor of Laws", "Juris Doctor"));
        collegeCoursesMap.put("College of Nursing (CN)", Collections.singletonList("Bachelor of Science in Nursing"));
        collegeCoursesMap.put("College of Engineering (COE)", Arrays.asList("Bachelor of Science in Civil Engineering", "Bachelor of Science in Computer Engineering", "Bachelor of Science in Electrical Engineering", "Bachelor of Science in Electronics Engineering", "Bachelor of Science in Industrial Engineering", "Bachelor of Science in Manufacturing Engineering", "Bachelor of Science in Mechanical Engineering", "Bachelor of Science in Mechatronics Engineering"));
        collegeCoursesMap.put("College of Education (COED)", Arrays.asList("Bachelor of Early Childhood Education", "Bachelor of Elementary Education", "Bachelor of Physical Education", "Bachelor of Secondary Education Major in English minor in Mandarin", "Bachelor of Secondary Education Major in Filipino", "Bachelor of Secondary Education Major in Mathematics", "Bachelor of Secondary Education Major in Sciences", "Bachelor of Secondary Education Major in Social Studies", "Bachelor of Secondary Education Major in Values Education", "Bachelor of Technology and Livelihood Education Major in Home Economics", "Bachelor of Technology and Livelihood Education Major in Industrial Arts", "Bachelor of Technology and Livelihood Education Major in Information and Communication Technology"));
        collegeCoursesMap.put("College of Science (CS)", Arrays.asList("Bachelor of Science in Biology", "Bachelor of Science in Environmental Science", "Bachelor of Science in Food Technology", "Bachelor of Science in Math with Specialization in Applied Statistics", "Bachelor of Science in Math with Specialization in Business Applications", "Bachelor of Science in Math with Specialization in Computer Science"));
        collegeCoursesMap.put("College of Sports, Exercise and Recreation (CSER)", Arrays.asList("Bachelor of Science in Exercise and Sports Sciences with specialization in Fitness and Sports Coaching", "Bachelor of Science in Exercise and Sports Sciences with specialization in Fitness and Sports Management", "Certificate of Physical Education"));
        collegeCoursesMap.put("College of Social Sciences and Philosophy (CSSP)", Arrays.asList("Bachelor of Public Administration", "Bachelor of Science in Psychology", "Bachelor of Science in Social Work"));
        collegeCoursesMap.put("Graduate School (GS)", Arrays.asList("Doctor of Education", "Doctor of Philosophy", "Doctor of Public Administration", "Master in Business Administration", "Master in Physical Education", "Master in Public Administration", "Master of Arts in Education", "Master of Engineering Program", "Master of Industrial Technology Management", "Master of Information Technology", "Master of Manufacturing Engineering", "Master of Science in Civil Engineering", "Master of Science in Computer Engineering", "Master of Science in Electronics and Communications Engineering"));
    }

    public static List<String> getCampusBranches() {
        return Arrays.asList("Select a Campus Branch", "Main Campus", "Bustos Campus", "Meneses Campus", "Sarmiento Campus", "Hagonoy Campus", "San Rafael Campus");
    }

    public static List<String> getColleges() {
        return Arrays.asList("Select a College", "College of Architecture and Fine Arts (CAFA)", "College of Arts and Letters (CAL)", "College of Business Education and Accountancy (CBEA)", "College of Criminal Justice Education (CCJE)", "College of Hospitality and Tourism Management (CHTM)", "College of Information and Communications Technology (CICT)", "College of Industrial Technology (CIT)", "College of Law (CLaw)", "College of Nursing (CN)", "College of Engineering (COE)", "College of Education (COED)", "College of Science (CS)", "College of Sports, Exercise and Recreation (CSER)", "College of Social Sciences and Philosophy (CSSP)", "Graduate School (GS)");
    }

    public static List<String> getCoursesForCollege(String college) {
        if (college != null && collegeCoursesMap.containsKey(college)) {
            return collegeCoursesMap.get(college);
        }
        return Collections.singletonList("Select a Course/Program");
    }
}
