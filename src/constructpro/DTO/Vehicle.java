package constructpro.DTO;

public class Vehicle {
    private int Id;
    private String name;
    private String plateNumber;
    private String status; //working, parkin , rented
    /*parking, working in our sites, rented to another company/person (duration of rent, person name, phone number, location)
    our company rent a machine (duration, phone number, cost) تحسب في مصاريف الشونطي الاجمالية  */
    private int siteID;
    private int driverID;//worker

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

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int id) {
        this.siteID = id;
    }

    public int getDriverID() {
        return driverID;
    }

    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }
    
}
