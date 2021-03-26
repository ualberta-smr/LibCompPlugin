package smr.cs.ualberta.libcomp.data;

public class DependencyStatement extends Statement {
    public DependencyStatement() {}

    public Integer fromlocation;
    public Integer tolocation;

    public Integer getFromlocation() { return fromlocation; }
    public Integer getTolocation() { return tolocation; }

    public void setFromlocation(Integer fromlocation) { this.fromlocation = fromlocation; }
    public void setTolocation(Integer tolocation) { this.tolocation = tolocation; }
}
