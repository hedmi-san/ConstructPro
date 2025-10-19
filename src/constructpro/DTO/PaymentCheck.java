package constructpro.DTO;

import java.time.LocalDate;

public class PaymentCheck {
    private int id;
    private int salaryrecordId;
    private LocalDate payementDay;
    private double paidAmount;
    private double salary;

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

    public LocalDate getPayementDay() {
        return payementDay;
    }

    public void setPayementDay(LocalDate payementDay) {
        this.payementDay = payementDay;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
    
}
