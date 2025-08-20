package constructpro.DTO;

import java.time.LocalDate;

public class Worker {
    private int id;
    private String firstName, lastName, birthPlace, fatherName, motherName;
    private LocalDate birthDate, startDate, identityCardDate;              
    private String familySituation,identityCardNumber, accountNumber, phoneNumber;// account number : compte ccp
    private String role;
    private int assignedSiteID;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getAssignedSiteID() {
        return assignedSiteID;
    }

    public void setAssignedSiteID(int siteID) {
        this.assignedSiteID = siteID;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getIdentityCardDate() {
        return identityCardDate;
    }

    public void setIdentityCardDate(LocalDate identityCardDate) {
        this.identityCardDate = identityCardDate;
    }

    public String getFamilySituation() {
        return familySituation;
    }

    public void setFamilySituation(String familySituation) {
        this.familySituation = familySituation;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Worker getWorkerFromFields() {
    Worker w = new Worker();
    w.setFirstName(getFirstName());
    w.setLastName(getLastName());
    w.setFatherName(getFatherName());
    w.setMotherName(getMotherName());
    w.setBirthPlace(getBirthPlace());
    w.setBirthDate(getBirthDate());
    w.setStartDate(getStartDate());
    w.setIdentityCardNumber(getIdentityCardNumber());
    w.setIdentityCardDate(getIdentityCardDate());
    w.setPhoneNumber(getPhoneNumber());
    w.setRole(getRole());
    w.setAccountNumber(getAccountNumber());
    w.setFamilySituation(getFamilySituation());
    w.setAssignedSiteID(getAssignedSiteID());
    return w;
    }  
}