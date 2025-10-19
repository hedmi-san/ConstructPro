package constructpro.DTO;


public class SalaryRecord {
    private int id;
    private int workerId;   
    private double totalEarned;      // Total they SHOULD have received (cumulative)
    private double totalPaid;        // Total actually paid (cumulative)
    // No retainedAmount field - calculate it!
    // Getters and Setters
    public int getId() { 
        return id; 
    }
    public void setId(int id) {
        this.id = id; 
    }
    
    public int getWorkerId() { 
        return workerId; 
    }
    public void setWorkerId(int workerId) { 
        this.workerId = workerId;
    }

    public double getTotalEarned() {
        return totalEarned; 
    }
    public void setTotalEarned(double totalEarned) {
        this.totalEarned = totalEarned;
    }
    
    public double getAmountPaid() {
        return totalPaid; 
    }
    public void setAmountPaid(double amountPaid) {
        this.totalPaid = amountPaid;
    }
    
    public double getRetainedAmount() { 
    return totalEarned - totalPaid; 
    }
    
}