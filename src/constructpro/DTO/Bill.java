package constructpro.DTO;

import java.time.LocalDate;
import java.util.List;

public class Bill {
    private int Id;
    private int supplierID;
    private int siteID;
    private LocalDate billDate;
    private String billType;//there are two types (Tool, Material)
    private double amount;
    private boolean paid;//ch7al mdina ll fournisour (شحال سلكناه)
    private String description;
    private List<Tool> equipment;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public List<Tool> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Tool> equipment) {
        this.equipment = equipment;
    }
    
    public double getTotalCost() {
        return amount;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
