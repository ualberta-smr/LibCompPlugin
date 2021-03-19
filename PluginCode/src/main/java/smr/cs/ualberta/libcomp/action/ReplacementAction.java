package smr.cs.ualberta.libcomp.action;

import com.android.aapt.Resources;
import com.intellij.lang.ASTNode;
import com.intellij.lang.FileASTNode;
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
import com.intellij.openapi.wm.WindowManager;
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
import smr.cs.ualberta.libcomp.data.ReplacementFeedback;
import smr.cs.ualberta.libcomp.data.ImportStatement;
import smr.cs.ualberta.libcomp.data.User;
import smr.cs.ualberta.libcomp.dialog.ReplacementDialog;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * The ActionReplacement class is the main action for the plugin
 * This is triggered by the replacement button on the main plugin dialog
 */

public class ReplacementAction extends AnAction {
    
    public ArrayList<ImportStatement> ImportListObjects;
    public ArrayList<DependencyStatement> DependListObjects;
    public ArrayList<DependencyStatement> MavenListObjects;
    private int to_library;
    private String full_lib_list;
    private String libraryName;
    private boolean sendToCloud = false ;


    public ReplacementAction() {
    ImportListObjects = new ArrayList<>();
    DependListObjects = new ArrayList<>();
    MavenListObjects = new ArrayList<>();

    }

    /**
     * The actionPerformed method is called whenever an action event is executed, i.e. a right click on the editor
     * @param event is the current action event
     */
    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {

        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        if (psiFile != null) {
            FileType fileType = psiFile.getFileType();
            String fileExtention = fileType.getDefaultExtension();

            if (fileExtention.equalsIgnoreCase("java") || fileExtention.equalsIgnoreCase("xml") ||
                    fileExtention.equalsIgnoreCase("groovy")) {
                try {
                    replaceRequestedImport(event);
                    detectImportOnAction(event);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }

            }

            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(true);
        }
    }

   public  Project getActiveProject()
    {

        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Project activeProject = null;
        for (Project project : projects) {
            Window window = WindowManager.getInstance().suggestParentWindow(project);
            if (window != null && window.isActive()) {
                activeProject = project;
            }
        }
        return  activeProject;
    }

    public void detectAllOpenEditors() throws IOException {
        Project proj= getActiveProject();
        String project_name = "none";
        if (proj == null)  { return;}
        project_name = proj.getName();
        FileEditorManager manager = FileEditorManager.getInstance(proj);
        VirtualFile[] filesAll = manager.getOpenFiles();
        FileEditor[] editorFileAll = manager.getAllEditors();
        int indexOpenEditors = 0;

        while (indexOpenEditors < editorFileAll.length) {

            PsiFile psiFile = PsiManager.getInstance(proj).findFile(filesAll[indexOpenEditors]);
            Editor editor = ((TextEditor)editorFileAll[indexOpenEditors]).getEditor();

            if (psiFile != null) {
                FileType fileType = psiFile.getFileType();

                if (fileType.getDefaultExtension().equalsIgnoreCase("java"))
                {detectImports(psiFile, editor, project_name); }
                if (fileType.getDefaultExtension().equalsIgnoreCase("xml"))
                {detectMaven(editor, psiFile, project_name);}
                if (fileType.getDefaultExtension().equalsIgnoreCase("groovy"))
                {detectDependancy(editor, psiFile, project_name);}
            }
            indexOpenEditors = indexOpenEditors + 1;
        }
    }

    public int detectDependenciesPSI(FileASTNode psinode)
    {
        int location = -1;
        ASTNode child = psinode.getFirstChildNode();
        String name = child.getText();
        boolean found = name.contains("dependencies");
        boolean over = false;
        while ((!found) && (!over))
        {
            child = child.getTreeNext();
            if (child != null)
            {
                name = child.getText();
                found = name.contains("dependencies");
            }
            else over = true;
           }
        if (found)
        {
             location = child.getStartOffset();
        }
        return location;
    }


    public void detectDependancy(@NotNull final Editor editor, @NotNull final PsiFile psiFile, @NotNull final  String project_name ) throws IOException {

        final MarkupModel editorModel = editor.getMarkupModel();
        final Document document = editor.getDocument();
        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.BREAKPOINT_ATTRIBUTES);
        TextAttributes softerAttributes = attributes.clone();
        boolean dependenciesExists = false;
        int i = 0;
        int loc = detectDependenciesPSI(psiFile.getNode()); // Parse PSI to detect the PSI dependencies node
        if (loc != -1) // dependencies exists
        {
            dependenciesExists = true;
            i = document.getLineNumber(loc); // line number of the dependencies PSI node
        }
        String lineText;
        String selectedTerm;
        DependListObjects.clear();
        editorModel.removeAllHighlighters();

