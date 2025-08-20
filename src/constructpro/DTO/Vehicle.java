package constructpro.DTO;

public class Vehicle {
    private int Id;
    private String type;
    private String plateNumber;
    private String status;
    /*parking, working in our sites, rented to another company/person (duration of rent, person name, phone number, location)
    our company rent a machine (duration, phone number, cost) تحسب في مصاريف الشونطي الاجمالية  */
    private ConstructionSite site;
    private Worker driver;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ConstructionSite getSite() {
        return site;
    }

    public void setSite(ConstructionSite site) {
        this.site = site;
    }

    public Worker getDriver() {
        return driver;
    }

    public void setDriver(Worker driver) {
        this.driver = driver;
    }
    
}
