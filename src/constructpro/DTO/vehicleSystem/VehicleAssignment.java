package constructpro.DTO.vehicleSystem;

import java.time.LocalDate;

public class VehicleAssignment {
    private int id;
    private int vehicle_id;
    private int assignedSiteId;
    private LocalDate assignmentDate;
    private LocalDate unAssignmentDate;
    private double billableDays;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public int getAssignedSiteId() {
        return assignedSiteId;
    }

    public void setAssignedSiteId(int assignedSiteId) {
        this.assignedSiteId = assignedSiteId;
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

    public double getBillableDays() {
        return billableDays;
    }

    public void setBillableDays(double billableDays) {
        this.billableDays = billableDays;
    }

}