        while (dependenciesExists)
        {
            int startOffset = document.getLineStartOffset(i);
            int endOffset = document.getLineEndOffset(i);
            lineText = null;
            String[] valuesInQuotes = new String[0];
            valuesInQuotes = null;

            lineText = document.getText(new TextRange(startOffset, endOffset)).trim();

            if ((lineText != null) && (lineText.length()>2))
            {valuesInQuotes = StringUtils.substringsBetween(lineText, "\'", "\'");}



            if (valuesInQuotes != null) {
                    selectedTerm = valuesInQuotes[0];

                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                    ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(selectedTerm);
                    if (choicesArray.size() > 0) {
                        DependencyStatement depObj = new DependencyStatement();
                        depObj.setImportLocation(i);
                        depObj.setImportLib(Integer.parseInt(choicesArray.get(0)));
                        depObj.setImportDomain(Integer.parseInt(choicesArray.get(1)));
                        depObj.setDomainName(choicesArray.get(2));
                        depObj.setFromlocation(startOffset);
                        depObj.setTolocation(endOffset);

                        if (dataAccessObject.isEnabled(Integer.parseInt(choicesArray.get(1)), project_name)) {
                            depObj.setEnableddomain(false);
                        }
                        else {
                            depObj.setEnableddomain(true);
                        }

                        DependListObjects.add(depObj);


                        if (depObj.getEnableddomain()) {
                            editorModel.addLineHighlighter(i,
                                    DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1, softerAttributes);

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


    public int detectMavenPSI(final Document document)
    {
        int location = -1;
        int i = 0;

        int startOffset = document.getLineStartOffset(i);
        int endOffset = document.getLineEndOffset(i);
        String name = document.getText(new TextRange(startOffset, endOffset)).trim();
        boolean found = name.contains("dependencies");
        boolean over = false;
        while ((!found) && (!over))
        {
            ++i;
            startOffset = document.getLineStartOffset(i);
            endOffset = document.getLineEndOffset(i);
            name = document.getText(new TextRange(startOffset, endOffset)).trim();
            found = name.contains("dependencies");
            over = name.contains("</project>");
        }
        if (found)
        {
            location = i;
        }
        return location;
    }

    public void detectMaven(@NotNull final Editor editor, @NotNull final PsiFile psiFile, @NotNull final  String project_name ) throws IOException {

        final MarkupModel editorModel = editor.getMarkupModel();
        final Document document = editor.getDocument();
        String lineText;
        String selectedTerm;

        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.BREAKPOINT_ATTRIBUTES);
        TextAttributes softerAttributes = attributes.clone();
        boolean dependenciesExists = false;
        int i = 0;
        int loc = detectMavenPSI(document); // Parse PSI to detect the PSI dependencies node
        if (loc != -1) // dependencies exists
        {
            dependenciesExists = true;
            i = loc; // line number of the dependencies PSI node
        }
        MavenListObjects.clear();
        editorModel.removeAllHighlighters();

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

            if (valuesInQuotes != null) {
                selectedTerm = valuesInQuotes[0];

                DatabaseAccess dataAccessObject = new DatabaseAccess();
                ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(selectedTerm);
                if (choicesArray.size() > 0) {
                    DependencyStatement depObj = new DependencyStatement();
                    depObj.setImportLocation(i);
                    depObj.setImportLib(Integer.parseInt(choicesArray.get(0)));
                    depObj.setImportDomain(Integer.parseInt(choicesArray.get(1)));
                    depObj.setDomainName(choicesArray.get(2));
                    depObj.setFromlocation(startOffset);
                    depObj.setTolocation(endOffset);

                    if (dataAccessObject.isEnabled(Integer.parseInt(choicesArray.get(1)), project_name)) {
                        depObj.setEnableddomain(false);
                    }
                    else {
                        depObj.setEnableddomain(true);
                    }

                    MavenListObjects.add(depObj);


                    if (depObj.getEnableddomain()) {
                        editorModel.addLineHighlighter(i,
                                DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1, softerAttributes);

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

    public void detectMavenOnAction(@NotNull final AnActionEvent event) {

        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        String project_name = project.getName();

        try {
            detectMaven(editor, psiFile, project_name);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void detectDependencyOnAction(@NotNull final AnActionEvent event) throws IOException {

            Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
            PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
            final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
            String project_name = project.getName();

            try {
                detectDependancy(editor, psiFile, project_name);
            }
            catch(Exception e) {
                e.printStackTrace();
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

        try {
            detectAllOpenEditors();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        if (psiFile != null) {
            FileType fileType = psiFile.getFileType();
            if (fileType.getDefaultExtension().equalsIgnoreCase("java")) {
                try {
                    detectImportOnAction(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileType.getDefaultExtension().equalsIgnoreCase("xml")) {
                detectMavenOnAction(event);
            }
            if (fileType.getDefaultExtension().equalsIgnoreCase("groovy")) {
                try {
                    detectDependencyOnAction(event);
                    event.getPresentation().setEnabledAndVisible(true);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
             event.getPresentation().setVisible(true);
             event.getPresentation().setEnabled(true);
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
    public void replaceRequestedImport(@NotNull final AnActionEvent event) throws ParseException {
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);

        String project_name = project.getName();
        String class_name = "";
        if (file instanceof PsiJavaFile) {
            PsiClass[] classes = ((PsiJavaFile) file).getClasses();
            if (classes.length > 0) {
                class_name= classes[0].getQualifiedName();
            }
        }

        Date action_date = new Date();
        int from_library;

        User userRecord = new User();
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        userRecord = dataAccessObject.readUserProfile();
        sendToCloud = (Integer.parseInt(userRecord.getSendAllCloud()) == 1);

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

                ReplacementDialog replacementDialog =new ReplacementDialog(ImportListObjects.get(currentLine).getDomainName(), ImportListObjects.get(currentLine).getImportDomain(),ImportListObjects.get(currentLine).getImportLib());

                String finalClass_name = class_name;
                WindowAdapter adapter = new WindowAdapter() {

                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        String finalChoice = replacementDialog.getLibraryReturned();
                        to_library = replacementDialog.getto_library();
                        full_lib_list = replacementDialog.getSelectionLibrary();

                        if (finalChoice.equals("None") == false) {
                            finalChoice = finalChoice + ".*";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(locationStartOfImport, locationEndOfImport, finalChoice1));
                        }

                        try {

                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, action_date, line_num, project_name, finalClass_name, full_lib_list, finalFrom_library, to_library);
                                dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);


                        }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        String finalChoice = replacementDialog.getLibraryReturned();
                        to_library = replacementDialog.getto_library();
                        full_lib_list = replacementDialog.getSelectionLibrary();

                        if (finalChoice.equals("None") == false) {
                            finalChoice = finalChoice + ".*";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(locationStartOfImport, locationEndOfImport, finalChoice1));
                            try {

                                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                                    ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, action_date, line_num, project_name, finalClass_name, full_lib_list, finalFrom_library, to_library);
                                    dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);

                            }
                            catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        try {

                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, action_date, line_num, project_name, finalClass_name, full_lib_list, finalFrom_library, to_library);
                                dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);
                            }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                };
               replacementDialog.addWindowListener(adapter);
               replacementDialog.addWindowFocusListener(adapter);
               replacementDialog.setVisible(true);
            }
            currentLine = currentLine + 1;
        }
    }

    public void replaceRequestedMaven(@NotNull final AnActionEvent event) throws ParseException {

        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);

        String project_name = project.getName();
        String class_name = "Maven File";

        Date action_date = new Date();
        int from_library;

        User userRecord = new User();
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        userRecord = dataAccessObject.readUserProfile();
        sendToCloud = (Integer.parseInt(userRecord.getSendAllCloud())==1);

        //Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();

        //Get the location of the mouse click, get the caret offset, then from the offset get the line number
        int mouseClickLocation = primaryCaret.getOffset();
        int clickedLineNumber = document.getLineNumber(mouseClickLocation);

        //Check if the user clicked on a line that is potentially replaceable (i.e. import statement is in database)
        int currentLine = 0;
        int locationStartOfImport;
        int locationEndOfImport;

        while (currentLine < MavenListObjects.size()) {
            if (MavenListObjects.get(currentLine).getImportLocation() == clickedLineNumber) {
                from_library = MavenListObjects.get(currentLine).getImportLib();
                locationStartOfImport = MavenListObjects.get(currentLine).getFromlocation();
                locationEndOfImport = MavenListObjects.get(currentLine).getTolocation();
                int finalFrom_library = from_library;

                ReplacementDialog replacementDialog =new ReplacementDialog(MavenListObjects.get(currentLine).getDomainName(), MavenListObjects.get(currentLine).getImportDomain(),MavenListObjects.get(currentLine).getImportLib());

              //  int finalLocationStartOfImport = locationStartOfImport;
                int finalLocationStartOfImport = document.getLineStartOffset(clickedLineNumber - 1);
                int finalLocationEndOfImport = locationEndOfImport;
                finalLocationEndOfImport = document.getLineEndOffset(clickedLineNumber + 4);
                String finalClass_name = class_name;
                int finalLocationEndOfImport1 = finalLocationEndOfImport;
                int finalLocationEndOfImport2 = finalLocationEndOfImport;
                WindowAdapter adapter = new WindowAdapter() {

                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        String finalChoice = replacementDialog.getMavenReturned();

                        to_library = replacementDialog.getto_library();
                        full_lib_list = replacementDialog.getSelectionLibrary();
                        libraryName = replacementDialog.getLibraryname();

                        if (finalChoice.equals("None") == false) {
                            try {
                                finalChoice = dataAccessObject.readMavenVersion(finalChoice,2);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(finalLocationStartOfImport, finalLocationEndOfImport1, finalChoice1));

                            try {
                                detectMaven(editor, psiFile, project_name);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }


                        }

                        try {

                            DatabaseAccess dataAccessObject = new DatabaseAccess();
                            ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, action_date, clickedLineNumber, project_name, finalClass_name, full_lib_list, finalFrom_library, to_library);
                            dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);

                        }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        String finalChoice = replacementDialog.getMavenReturned();
                        to_library = replacementDialog.getto_library();
                        full_lib_list = replacementDialog.getSelectionLibrary();
                        libraryName = replacementDialog.getLibraryname();

                        if (finalChoice.equals("None") == false) {

                            try {
                                finalChoice = dataAccessObject.readMavenVersion(finalChoice, 2);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                            String finalChoice1 = finalChoice;

                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(finalLocationStartOfImport, finalLocationEndOfImport2, finalChoice1));

                            try {
                                detectMaven(editor, psiFile, project_name);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }


                            try {
                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, action_date, clickedLineNumber, project_name, finalClass_name, full_lib_list, finalFrom_library, to_library);
                                dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);

                            }
                            catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        try {

                            DatabaseAccess dataAccessObject = new DatabaseAccess();
                            ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, action_date, clickedLineNumber, project_name, finalClass_name, full_lib_list, finalFrom_library, to_library);
                            dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);

                        }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                };
                replacementDialog.addWindowListener(adapter);
                replacementDialog.addWindowFocusListener(adapter);
                replacementDialog.setVisible(true);
            }
            currentLine = currentLine + 1;
        }
    }



    public void replaceRequestedDependency(@NotNull final AnActionEvent event) throws ParseException {

        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);

        String project_name = project.getName();
        String class_name = "groovy Class";

        Date action_date = new Date();
        int from_library;

        User userRecord = new User();
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        userRecord = dataAccessObject.readUserProfile();
        sendToCloud = (Integer.parseInt(userRecord.getSendAllCloud())==1);

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
                from_library = DependListObjects.get(currentLine).getImportLib();
                locationStartOfImport = DependListObjects.get(currentLine).getFromlocation();
                locationEndOfImport = DependListObjects.get(currentLine).getTolocation();
                int finalFrom_library = from_library;

                ReplacementDialog replacementDialog =new ReplacementDialog(DependListObjects.get(currentLine).getDomainName(), DependListObjects.get(currentLine).getImportDomain(),DependListObjects.get(currentLine).getImportLib());

                int finalLocationStartOfImport = locationStartOfImport;
                int finalLocationEndOfImport = locationEndOfImport;
                finalLocationEndOfImport = document.getLineEndOffset(clickedLineNumber);
                String finalClass_name = class_name;
                int finalLocationEndOfImport1 = finalLocationEndOfImport;
                int finalLocationEndOfImport2 = finalLocationEndOfImport;
                WindowAdapter adapter = new WindowAdapter() {

                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        String finalChoice = replacementDialog.getMavenReturned();

                        to_library = replacementDialog.getto_library();
                        full_lib_list = replacementDialog.getSelectionLibrary();
                        libraryName = replacementDialog.getLibraryname();

                        if (finalChoice.equals("None") == false) {
                            try {
                                finalChoice = dataAccessObject.readMavenVersion(finalChoice, 1);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(finalLocationStartOfImport, finalLocationEndOfImport1, finalChoice1));

                            try {
                                detectDependancy(editor, psiFile, project_name);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }


                        }

                        try {

                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, action_date, clickedLineNumber, project_name, finalClass_name, full_lib_list, finalFrom_library, to_library);
                                dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);

                        }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        String finalChoice = replacementDialog.getMavenReturned();
                        to_library = replacementDialog.getto_library();
                        full_lib_list = replacementDialog.getSelectionLibrary();
                        libraryName = replacementDialog.getLibraryname();

