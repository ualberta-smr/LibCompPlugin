package smr.cs.ualberta.libcomp.action;

import com.intellij.lang.ASTNode;
import com.intellij.lang.FileASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.xdebugger.ui.DebuggerColors;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.*;
import com.intellij.psi.*;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;
import smr.cs.ualberta.libcomp.*;
import smr.cs.ualberta.libcomp.data.DependencyStatement;
import smr.cs.ualberta.libcomp.data.ReplacementFeedback;
import smr.cs.ualberta.libcomp.data.ImportStatement;
import smr.cs.ualberta.libcomp.data.User;
import smr.cs.ualberta.libcomp.dialog.ReplacementDialog;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The ActionReplacement class is the main action for the plugin
 * This is triggered by the replacement button on the main plugin dialog
 */

public class EnableDomainAction extends AnAction {

    public int ExludedDomains;

    public EnableDomainAction() {
        ExludedDomains = 2;
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        ExludedDomains = 3;

        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);

        if (psiFile != null) {
            FileType fileType = psiFile.getFileType();

            if (fileType.getDefaultExtension().equalsIgnoreCase("java")) {
                detectJavaImport(psiFile, editor, project);
            }

            if (fileType.getDefaultExtension().equalsIgnoreCase("groovy"))
            {
                try {
                    detectGradleDependency(editor, psiFile , project);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.getPresentation().setEnabledAndVisible(true);
            }

            if (fileType.getDefaultExtension().equalsIgnoreCase("xml"))
            {
                try {
                    detectMavenDependency(editor, psiFile , project);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.getPresentation().setEnabledAndVisible(true);
            }

            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(true);
        }
    }

    public int detectGradleStartLocation(FileASTNode psinode)
    {
        int startLocation = -1;
        ASTNode child = psinode.getFirstChildNode();
        String name = child.getText();
        boolean isFound = name.contains("dependencies");
        boolean isReadAll = false;
        while ((!isFound) && (!isReadAll)) {
            child = child.getTreeNext();
            if (child != null)
            {
                name = child.getText();
                isFound = name.contains("dependencies");
            }
            else isReadAll = true;
        }
        if (isFound) {
            startLocation = child.getStartOffset();
        }
        return startLocation;
    }

    public int detectMavenStartLocation(final Document document)
    {
        int startLocation = -1;
        int i = 0;

        int startOffset = document.getLineStartOffset(i);
        int endOffset = document.getLineEndOffset(i);
        String name = document.getText(new TextRange(startOffset, endOffset)).trim();
        boolean isFound = name.contains("dependencies");
        boolean isReadAll = false;
        while ((!isFound) && (!isReadAll)) {
            ++i;
            startOffset = document.getLineStartOffset(i);
            endOffset = document.getLineEndOffset(i);
            name = document.getText(new TextRange(startOffset, endOffset)).trim();
            isFound = name.contains("dependencies");
            isReadAll = name.contains("</project>");
        }
        if (isFound) {
            startLocation = i;
        }
        return startLocation;
    }

    public void detectMavenDependency(@NotNull final Editor editor, @NotNull final PsiFile psiFile, @NotNull final  Project project  ) throws IOException {

        final MarkupModel editorModel = editor.getMarkupModel();
        final Document document = editor.getDocument();
        String project_name = project.getName();

        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.BREAKPOINT_ATTRIBUTES);
        TextAttributes softerAttributes = attributes.clone();
        boolean dependenciesExists = false;
        int i = 0;
        int loc = detectMavenStartLocation(document); // Parse PSI to detect the PSI dependencies node
        if (loc != -1) // dependencies exists
        {
            dependenciesExists = true;
            i = document.getLineNumber(loc); // line number of the dependencies PSI node
        }

        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int mouseClickLocation = primaryCaret.getOffset();
        int clickedLineNumber = document.getLineNumber(mouseClickLocation);

        String lineText = null;
        String selectedTerm;

        while (dependenciesExists)
        {
            int startOffset = document.getLineStartOffset(i);
            int endOffset = document.getLineEndOffset(i);

            lineText = null;
            String[] valuesInQuotes = new String[0];
            valuesInQuotes = null;
            lineText = document.getText(new TextRange(startOffset, endOffset)).trim();
            if ((lineText != null) && (lineText.length()>2))
            {valuesInQuotes = StringUtils.substringsBetween(lineText, "<groupId>", "</groupId>");}


            if (i == clickedLineNumber) {
                if (valuesInQuotes != null) {
                    selectedTerm = valuesInQuotes[0];
                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                    ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(selectedTerm);
                    if (choicesArray.size() > 0) {
                        int domain = Integer.parseInt(choicesArray.get(1));
                        dataAccessObject.EnabledDomain (domain, project_name);
                        ReplacementAction actionPerformed = new ReplacementAction();
                        try {
                            actionPerformed.detectAllOpenEditors();
                        } catch (IOException | SAXException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            ++i;
            boolean isContainsEnd = lineText.contains("</dependencies>");
            if (isContainsEnd) {
                dependenciesExists = false;
            }
        }
    }

    public void detectGradleDependency(@NotNull final Editor editor, @NotNull final PsiFile psiFile, @NotNull final  Project project  ) throws IOException {

        final MarkupModel editorModel = editor.getMarkupModel();
        final Document document = editor.getDocument();
        String project_name = project.getName();

        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.BREAKPOINT_ATTRIBUTES);
        TextAttributes softerAttributes = attributes.clone();
        boolean dependenciesExists = false;
        int i = 0;
        int loc = detectGradleStartLocation(psiFile.getNode()); // Parse PSI to detect the PSI dependencies node
        if (loc != -1) // dependencies exists
        {
            dependenciesExists = true;
            i = document.getLineNumber(loc); // line number of the dependencies PSI node
        }

        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int mouseClickLocation = primaryCaret.getOffset();
        int clickedLineNumber = document.getLineNumber(mouseClickLocation);


        String lineText = null;
        String selectedTerm;

        while (dependenciesExists)
        {
            int startOffset = document.getLineStartOffset(i);
            int endOffset = document.getLineEndOffset(i);
            lineText = document.getText(new TextRange(startOffset, endOffset)).trim();
            String[] valuesInQuotes = StringUtils.substringsBetween(lineText, "\'", "\'");
            if (i == clickedLineNumber) {
                if (valuesInQuotes != null) {
                    selectedTerm = valuesInQuotes[0];
                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                    ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(selectedTerm);
                    if (choicesArray.size() > 0) {
                        int domain = Integer.parseInt(choicesArray.get(1));
                        dataAccessObject.EnabledDomain (domain, project_name);
                        ReplacementAction actionPerformed = new ReplacementAction();
                        try {
                            actionPerformed.detectAllOpenEditors();
                        } catch (IOException | SAXException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            ++i;
            boolean isContainsEnd = lineText.contains("}");
            if (isContainsEnd) {
                dependenciesExists = false;
            }
        }
    }

    public void detectJavaImport(@NotNull final PsiFile psiFile, @NotNull final Editor editor, @NotNull final  Project project ) {

        try {
            final Document document = editor.getDocument();
            PsiJavaFile javaFile = (PsiJavaFile)psiFile;
            PsiImportList importList = javaFile.getImportList();
            String project_name = project.getName();

            if (importList == null) { return; }
            int importLineNumber;
            Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
            int mouseClickLocation = primaryCaret.getOffset();
            int clickedLineNumber = document.getLineNumber(mouseClickLocation);

            for (PsiImportStatementBase importStatementObject : importList.getAllImportStatements()) {
                String TermSelected = importStatementObject.getImportReference().getReferenceName();
                int locationLastWord = importStatementObject.getImportReference().getTextOffset();
                importLineNumber = document.getLineNumber(locationLastWord);
                if (importLineNumber == clickedLineNumber)
                {
                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                    ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(TermSelected);
                    if (choicesArray.size()>0){
                        int domain = Integer.parseInt(choicesArray.get(1));
                        dataAccessObject.EnabledDomain (domain, project_name);
                        ReplacementAction actionPerformed = new ReplacementAction();
                        try {
                            actionPerformed.detectAllOpenEditors();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
