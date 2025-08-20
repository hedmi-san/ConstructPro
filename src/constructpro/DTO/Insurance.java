package constructpro.DTO;

import java.time.LocalDate;
import java.util.List;


public class Insurance {
    private int id;
    private String status;//active, non active , pending 
    private String agencyName;
    private String insuranceNumber;
    private LocalDate startDate,endDate;
    private int workerId; 
    private List<String> insuranceDocuments;
    /*
        - Act de Naissance
        - Fiche familiale de l'etat civil
        - Photocopie de la carte identite
        - Photocopie de cheque
    */
    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }
    
    public void setInsuranceNumber(String insuranceNumber){
        this.insuranceNumber = insuranceNumber;
    }
    
    public String getInsuranceNumber(){
        return insuranceNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public int getWorkerID() {
        return workerId;
    }

    public void setWorkerID(int id) {
        this.workerId = id;
    }

    public List<String> getInsuranceDocuments() {
        return insuranceDocuments;
    }

    public void setInsuranceDocuments(List<String> insuranceDocuments) {
        this.insuranceDocuments = insuranceDocuments;
    }
}
