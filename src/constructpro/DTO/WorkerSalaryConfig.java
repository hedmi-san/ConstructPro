package constructpro.DTO;

import java.time.LocalDate;

public class WorkerSalaryConfig {
    private int id;
    private int workerId;
    private double dailyRate;
    private double paymentPercentage; // What percentage they get paid (e.g., 70%)
    private LocalDate effectiveDate; // When this rate became effective
    private boolean isActive;
    
    // Constructors
    public WorkerSalaryConfig() {}
    
    public WorkerSalaryConfig(int workerId, double dailyRate, double paymentPercentage) {
        this.workerId = workerId;
        this.dailyRate = dailyRate;
        this.paymentPercentage = paymentPercentage;
        this.effectiveDate = LocalDate.now();
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getWorkerId() { return workerId; }
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    
    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }
    
    public double getPaymentPercentage() { return paymentPercentage; }
    public void setPaymentPercentage(double paymentPercentage) { this.paymentPercentage = paymentPercentage; }
    
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
