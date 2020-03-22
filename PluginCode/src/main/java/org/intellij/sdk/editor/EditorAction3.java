package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


public class EditorAction3 extends AnAction {
    public EditorAction3() {
        //  JOptionPane.showMessageDialog(null,"message 1");
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {

        // JOptionPane.showMessageDialog(null,"message 2");
        UserProfile2 f = new UserProfile2("User Profile");
        f.init();

    }
}
