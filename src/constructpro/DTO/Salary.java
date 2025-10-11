package constructpro.DTO;
import java.time.LocalDate;

public class Salary {
    private int id;
    private int workerId;
    private LocalDate paymentDate;
    private int daysWorked;
    private double dailyRate;
    private double totalEarned; // daysWorked * dailyRate (what they actually earned)
    private double amountPaid; // what they received (totalEarned * paymentPercentage)
    private double retainedAmount; // totalEarned - amountPaid (kept by company)
    private String notes;
    private boolean isPaid;
    
    // Constructors
    public Salary() {}
    
    public Salary(int workerId, int payrollPeriodId, int daysWorked, double dailyRate, double paymentPercentage) {
        this.workerId = workerId;
        this.daysWorked = daysWorked;
        this.dailyRate = dailyRate;
        this.totalEarned = daysWorked * dailyRate;
        this.amountPaid = totalEarned * (paymentPercentage / 100.0);
        this.retainedAmount = totalEarned - amountPaid;
        this.paymentDate = LocalDate.now();
        this.isPaid = false;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getWorkerId() { return workerId; }
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public double getDailyRate() { return dailyRate;}
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate;}

    public boolean isIsPaid() { return isPaid;}
    public void setIsPaid(boolean isPaid) { this.isPaid = isPaid;}
    
    public int getDaysWorked() { return daysWorked; }
    
    public double getTotalEarned() { return totalEarned; }
    public void setTotalEarned(double totalEarned) { this.totalEarned = totalEarned; }
    
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    
    public double getRetainedAmount() { return retainedAmount; }
    public void setRetainedAmount(double retainedAmount) { this.retainedAmount = retainedAmount; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
    
}