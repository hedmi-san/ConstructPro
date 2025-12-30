package constructpro.DTO;

import java.time.LocalDate;

public class FinancialTransaction {
    private int id;
    private int supplierId;
    private LocalDate paymentDate;
    private double amount;
    private String method;
    private String imagePath;

    public FinancialTransaction() {
    }

    public FinancialTransaction(int supplierId, LocalDate paymentDate, double amount, String method, String imagePath) {
        this.supplierId = supplierId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.method = method;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
