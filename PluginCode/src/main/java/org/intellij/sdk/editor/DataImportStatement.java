package org.intellij.sdk.editor;

import com.intellij.psi.PsiImportStatementBase;

/**
 * The DataImportStatement class is an object to store information on an import statement
 */

public class DataImportStatement {

    public PsiImportStatementBase importListBase;
    public Integer importLocation;
    public Integer importDomain;
    public Integer importLib;
    private String domainName;
    public DataImportStatement() {
    }

    public PsiImportStatementBase getImportListBase() { return importListBase; }
    public Integer getImportLocation() { return importLocation; }
    public Integer getImportDomain() { return importDomain; }
    public Integer getImportLib() { return importLib; }
    public String getDomainName() { return domainName; }

    public void setDomainName(String domainName) { this.domainName = "Libraries from the " + domainName + " domain"; }
    public void setImportListBase(PsiImportStatementBase importListBase) { this.importListBase = importListBase; }
    public void setImportLocation(Integer importLocation) {this.importLocation = importLocation; }
    public void setImportDomain(Integer importDomain) {this.importDomain = importDomain; }
    public void setImportLib(Integer importLib) { this.importLib = importLib; }

}
