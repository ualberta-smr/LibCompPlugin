package org.intellij.sdk.editor;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.ui.DebuggerColors;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.psi.*;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class EditorIllustrationAction extends AnAction {


    public ArrayList<ImportStatement> ImportListObjects;

    private String allLibrary;
    private int year;
    private int month;
  //  String domainName;
  //  private int libID;
  //  private int domainID ;

    public EditorIllustrationAction() {
        ImportListObjects = new ArrayList<>();
    }

    public void setAllLibrary(String allLibrary) {  this.allLibrary = this.allLibrary + ";" + allLibrary;  }
    public String getAllLibrary() { return allLibrary; }

    /**
     * The actionPerformed method is called whenever an action event is executed, i.e. a right click on the editor
     * @param event is the current action event
     */
    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
       // detectAllOpenEditors();


       replaceRequestedImport(event);
       detectImportStatements(event);
    }

    public void detectAllOpenEditors() {

        int i = 0;
        Project proj= ProjectManager.getInstance().getOpenProjects()[0];
        FileEditorManager manager = FileEditorManager.getInstance(proj);

        VirtualFile[] filesAll = manager.getOpenFiles();
        FileEditor[] editorFileAll = manager.getAllEditors();


        while (i < editorFileAll.length)
        {
            PsiFile psiFile = PsiManager.getInstance(proj).findFile(filesAll[i]);
            Editor editor = ((TextEditor)editorFileAll[i]).getEditor();
            detectionImports(psiFile, editor);
            i = i + 1;
        }

    }


    /**
     * The update method is called whenever the action event is updated.
     * This will later be replaced with the onLoad method once I figure that out
     * The condition in this method ensures that there is al least one caret available
     * The method then calls a separate method to detect import statements
     * @param event is the current action event
     */
    @Override
    public void update(@NotNull final AnActionEvent event) {
       final Project project = event.getProject();
       final Editor editor = event.getData(CommonDataKeys.EDITOR);

        detectAllOpenEditors();


        if ((project != null) && (editor != null))
        {
            detectImportStatements(event);
        }


    }

     /**
     * The replaceRequestedImport method will replace the import statement on the line which the user clicked on with the library which the user chooses from the dialog
     * This will only occur if the library in the initial import statement is valid (i.e. in database)
     * For now, the libraries in the database are the last term of the import statement
     * ex. for my.import.statement.rehab, the term queried against in the database is "rehab"
     * This can be changed with some lexical analysis once we discuss exactly which part of the statement must be queried against the database
     * @param event This is the current action event
     */
    public void replaceRequestedImport(@NotNull final AnActionEvent event) {
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();

         String projectID = project.getName();
         String className = this.getClass().getName();

        //Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();

        //Get the location of the mouse click, get the caret offset, then from the offset get the line number
        int mouseClickLocation = primaryCaret.getOffset();
        int clickedLineNumber = document.getLineNumber(mouseClickLocation);

        //Check if the user clicked on a line that is potentially replaceable (i.e. import statement is in database)
        int currentLine = 0;


        while (currentLine < ImportListObjects.size()) {
            if (ImportListObjects.get(currentLine).getImportLocation() == clickedLineNumber) {
                //found a replaceable import statement
                String importStatementFull;
                String importStatementLastWord; //this importStatementObject the last library of the import statement (i.e. last word at the end)
                int offsetLastWord = 0;
                int lineNumber;

                PsiImportStatementBase importStatementObject =  ImportListObjects.get(currentLine).getImportListBase();
                importStatementFull = importStatementObject.getImportReference().getQualifiedName();
                importStatementLastWord = importStatementObject.getImportReference().getReferenceName();

                offsetLastWord = importStatementObject.getImportReference().getTextOffset();
                lineNumber = document.getLineNumber(offsetLastWord);

                int locationStartOfImport = offsetLastWord - (importStatementFull.length() - importStatementLastWord.length());
                int locationEndOfImport = document.getLineEndOffset(lineNumber) - 1;

                ButtonClumn bc=new ButtonClumn(ImportListObjects.get(currentLine).getDomainName(), ImportListObjects.get(currentLine).getImportDomain(),ImportListObjects.get(currentLine).getImportLib(),year,month);
                WindowAdapter adapter = new WindowAdapter() {

                    //does not do anything right now, may remove
                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        String finalChoice = bc.getLibraryReturned();
                        if (finalChoice.equals("None") == false) {
                            finalChoice = finalChoice + ".*";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(locationStartOfImport, locationEndOfImport, finalChoice1));
                        }
                        SelectRecords dataAccessObject = new SelectRecords();
                        feedback feedbackDataPoint = new feedback(importStatementFull,finalChoice, locationStartOfImport,projectID,className,getAllLibrary(),bc.getSelectionLibrary());
                        int results = dataAccessObject.updateFeedback(feedbackDataPoint);
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        String finalChoice = bc.getLibraryReturned();
                        if (finalChoice.equals("None") == false) {
                            finalChoice = finalChoice + ".*";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(locationStartOfImport, locationEndOfImport, finalChoice1));
                            SelectRecords dataAccessObject = new SelectRecords();
                            feedback feedbackDataPoint = new feedback(importStatementFull,finalChoice1, locationStartOfImport,projectID,className,getAllLibrary(),bc.getSelectionLibrary());
                            int results = dataAccessObject.updateFeedback(feedbackDataPoint);
                        }
                        SelectRecords dataAccessObject = new SelectRecords();
                        feedback feedbackDataPoint = new feedback(importStatementFull,finalChoice, locationStartOfImport,projectID,className,getAllLibrary(),bc.getSelectionLibrary());
                        int results = dataAccessObject.updateFeedback(feedbackDataPoint);
                    }
                };

               bc.addWindowListener(adapter);
               bc.addWindowFocusListener(adapter);

                bc.setVisible(true);

            }
            currentLine = currentLine + 1;
        }
    }

    /**
     * The detectImportStatementMethod will got through the current open file and test the import statements to see if they are in the database
     * The trigger for this to occur is a right click anywhere in the editor, I have yet to figure out how to have it work onLoad
     * Right now, the library being queried is the "last word" of the import statement
     * ex. for my.import.statement.rehab, the term queried against in the database is "rehab"
     * @param event This is the current action event
     */

    public void detectionImports(@NotNull final PsiFile psiFile,@NotNull final Editor editor )
    {
        final MarkupModel editorModel = editor.getMarkupModel();
        final Document document = editor.getDocument();

        //Work off of the primary caret to get the selection info
        final PsiJavaFile javaFile = (PsiJavaFile)psiFile;

        //Prepare the color for the the highlighted lines
        TextAttributes attributes =
                EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.BREAKPOINT_ATTRIBUTES);
        TextAttributes softerAttributes = attributes.clone();

        final PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return;
        }

        ImportListObjects.clear();
        editorModel.removeAllHighlighters();

        int locationLastWord = 0;
        int importLineNumber;

        //iterate through import statements, if the "last term" is in the database, highlight that line
        for (PsiImportStatementBase importStatementObject : importList.getAllImportStatements()){

            //get location of import statement
            String TermSelected = importStatementObject.getImportReference().getReferenceName();
            locationLastWord = importStatementObject.getImportReference().getTextOffset();
            importLineNumber = document.getLineNumber(locationLastWord);
            setAllLibrary(TermSelected);

            //check database
            SelectRecords dataAccessObject = new SelectRecords();
            ArrayList<String> choicesArray = dataAccessObject.selectAllLibraries(TermSelected);
            if (choicesArray.size()>0){

                // Prepare the object for the PSI, location, library, and domain
                ImportStatement impObj = new ImportStatement();
                impObj.setImportListBase(importStatementObject);
                impObj.setImportLocation(importLineNumber);
                impObj.setImportLib(Integer.parseInt(choicesArray.get(0)));
                impObj.setImportDomain(Integer.parseInt(choicesArray.get(1)));
                impObj.setDomainName(choicesArray.get(4));
                ImportListObjects.add(impObj);

                year = Integer.parseInt(choicesArray.get(2));
                month = Integer.parseInt(choicesArray.get(3));
                //domainName = ;

                //highlight the line
                editorModel.addLineHighlighter(importLineNumber,
                        DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1,softerAttributes);
            }
        }


    }

    public void detectImportStatements(@NotNull final AnActionEvent event) {

         PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
         Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        detectionImports(psiFile, editor);

    }
}
