import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.intellij.sdk.editor.EditorIllustrationAction;
import org.intellij.sdk.editor.SelectRecords;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class MainClassPlugin implements ProjectComponent, PersistentStateComponent<MainClassPlugin> {
    @Override

    public void initComponent() {
        int x = 1;
    }

    private static void Loadingpopup() {
        JLabel messageLabel = new JLabel("Loading Library");
        Timer timer = new Timer(5000,
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        SwingUtilities.getWindowAncestor(messageLabel).dispose();
                    }
                });

        // Check if new version of data exists
        SelectRecords dataAccessObject = new SelectRecords();
        dataAccessObject.updateVersionData();

        timer.setRepeats(false);
        timer.start();
        JOptionPane.showMessageDialog(null, messageLabel, "Library Comparison", JOptionPane.NO_OPTION);
    }

    @Override
    public void projectOpened() {
        int x = 1;
        // creating timer task
        SwingUtilities.invokeLater(() -> {
            Loadingpopup();
            EditorIllustrationAction actionPerformed;
            actionPerformed = new EditorIllustrationAction();
            //Return later: actionPerformed.detectAllOpenEditors();
        });
    }

    @Override
    public void loadState(@NotNull MainClassPlugin state) {
         // JOptionPane.showMessageDialog(null, "State 1");
        XmlSerializerUtil.copyBean(state, this);
    }

    @Override
    public MainClassPlugin getState() {
        return this;
    }
}


