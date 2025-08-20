package constructpro.DTO;

import java.util.List;
import java.time.LocalDate;

public class ConstructionSite {
    private int Id;
    private String name;
    private String location;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Worker> workers;
    private List<Vehicle> vehicles;
    private List<Material> materials;
    private List<Tool> toolsList;
    private List<Bill> siteBills;
    private double totalCost;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getStatus(){
        return status;
    }
    
    public void setStatus(String status){
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public List<Tool> getEquipmentList() {
        return toolsList;
    }

    public void setEquipmentList(List<Tool> equipmentList) {
        this.toolsList = equipmentList;
    }

    public List<Bill> getSiteBills() {
        return siteBills;
    }

    public void setSiteBills(List<Bill> siteBills) {
        this.siteBills = siteBills;
    }
    
    public List<Tool> getToolsList() {
        return toolsList;
    }

    public void setToolsList(List<Tool> toolsList) {
        this.toolsList = toolsList;
    }
    
    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }
}
