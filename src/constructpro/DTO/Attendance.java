package constructpro.DTO;

import java.time.LocalDate;

public class Attendance {
    private int id;
    private int workerId;
    private LocalDate attendanceDate;
    private boolean isPresent;
    private String Notes;
    // Constructors
    public Attendance() {}
    
    public Attendance(int workerId, LocalDate attendanceDate, boolean isPresent) {
        this.workerId = workerId;
        this.attendanceDate = attendanceDate;
        this.isPresent = isPresent;
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

    public String getNotes() {return Notes;}
    public void setNotes(String Notes) {this.Notes = Notes;} 
}
