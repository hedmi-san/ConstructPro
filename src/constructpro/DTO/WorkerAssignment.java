package constructpro.DTO;

import java.time.LocalDate;

public class WorkerAssignment {
    private int id;
    private int workerId;
    private int siteId;
    private LocalDate assignmentDate;
    private LocalDate unAssignmentDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public LocalDate getUnAssignmentDate() {
        return unAssignmentDate;
    }

    public void setUnAssignmentDate(LocalDate unAssignmentDate) {
        this.unAssignmentDate = unAssignmentDate;
    }
    
    
}
