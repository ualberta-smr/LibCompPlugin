package smr.cs.ualberta.libcomp.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The ActionSendFeedback class is the action for sending feedback entries to the database
 * This is triggered by the user opening the send feedback dialog via the Tools menu
 */

public class SendFeedback extends AnAction {

    public SendFeedback() {
    }

    public void update(@NotNull final AnActionEvent event) {
            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(true);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        smr.cs.ualberta.libcomp.dialog.SendFeedback sendFeedback = new smr.cs.ualberta.libcomp.dialog.SendFeedback("User Profile");
        sendFeedback.init();
    }
}
