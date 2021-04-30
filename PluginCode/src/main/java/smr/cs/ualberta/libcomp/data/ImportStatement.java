package smr.cs.ualberta.libcomp.data;

import com.intellij.psi.PsiImportStatementBase;

/**
 * The DataImportStatement class is an object to store information on an import statement
 */

public class ImportStatement extends Statement {
    public ImportStatement() {}

    public PsiImportStatementBase importListBase;

    public PsiImportStatementBase getImportListBase() { return importListBase; }

    @Override
    public void setDomainName(String domainName) { this.domainName = "Libraries from the " + domainName + " domain"; }
    public void setImportListBase(PsiImportStatementBase importListBase) { this.importListBase = importListBase; }
}
