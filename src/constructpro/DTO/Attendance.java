package constructpro.DTO;

import java.time.LocalDate;

public class Attendance {
    private int id;
    private int workerId;
    private LocalDate attendanceDate;
    private boolean isPresent;
    private double hoursWorked; // For overtime or partial days
    private String Notes;
    // Constructors
    public Attendance() {}
    
    public Attendance(int workerId, LocalDate attendanceDate, boolean isPresent) {
        this.workerId = workerId;
        this.attendanceDate = attendanceDate;
        this.isPresent = isPresent;
        this.hoursWorked = isPresent ? 8.0 : 0.0; // Default 8 hours if present
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getWorkerId() { return workerId; }
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    
    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }
    
    public double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }

    public String getNotes() {return Notes;}
    public void setNotes(String Notes) {this.Notes = Notes;} 
}
