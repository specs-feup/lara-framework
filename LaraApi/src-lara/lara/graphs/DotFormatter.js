export default class DotFormatter {
    // Array of objects that contains the properties 'attr' (string) and 'predicate' (function)
    nodeAttrs = [];
    // Array of objects that contains the properties 'attr' (string) and 'predicate' (function)
    edgeAttrs = [];
    // Function that receives a node and returns the corresponding label. By default, call .toString() over the data
    nodeLabelFormatter = (node) => node.data().toString();
    // Function that receives an edge and returns the corresponding label. By default, call .toString() over the data
    edgeLabelFormatter = (edge) => edge.data().toString();
    static sanitizeDotLabel(label) {
        return label.replaceAll("\n", "\\l").replaceAll("\r", "");
    }
    addNodeAttribute(attrString, predicate = () => true) {
        this.nodeAttrs.push({ attr: attrString, predicate: predicate });
    }
    addEdgeAttribute(attrString, predicate = () => true) {
        this.edgeAttrs.push({ attr: attrString, predicate: predicate });
    }
    setNodeLabelFormatter(nodeLabelFormatter) {
        this.nodeLabelFormatter = nodeLabelFormatter;
    }
    setEdgeLabelFormatter(edgeLabelFormatter) {
        this.edgeLabelFormatter = edgeLabelFormatter;
    }
    getNodeAttributes(node) {
        return this.nodeAttrs
            .filter((obj) => obj.predicate(node))
            .map((obj) => obj.attr)
            .join(" ");
    }
    getEdgeAttributes(edge) {
        return this.edgeAttrs
            .filter((obj) => obj.predicate(edge))
            .map((obj) => obj.attr)
            .join(" ");
    }
    getNodeLabel(node) {
        return DotFormatter.sanitizeDotLabel(this.nodeLabelFormatter(node));
    }
    getEdgeLabel(edge) {
        return DotFormatter.sanitizeDotLabel(this.edgeLabelFormatter(edge));
    }
}
//# sourceMappingURL=DotFormatter.js.map