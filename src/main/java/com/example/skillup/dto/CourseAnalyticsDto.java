 package com.example.skillup.dto;

public class CourseAnalyticsDto {

     private int totalEnrollments;
        private int activeStudents;
        private int completedStudents;
        private double completionRate;
        
        // Getters and setters
        public int getTotalEnrollments() { return totalEnrollments; }
        public void setTotalEnrollments(int totalEnrollments) { this.totalEnrollments = totalEnrollments; }
        
        public int getActiveStudents() { return activeStudents; }
        public void setActiveStudents(int activeStudents) { this.activeStudents = activeStudents; }
        
        public int getCompletedStudents() { return completedStudents; }
        public void setCompletedStudents(int completedStudents) { this.completedStudents = completedStudents; }
        
        public double getCompletionRate() { return completionRate; }
        public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }
}