package constructpro.DTO;

public class WorkerBalance {
    private int id;
    private int workerId;
    private double totalEarned; // All-time total earned
    private double totalPaid; // All-time total paid
    private double totalRetained; // All-time total retained (pending balance)
    private double finalPayoutAmount; // Amount paid at project completion
    private boolean isFinalPayoutMade;
    
    // Constructors
    public WorkerBalance() {}
    
    public WorkerBalance(int workerId) {
        this.workerId = workerId;
        this.totalEarned = 0.0;
        this.totalPaid = 0.0;
        this.totalRetained = 0.0;
        this.finalPayoutAmount = 0.0;
        this.isFinalPayoutMade = false;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getWorkerId() { return workerId; }
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    
    public double getTotalEarned() { return totalEarned; }
    public void setTotalEarned(double totalEarned) { this.totalEarned = totalEarned; }
    
    public double getTotalPaid() { return totalPaid; }
    public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }
    
    public double getTotalRetained() { return totalRetained; }
    public void setTotalRetained(double totalRetained) { this.totalRetained = totalRetained; }
    
    public double getFinalPayoutAmount() { return finalPayoutAmount; }
    public void setFinalPayoutAmount(double finalPayoutAmount) { this.finalPayoutAmount = finalPayoutAmount; }
    
    public boolean isFinalPayoutMade() { return isFinalPayoutMade; }
    public void setFinalPayoutMade(boolean finalPayoutMade) { isFinalPayoutMade = finalPayoutMade; }
    
    // Helper methods
    public double getPendingBalance() {
        return totalRetained - finalPayoutAmount;
    }
    
    public void addPayment(double earned, double paid) {
        this.totalEarned += earned;
        this.totalPaid += paid;
        this.totalRetained += (earned - paid);
    }
}