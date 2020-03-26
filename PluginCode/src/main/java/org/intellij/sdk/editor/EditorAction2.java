package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


public class EditorAction2 extends AnAction {
    public EditorAction2() {
      //  JOptionPane.showMessageDialog(null,"message 1");
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {

        // JOptionPane.showMessageDialog(null,"message 2");
        UserProfile f = new UserProfile("User Profile");
        f.init();

    }
}