                        if (finalChoice.equals("None") == false) {

                            try {
                                finalChoice = dataAccessObject.readMavenVersion(finalChoice, 1);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                            String finalChoice1 = finalChoice;

                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(finalLocationStartOfImport, finalLocationEndOfImport2, finalChoice1));

                            try {
                                detectDependancy(editor, psiFile, project_name);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }


                            try {
                                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                                    ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, action_date, clickedLineNumber, project_name, finalClass_name, full_lib_list, finalFrom_library, to_library);
                                    dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);

                            }
                            catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        try {

                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, action_date, clickedLineNumber, project_name, finalClass_name, full_lib_list, finalFrom_library, to_library);
                                dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);

                        }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                };
                replacementDialog.addWindowListener(adapter);
                replacementDialog.addWindowFocusListener(adapter);
                replacementDialog.setVisible(true);
            }
            currentLine = currentLine + 1;
        }
    }

    public Boolean domainsEnabled (int domain)
    {
        Boolean enabledDomain = false;

        if (domain < 2) { enabledDomain = true; }

        return enabledDomain;
    }
    /**
     * The detectImportStatementMethod will got through the current open file and test the import statements to see if they are in the database
     * The trigger for this to occur is a right click anywhere in the editor, I have yet to figure out how to have it work onLoad
     * Right now, the library being queried is the "last word" of the import statement
     * ex. for my.import.statement.rehab, the term queried against in the database is "rehab"
     */
    public void detectImports(@NotNull final PsiFile psiFile, @NotNull final Editor editor, @NotNull final  String project_name ) {

        try {
            final MarkupModel editorModel = editor.getMarkupModel();
            final Document document = editor.getDocument();
            PsiJavaFile javaFile = (PsiJavaFile)psiFile;

            TextAttributes attributes =
                    EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.BREAKPOINT_ATTRIBUTES);
            TextAttributes softerAttributes = attributes.clone();

            PsiImportList importList = javaFile.getImportList();
            if (importList == null) {

                return;
            }

            ImportListObjects.clear();
            editorModel.removeAllHighlighters();

            int locationLastWord = 0;
            int importLineNumber;

            for (PsiImportStatementBase importStatementObject : importList.getAllImportStatements()){

                //get location of import statement
                String TermSelected = importStatementObject.getImportReference().getReferenceName();
                locationLastWord = importStatementObject.getImportReference().getTextOffset();
                importLineNumber = document.getLineNumber(locationLastWord);

                //check database
                DatabaseAccess dataAccessObject = new DatabaseAccess();
                ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(TermSelected);

                if (choicesArray.size()>0){
                    ImportStatement impObj = new ImportStatement();
                    impObj.setImportListBase(importStatementObject);
                    impObj.setImportLocation(importLineNumber);
                    impObj.setImportLib(Integer.parseInt(choicesArray.get(0)));
                    impObj.setImportDomain(Integer.parseInt(choicesArray.get(1)));
                    impObj.setDomainName(choicesArray.get(2));
                    if (dataAccessObject.isEnabled(Integer.parseInt(choicesArray.get(1)), project_name)) {
                        impObj.setEnableddomain(false);
                    }
                    else {
                        impObj.setEnableddomain(true);
                    }
                    ImportListObjects.add(impObj);

                    if (impObj.getEnableddomain()) {
                        editorModel.addLineHighlighter(importLineNumber,
                        DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1,softerAttributes);
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void detectImportOnAction(@NotNull final AnActionEvent event) throws IOException {

         PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
         Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
         final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
         String project_name = project.getName();

         try {
             detectImports(psiFile, editor, project_name);
         }
         catch(Exception e) {
             e.printStackTrace();
         }
    }
}
