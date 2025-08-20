package constructpro.DTO;


public class Tool {
    private int Id;
    private ConstructionSite site;
    private String name;
    private String type;
    private String status;

    public ConstructionSite getSite() {
        return site;
    }

    public void setSite(ConstructionSite site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
