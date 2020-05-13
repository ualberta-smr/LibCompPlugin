import com.intellij.openapi.components.*;
import org.intellij.sdk.editor.ActionReplacement;
import org.intellij.sdk.editor.DatabaseAccess;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Triggered on load
 * Checks if data must be updated
 */

public class MainClassPlugin implements ProjectComponent {

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
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        dataAccessObject.updateVersionData();

        timer.setRepeats(false);
        timer.start();

        //String current = PathManager.getPluginsPath();
        JOptionPane.showMessageDialog(null, messageLabel, "user.dir", JOptionPane.NO_OPTION);
    }

    @Override
    public void projectOpened() {
        int x = 1;
        // creating timer task
        SwingUtilities.invokeLater(() -> {
            Loadingpopup();
            ActionReplacement actionPerformed;
            actionPerformed = new ActionReplacement();
            //Return later: actionPerformed.detectAllOpenEditors();
        });
    }

    /*
    @Override
    public void loadState(@NotNull MainClassPlugin state) {
        // JOptionPane.showMessageDialog(null, "State 1");
        XmlSerializerUtil.copyBean(state, this);
    }

    @Override
    public MainClassPlugin getState() {
        return this;
    }*/
}


