import com.intellij.openapi.module.ModuleComponent;

// this class is not used yet, I am still experimenting with it

public class ModuleClassComponent implements ModuleComponent {

    @Override
    public void initComponent() {
        int x = 1;
        System.out.println("ModuleClassComponent.initComponent");
    }
}
