package com.example.skillup.dto;

  public  class UserStatsDto {
        private long totalUsers;
        private long studentCount;
        private long instructorCount;
        
        // Getters and setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        public long getStudentCount() { return studentCount; }
        public void setStudentCount(long studentCount) { this.studentCount = studentCount; }
        public long getInstructorCount() { return instructorCount; }
        public void setInstructorCount(long instructorCount) { this.instructorCount = instructorCount; }
    }
