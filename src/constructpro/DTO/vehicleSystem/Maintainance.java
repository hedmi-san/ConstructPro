package constructpro.DTO.vehicleSystem;

import java.time.LocalDate;

public class Maintainance {
    private int id;
    private int vehicle_id;
    private int assignedSiteId;
    private String maintainanceType;
    private LocalDate repair_date;
    private double repairCost;

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

    public String getMaintainanceType() {
        return maintainanceType;
    }

    public void setMaintainanceType(String maintainanceType) {
        this.maintainanceType = maintainanceType;
    }

    public LocalDate getRepair_date() {
        return repair_date;
    }

    public void setRepair_date(LocalDate repair_date) {
        this.repair_date = repair_date;
    }

    public double getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(double repairCost) {
        this.repairCost = repairCost;
    }

}
