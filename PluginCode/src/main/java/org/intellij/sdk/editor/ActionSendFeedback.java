package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * The ActionSendFeedback class is the action for sending feedback entries to the database
 * This is triggered by the user opening the send feedback dialog via the Tools menu
 */

public class ActionSendFeedback extends AnAction {

    public ActionSendFeedback() {
    }

    public void update(@NotNull final AnActionEvent event) {
            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(true);
    }


    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        DialogSendFeedback dialogSendFeedback = new DialogSendFeedback("User Profile");
        dialogSendFeedback.init();
    }
}
