import com.intellij.openapi.components.ProjectComponent;

// this class is not used yet, I am still experimenting with it

public class MainClassPlugin implements ProjectComponent {

    @Override
    public void initComponent() {
        int x = 1;
        System.out.println("MainClassPlugin.initComponent");
    }

    @Override
    public void projectOpened() {
   int x = 1;
        System.out.println("MainClassPlugin.projectOpened");
    }

}
