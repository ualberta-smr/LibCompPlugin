package smr.cs.ualberta.libcomp.action;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.ui.DebuggerColors;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.psi.*;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import smr.cs.ualberta.libcomp.*;
import smr.cs.ualberta.libcomp.data.DependencyStatement;
import smr.cs.ualberta.libcomp.data.Feedback;
import smr.cs.ualberta.libcomp.data.ImportStatement;
import smr.cs.ualberta.libcomp.data.User;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * The ActionReplacement class is the main action for the plugin
 * This is triggered by the replacement button on the main plugin dialog
 */

public class Replacement extends AnAction {
    
    public ArrayList<ImportStatement> ImportListObjects;
    public ArrayList<DependencyStatement> DependListObjects;

    private int to_library;
    private String full_lib_list;
    private String libraryName;
    private int sendToCloud = 1;
    public Replacement() {
    ImportListObjects = new ArrayList<>();
    DependListObjects = new ArrayList<>();
    }

    /**
     * The actionPerformed method is called whenever an action event is executed, i.e. a right click on the editor
     * @param event is the current action event
     */
    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
       //Returned later: detectAllOpenEditors();
        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        if (psiFile != null)
        {
            FileType fileType = psiFile.getFileType();
            if (fileType.getDefaultExtension().equalsIgnoreCase("java")) {
                replaceRequestedImport(event);
                try {
                    detectImportStatements(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                replaceRequestedDependence(event);
                try {
                    detectDependencies(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                            }
            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(true);
        }

    }

    public void detectAllOpenEditors() {
        int i = 0;
        Project proj= ProjectManager.getInstance().getOpenProjects()[0];
        FileEditorManager manager = FileEditorManager.getInstance(proj);
        VirtualFile[] filesAll = manager.getOpenFiles();
        FileEditor[] editorFileAll = manager.getAllEditors();
        
        while (i < editorFileAll.length) {
            PsiFile psiFile = PsiManager.getInstance(proj).findFile(filesAll[i]);
            Editor editor = ((TextEditor)editorFileAll[i]).getEditor();
            detectImports(psiFile, editor);
            i = i + 1;
        }
    }

    public void detectDependencies(@NotNull final AnActionEvent event) throws IOException {
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final MarkupModel editorModel = editor.getMarkupModel();
        final Document document = editor.getDocument();

        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.BREAKPOINT_ATTRIBUTES);
        TextAttributes softerAttributes = attributes.clone();

        String lineText;
        String TermSelected;

        //Replace the selection with a string
        DependListObjects.clear();
        editorModel.removeAllHighlighters();

        boolean Searchmode = false;

        for (int i = 0; i < document.getLineCount()-1; i++) {
            int startOffset = document.getLineStartOffset(i);
            int endOffset = document.getLineEndOffset(i);
            lineText = document.getText(new TextRange(startOffset, endOffset)).trim();

            if (Searchmode) {
                String[] valuesInQuotes = StringUtils.substringsBetween(lineText, "\'", "\'");
                //if (valuesInQuotes.length > 0)
                if (valuesInQuotes!=null)
                {
                    TermSelected = valuesInQuotes[0];
                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                    ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(TermSelected);
                    if (choicesArray.size() > 0) {

                        DependencyStatement depObj = new DependencyStatement();
                        depObj.setImportLocation(i);
                        depObj.setImportLib(Integer.parseInt(choicesArray.get(0)));
                        depObj.setImportDomain(Integer.parseInt(choicesArray.get(1)));
                        depObj.setDomainName(choicesArray.get(4));
                        depObj.setFromlocation(startOffset);
                        depObj.setTolocation(endOffset);
                        DependListObjects.add(depObj);

                        editorModel.addLineHighlighter(i,
                                DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1, softerAttributes);
                    }
                }
            }
            boolean isContains = lineText.contains("dependencies");
            if (isContains) { Searchmode = true; }
            boolean isContainsEnd = lineText.contains("}");
            if (Searchmode && isContainsEnd) { Searchmode = false; }
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
//       final Project project = event.getProject();

        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        if (psiFile != null)
        {
            FileType fileType = psiFile.getFileType();
            if (fileType.getDefaultExtension().equalsIgnoreCase("java")) {
                try {
                    detectImportStatements(event);
                } catch (IOException e) {e.printStackTrace(); }
            }
            else
            {
            try {
               detectDependencies(event);
                event.getPresentation().setEnabledAndVisible(true);
            } catch (IOException e) { e.printStackTrace(); }
            }

             event.getPresentation().setVisible(true);
             event.getPresentation().setEnabled(true);
            //detectAllOpenEditors();

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

         String project_name = project.getName();
         String class_name = this.getClass().getName();
         Date action_date = new Date();
         int from_library;

        User userRecord = new User();
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        userRecord = dataAccessObject.readUserProfile();
        sendToCloud = Integer.parseInt(userRecord.getSendAllCloud());

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
                int line_num;

                from_library = ImportListObjects.get(currentLine).getImportLib();
                int finalFrom_library = from_library;
                PsiImportStatementBase importStatementObject =  ImportListObjects.get(currentLine).getImportListBase();
                importStatementFull = importStatementObject.getImportReference().getQualifiedName();
                importStatementLastWord = importStatementObject.getImportReference().getReferenceName();

                offsetLastWord = importStatementObject.getImportReference().getTextOffset();
                line_num = document.getLineNumber(offsetLastWord);

                int locationStartOfImport = offsetLastWord - (importStatementFull.length() - importStatementLastWord.length());
                int locationEndOfImport = document.getLineEndOffset(line_num) - 1;

                smr.cs.ualberta.libcomp.dialog.Replacement bc=new smr.cs.ualberta.libcomp.dialog.Replacement(ImportListObjects.get(currentLine).getDomainName(), ImportListObjects.get(currentLine).getImportDomain(),ImportListObjects.get(currentLine).getImportLib());

                WindowAdapter adapter = new WindowAdapter() {

                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        String finalChoice = bc.getLibraryReturned();
                        to_library = bc.getto_library();
                        full_lib_list = bc.getSelectionLibrary();

                        if (finalChoice.equals("None") == false) {
                            finalChoice = finalChoice + ".*";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(locationStartOfImport, locationEndOfImport, finalChoice1));
                        }


                        try {
                            if (sendToCloud == 1)
                            {
                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                Feedback feedbackPoint = new Feedback(0, action_date, line_num, project_name, class_name, full_lib_list, finalFrom_library, to_library);
                                dataAccessObject.updateFeedback(feedbackPoint);
                            }

                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        String finalChoice = bc.getLibraryReturned();
                        to_library = bc.getto_library();
                        full_lib_list = bc.getSelectionLibrary();

                        if (finalChoice.equals("None") == false) {
                            finalChoice = finalChoice + ".*";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(locationStartOfImport, locationEndOfImport, finalChoice1));
                            try {
                                if (sendToCloud == 1)
                                {
                                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                                    Feedback feedbackPoint = new Feedback(0, action_date, line_num, project_name, class_name, full_lib_list, finalFrom_library, to_library);
                                    dataAccessObject.updateFeedback(feedbackPoint);
                                }
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        try {
                            if (sendToCloud == 1)
                            {
                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                Feedback feedbackPoint = new Feedback(0, action_date, line_num, project_name, class_name, full_lib_list, finalFrom_library, to_library);
                                dataAccessObject.updateFeedback(feedbackPoint);
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                };
               bc.addWindowListener(adapter);
               bc.addWindowFocusListener(adapter);
               bc.setVisible(true);
            }
            currentLine = currentLine + 1;
        }
    }


    public void replaceRequestedDependence(@NotNull final AnActionEvent event) {
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();

        String project_name = project.getName();
        String class_name = this.getClass().getName();
        Date action_date = new Date();
        int from_library;

        User userRecord = new User();
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        userRecord = dataAccessObject.readUserProfile();
        sendToCloud = Integer.parseInt(userRecord.getSendAllCloud());

        //Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        //Get the location of the mouse click, get the caret offset, then from the offset get the line number
        int mouseClickLocation = primaryCaret.getOffset();
        int clickedLineNumber = document.getLineNumber(mouseClickLocation);

        //Check if the user clicked on a line that is potentially replaceable (i.e. import statement is in database)
        int currentLine = 0;
        int locationStartOfImport;
        int locationEndOfImport;

        while (currentLine < DependListObjects.size()) {
            if (DependListObjects.get(currentLine).getImportLocation() == clickedLineNumber) {
                //found a replaceable import statement

                from_library = DependListObjects.get(currentLine).getImportLib();
                locationStartOfImport = DependListObjects.get(currentLine).getFromlocation();
                locationEndOfImport = DependListObjects.get(currentLine).getTolocation();
                int finalFrom_library = from_library;


                smr.cs.ualberta.libcomp.dialog.Replacement bc=new smr.cs.ualberta.libcomp.dialog.Replacement(DependListObjects.get(currentLine).getDomainName(), DependListObjects.get(currentLine).getImportDomain(),DependListObjects.get(currentLine).getImportLib());

                int finalLocationStartOfImport = locationStartOfImport;
                int finalLocationEndOfImport = locationEndOfImport;
                WindowAdapter adapter = new WindowAdapter() {

                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        String finalChoice = bc.getLibraryReturned();
                        to_library = bc.getto_library();
                        full_lib_list = bc.getSelectionLibrary();
                        libraryName = bc.getLibraryname();

                        if (finalChoice.equals("None") == false) {

                            finalChoice = "    compile group: \'"+ finalChoice + "', name: \'"+ libraryName + "\',  version: \'please select version\'";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(finalLocationStartOfImport, finalLocationEndOfImport, finalChoice1));
                        }


                        try {
                            if (sendToCloud == 1)
                            {
                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                Feedback feedbackPoint = new Feedback(0, action_date, clickedLineNumber, project_name, class_name, full_lib_list, finalFrom_library, to_library);
                                dataAccessObject.updateFeedback(feedbackPoint);
                            }

                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        String finalChoice = bc.getLibraryReturned();
                        to_library = bc.getto_library();
                        full_lib_list = bc.getSelectionLibrary();
                        libraryName = bc.getLibraryname();

                        if (finalChoice.equals("None") == false) {
                            finalChoice = "    compile group: \'"+ finalChoice + "', name: \'"+ libraryName + "\',  version: \'please select version\'";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(finalLocationStartOfImport, finalLocationEndOfImport, finalChoice1));
                            try {
                                if (sendToCloud == 1)
                                {
                                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                                    Feedback feedbackPoint = new Feedback(0, action_date, clickedLineNumber, project_name, class_name, full_lib_list, finalFrom_library, to_library);
                                    dataAccessObject.updateFeedback(feedbackPoint);
                                }
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        try {
                            if (sendToCloud == 1)
                            {
                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                Feedback feedbackPoint = new Feedback(0, action_date, clickedLineNumber, project_name, class_name, full_lib_list, finalFrom_library, to_library);
                                dataAccessObject.updateFeedback(feedbackPoint);
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
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
     */
    public void detectImports(@NotNull final PsiFile psiFile, @NotNull final Editor editor ) {
        //Work off of the primary caret to get the selection info
        try {
            //Block of code to try
            final MarkupModel editorModel = editor.getMarkupModel();
            final Document document = editor.getDocument();
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
       //     ResetAllLibrary("");
            //iterate through import statements, if the "last term" is in the database, highlight that line
            for (PsiImportStatementBase importStatementObject : importList.getAllImportStatements()){

                //get location of import statement
                String TermSelected = importStatementObject.getImportReference().getReferenceName();
                locationLastWord = importStatementObject.getImportReference().getTextOffset();
                importLineNumber = document.getLineNumber(locationLastWord);

                //check database
                DatabaseAccess dataAccessObject = new DatabaseAccess();
                ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(TermSelected);
                if (choicesArray.size()>0){
           //         setAllLibrary(choicesArray.get(0));
                    //Prepare the object for the PSI, location, library, and domain
                    ImportStatement impObj = new ImportStatement();
                    impObj.setImportListBase(importStatementObject);
                    impObj.setImportLocation(importLineNumber);
                    impObj.setImportLib(Integer.parseInt(choicesArray.get(0)));
                    impObj.setImportDomain(Integer.parseInt(choicesArray.get(1)));
                    impObj.setDomainName(choicesArray.get(4));
                    ImportListObjects.add(impObj);


                    //highlight the line
                    editorModel.addLineHighlighter(importLineNumber,
                            DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1,softerAttributes);
                }
            }
        }
        catch(Exception e) {
            //Block of code to handle errors
        }
    }

    public void detectImportStatements(@NotNull final AnActionEvent event) throws IOException {

         PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
         Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);

         try {
             final PsiJavaFile javaFile = (PsiJavaFile) psiFile;
             detectImports(psiFile, editor);
         }
         catch(Exception e) {
             //detectionDependencies(psiFile, editor);
         }
    }
}
