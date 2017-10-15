package pt.up.fe.specs.lara.doc.aspectir;

import java.util.List;

public class AspectIrDoc {

	private final List<AspectIrElement> aspectIrElements;
	
	public AspectIrDoc(List<AspectIrElement> aspectIrElements) {
		// TODO: Organize elements (e.g., separate into classes / functions, put together elements that belong to each other, etc.)
		this.aspectIrElements = aspectIrElements;
	}
	
	@Override
	public String toString() {
		return aspectIrElements.toString();
	}

}
