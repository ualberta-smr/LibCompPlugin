import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.actionSystem.TimerListener;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.intellij.sdk.editor.EditorIllustrationAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;


//@State(name = "MainClassPlugin", storages = {@Storage("$PROJECT_DIR$")})

//@State(name = "MainClassPlugin")
//@State(name = "MainClassPlugin", storages = StoragePathMacros.PRODUCT_WORKSPACE_FILE)

//@State(name = "MainClassPlugin", storages = {@Storage(StoragePathMacros.PRODUCT_WORKSPACE_FILE)})

public class MainClassPlugin implements ProjectComponent, PersistentStateComponent<MainClassPlugin> {


    @Override
    public void initComponent() {
        int x = 1;
      // JOptionPane.showMessageDialog(null, "MainClassPlugin.initComponent");

    }

    private static void Loadingpopup()
    {
        JLabel messageLabel = new JLabel("Loading Library Comparison, Please wait ...");
        Timer timer = new Timer(5000,
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent event)
                    {
                        SwingUtilities.getWindowAncestor(messageLabel).dispose();
                    }
                });
        timer.setRepeats(false);
        timer.start();
        JOptionPane.showMessageDialog(null, messageLabel, "Library Comparison", JOptionPane.NO_OPTION);
    }

    @Override
    public void projectOpened() {
        int x = 1;

        // creating timer task, timer

        SwingUtilities.invokeLater(() -> {

            Loadingpopup();

            Project proj= ProjectManager.getInstance().getOpenProjects()[0];
            FileEditorManager manager = FileEditorManager.getInstance(proj);

            VirtualFile files[] = manager.getSelectedFiles();
            FileEditor editorFile = manager.getSelectedEditor();

            PsiFile psiFile = PsiManager.getInstance(proj).findFile(files[0]);
            Editor editor = ((TextEditor)editorFile).getEditor();

            EditorIllustrationAction actionPerformed;
            actionPerformed = new EditorIllustrationAction();
            actionPerformed.detectionImports(psiFile, editor);
        });
    }

    @Override
    public void loadState(@NotNull MainClassPlugin state) {

         // JOptionPane.showMessageDialog(null, "State 1");

        //XmlSerializerUtil.copyBean(state, this);
    }

    @Override
    public MainClassPlugin getState() {
        return this;
    }
}


