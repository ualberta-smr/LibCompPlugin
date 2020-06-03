package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The ActionUserProfile class is the action for sending feedback entries to the database
 * This is triggered by the user opening the user profile dialog via the Tools menu
 */

public class ActionUserProfile extends AnAction {

    public ActionUserProfile() {
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        DialogUserProfile dialogUserProfile = new DialogUserProfile("User Profile");
        dialogUserProfile.init();

    }
}
