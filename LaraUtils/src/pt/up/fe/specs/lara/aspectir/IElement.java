package pt.up.fe.specs.lara.aspectir;

import java.io.PrintStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/****************************** Interface IElement ******************************/
public interface IElement extends Visitable {
    public void loadXml(Element e, String rootName, Document doc) throws Exception;

    public IElement getParent();

    public void writeXml(Document doc, Element parent, String rootName, int level);

    public void print(PrintStream os, int indent);

    public String typeName();

    public void printIndent(PrintStream os, int indent);

    /**
     * 
     * @param <T>
     * @param ancestorClass
     * @return an ancestor of the given class, or null if none was found
     */
    default <T extends IElement> T getAncestor(Class<T> ancestorClass) {
        IElement currentElement = this;
        IElement currentParent = null;
        while ((currentParent = currentElement.getParent()) != null) {
            if (ancestorClass.isInstance(currentParent)) {
                return ancestorClass.cast(currentParent);
            }

            currentElement = currentParent;
        }

        // No parent found
        return null;
    }
}
/********************************************************************************/
