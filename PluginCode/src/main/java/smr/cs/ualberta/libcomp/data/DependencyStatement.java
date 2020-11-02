package smr.cs.ualberta.libcomp.data;

public class DependencyStatement {
    public DependencyStatement() {}

    public Integer importLocation;
    public Integer importDomain;
    public Integer importLib;
    public Integer fromlocation;
    public Integer tolocation;
    private String domainName;
    private Boolean Enableddomain;

    public Boolean getEnableddomain() { return Enableddomain;}
    public Integer getImportLocation() { return importLocation; }
    public Integer getImportDomain() { return importDomain; }
    public Integer getImportLib() { return importLib; }
    public String getDomainName() { return domainName; }
    public Integer getFromlocation() { return fromlocation; }
    public Integer getTolocation() { return tolocation; }

    public void setImportLocation(Integer importLocation) { this.importLocation = importLocation; }
    public void setImportDomain(Integer importDomain) { this.importDomain = importDomain; }
    public void setImportLib(Integer importLib) { this.importLib = importLib; }
    public void setFromlocation(Integer fromlocation) { this.fromlocation = fromlocation; }
    public void setTolocation(Integer tolocation) { this.tolocation = tolocation; }
    public void setDomainName(String domainName) { this.domainName = domainName; }
    public void setEnableddomain(Boolean enableddomain) { Enableddomain = enableddomain; }
}
