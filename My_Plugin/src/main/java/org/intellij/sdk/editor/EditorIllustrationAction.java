package org.intellij.sdk.editor;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.ui.DebuggerColors;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.psi.*;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.util.ArrayList;

public class EditorIllustrationAction extends AnAction {

    public ArrayList<PsiImportStatementBase> importList;
    public ArrayList<Integer> importListLocation;

    public EditorIllustrationAction() {
        importList = new ArrayList<>();
        importListLocation = new ArrayList<>();
    }

    /**
     * The actionPerformed method is called whenever an action event is executed, i.e. a right click on the editor
     * @param event is the current action event
     */
    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
       replaceRequestedImport(event);
       detectImportStatements(event);
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
        if (editor != null && project != null) {
            detectImportStatements(event);
        }
    }

    /**
     * The askUser method shows the user a dialog where the user may choose a replacement library from a list of valid options sent as a parameter
     * @param libraryChoices is the lost of libraries which the user may choose from
     * @return A string is returned, this is the library which the user selects as a replacement (returns empty string if no choice)
     */
    static String askUser(String[] libraryChoices) {
        String librarySelected = (String) JOptionPane.showInputDialog(
                null,
                "Select one of the Libraries to replace",
                "Library Suggestion Screen",
                JOptionPane.PLAIN_MESSAGE,
                null,
                libraryChoices,
                libraryChoices[0]);
        return librarySelected;
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

        //Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();

        //Get the location of the mouse click, get the caret offset, then from the offset get the line number
        int mouseClickLocation = primaryCaret.getOffset();
        int clickedLineNumber = document.getLineNumber(mouseClickLocation);

        //Check if the user clicked on a line that is potentially replaceable (i.e. import statement is in database)
        int currentLine = 0;
        while (currentLine <= importListLocation.size()) {
            if (importListLocation.get(currentLine) == clickedLineNumber) {

                //found a replaceable import statement
                String importStatementFull;
                String importStatementLastWord; //this importStatementObject the last library of the import statement (i.e. last word at the end)
                int offsetLastWord = 0;
                int lineNumber;

                PsiImportStatementBase importStatementObject = importList.get(currentLine);
                importStatementFull = importStatementObject.getImportReference().getQualifiedName();
                importStatementLastWord = importStatementObject.getImportReference().getReferenceName();

                offsetLastWord = importStatementObject.getImportReference().getTextOffset();
                lineNumber = document.getLineNumber(offsetLastWord);

                int locationStartOfImport = offsetLastWord - (importStatementFull.length() - importStatementLastWord.length());
                int locationEndOfImport = document.getLineEndOffset(lineNumber) - 1;

                //Send the library on the line which the user clicked on to be queried
                String librarySelected = importStatementLastWord;
                SelectRecords dataAccessObject = new SelectRecords();
                ArrayList<String> libraryChoicesArray = dataAccessObject.selectAllLibraries(librarySelected);

                //Replace the import statement on line clicked on with the selected replacement library
                if (libraryChoicesArray.size() > 0) {
                    String[] otherLibraryOptions = libraryChoicesArray.toArray(new String[libraryChoicesArray.size()]);
                    String selectedReplacement = "";
                    selectedReplacement = askUser(otherLibraryOptions);
                    if (selectedReplacement.length() > 0) {
                        String finalChoice = selectedReplacement;
                        WriteCommandAction.runWriteCommandAction(project, () ->
                            document.replaceString(locationStartOfImport, locationEndOfImport, finalChoice));}
                }
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
    public void detectImportStatements(@NotNull final AnActionEvent event) {

        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
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

        this.importList.clear();
        importListLocation.clear();
        editorModel.removeAllHighlighters();

        String importStatementLastWord;
        int locationLastWord = 0;
        int importLineNumber;

        //iterate through import statements, if the "last term" is in the database, highlight that line
        for (PsiImportStatementBase importStatementObject : importList.getAllImportStatements()){

            //get location of import statement
            importStatementLastWord = importStatementObject.getImportReference().getReferenceName();
            locationLastWord = importStatementObject.getImportReference().getTextOffset();
            importLineNumber = document.getLineNumber(locationLastWord);
            String TermSelected = importStatementLastWord;

            //check database
            SelectRecords dataAccessObject = new SelectRecords();
            ArrayList<String> choicesArray = dataAccessObject.selectAllLibraries(TermSelected);
               if (choicesArray.size()>0){
                this.importList.add(importStatementObject);
                importListLocation.add(importLineNumber);

                //highlight the line
                editorModel.addLineHighlighter(importLineNumber,
                        DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1,softerAttributes);
            }
        }
    }
}
