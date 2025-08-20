package constructpro.DTO;
import java.time.LocalDate;

public class PayrollPeriod {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate paymentDate;
    private String periodType; // "FIRST_HALF" or "SECOND_HALF"
    private boolean isProcessed;
    private boolean isPaid;
    
    // Constructors
    public PayrollPeriod() {}
    
    public PayrollPeriod(LocalDate startDate, LocalDate endDate, String periodType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodType = periodType;
        this.isProcessed = false;
        this.isPaid = false;
        
        // Payment date is typically 1-2 days after period ends
        this.paymentDate = endDate.plusDays(1);
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }
    
    public boolean isProcessed() { return isProcessed; }
    public void setProcessed(boolean processed) { isProcessed = processed; }
    
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
}