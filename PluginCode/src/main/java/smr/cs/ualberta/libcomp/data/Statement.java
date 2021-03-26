package smr.cs.ualberta.libcomp.data;

/**
 * The DataStatement class is a parent class for ImportStatement and DependencyStatement
 */
public class Statement {

    public Integer importLocation;
    public Integer importDomain;
    public Integer importLib;
    protected String domainName;
    private Boolean Enableddomain;

    public Integer getImportLocation() { return importLocation; }
    public Integer getImportDomain() { return importDomain; }
    public Integer getImportLib() { return importLib; }
    public String getDomainName() { return domainName; }
    public Boolean getEnableddomain() { return Enableddomain;}

    public void setImportLocation(Integer importLocation) { this.importLocation = importLocation; }
    public void setImportDomain(Integer importDomain) { this.importDomain = importDomain; }
    public void setImportLib(Integer importLib) { this.importLib = importLib; }
    public void setEnableddomain(Boolean enableddomain) { Enableddomain = enableddomain; }
    public void setDomainName(String domainName) { this.domainName = domainName; }
}
