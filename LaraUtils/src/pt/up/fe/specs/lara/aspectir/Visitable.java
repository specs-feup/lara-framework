package pt.up.fe.specs.lara.aspectir;

/****************************** Interface Visitable ******************************/
public interface Visitable {
    public void accept(Visitor visitor);

    public void visitDepthFirst(Visitor visitor);
}
/********************************************************************************/
