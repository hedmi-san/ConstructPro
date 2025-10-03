package constructpro.DTO;

import java.time.LocalDate;


public class Tool {
    private int Id;
    private String siteName;
    private String name;
    private LocalDate purshaceDate;
    private double quantity;
    private double unitPrice;
    
    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }
    
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPurshaceDate() {
        return purshaceDate;
    }

    public void setPurshaceDate(LocalDate purshaceDate) {
        this.purshaceDate = purshaceDate;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
