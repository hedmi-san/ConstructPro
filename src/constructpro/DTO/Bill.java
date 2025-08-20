package constructpro.DTO;

import java.time.LocalDate;
import java.util.List;

public class Bill {
    private int Id;
    private Fournisseur supplier;
    private LocalDate date;
    private double amount;
    private double transportationFee;
    private boolean paid;//ch7al mdina ll fournisour (شحال سلكناه)
    private String description;
    private List<Tool> equipment;

    public List<Tool> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Tool> equipment) {
        this.equipment = equipment;
    }
    
    public double getTotalCost() {
        return amount + transportationFee;
    }

    public Fournisseur getSupplier() {
        return supplier;
    }

    public void setSupplier(Fournisseur supplier) {
        this.supplier = supplier;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTransportationFee() {
        return transportationFee;
    }

    public void setTransportationFee(double transportationFee) {
        this.transportationFee = transportationFee;
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
