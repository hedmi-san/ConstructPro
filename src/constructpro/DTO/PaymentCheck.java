package constructpro.DTO;

import java.time.LocalDate;

public class PaymentCheck {
    private int id;
    private int salaryrecordId;
    private int siteId;
    private LocalDate paymentDate;
    private double paidAmount;
    private double baseSalary;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalaryrecordId() {
        return salaryrecordId;
    }

    public void setSalaryrecordId(int salaryrecordId) {
        this.salaryrecordId = salaryrecordId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
    
    public LocalDate getPaymentDay() {
        return paymentDate;
    }

    public void setPaymentDay(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }
    
    public double getRetainedThisPayment() {
        return baseSalary - paidAmount;
    }
}
