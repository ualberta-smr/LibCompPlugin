package smr.cs.ualberta.libcomp.data;

import com.intellij.psi.PsiImportStatementBase;

/**
 * The DataImportStatement class is an object to store information on an import statement
 */

public class ImportStatement {

    public PsiImportStatementBase importListBase;
    public Integer importLocation;
    public Integer importDomain;
    public Integer importLib;
    private String domainName;
    private Boolean Enableddomain;

    public ImportStatement() {
    }

    public PsiImportStatementBase getImportListBase() { return importListBase; }
    public Integer getImportLocation() { return importLocation; }
    public Integer getImportDomain() { return importDomain; }
    public Integer getImportLib() { return importLib; }
    public String getDomainName() { return domainName; }
    public Boolean getEnableddomain() { return Enableddomain; }


    public void setDomainName(String domainName) { this.domainName = "Libraries from the " + domainName + " domain"; }
    public void setImportListBase(PsiImportStatementBase importListBase) { this.importListBase = importListBase; }
    public void setImportLocation(Integer importLocation) {this.importLocation = importLocation; }
    public void setImportDomain(Integer importDomain) {this.importDomain = importDomain; }
    public void setImportLib(Integer importLib) { this.importLib = importLib; }
    public void setEnableddomain(Boolean enableddomain) { Enableddomain = enableddomain; }
}
