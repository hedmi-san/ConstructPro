package constructpro.DTO;

import java.util.List;

public class Fournisseur {
    private int Id;
    private String name;
    private String address;
    private String phone;
    private String type;
    private List<Integer> billsID;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getBillsID() {
        return billsID;
    }

    public void setBillsID(List<Integer> billsID) {
        this.billsID = billsID;
    }
    
    
}
