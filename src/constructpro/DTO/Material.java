package constructpro.DTO;

import java.time.LocalDate;

public class Material {
     private int Id;
    private int supplierId;
    private String materialType; // "soil", "concrete", "equipment"
    private double quantity;
    private double unitPrice;
    private LocalDate purchasedate;
    private int transportFee;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getTransportFee() {
        return transportFee;
    }

    public void setTransportFee(int transportFee) {
        this.transportFee = transportFee;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
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

    public LocalDate getPurchaseDate() {
        return purchasedate;
    }

    public void setPurchaseDate(LocalDate date) {
        this.purchasedate = date;
    }
    
    
}
