package constructpro.DTO;

import java.time.LocalDate;


public class Tool {
    private int Id;
    private String siteName;
    private String name;
    private LocalDate purshaceDate;
    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }
    
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPurshaceDate() {
        return purshaceDate;
    }

    public void setPurshaceDate(LocalDate purshaceDate) {
        this.purshaceDate = purshaceDate;
    }
  
}
