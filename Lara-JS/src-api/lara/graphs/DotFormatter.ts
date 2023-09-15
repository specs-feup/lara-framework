import cytoscape from "../../libs/cytoscape-3.26.0.js";
import NodeData from "./NodeData.js";
import EdgeData from "./EdgeData.js";

export interface NodeAttribute {
  attr: string;
  predicate: (node: cytoscape.NodeSingular) => boolean;
}

export interface EdgeAttribute {
  attr: string;
  predicate: (node: cytoscape.EdgeSingular) => boolean;
}

export default class DotFormatter {
  // Array of objects that contains the properties 'attr' (string) and 'predicate' (function)
  private nodeAttrs: NodeAttribute[] = [];

  // Array of objects that contains the properties 'attr' (string) and 'predicate' (function)
  private edgeAttrs: EdgeAttribute[] = [];

  // Function that receives a node and returns the corresponding label. By default, call .toString() over the data
  private nodeLabelFormatter: (node: cytoscape.NodeSingular) => string = (
    node
  ) => (node.data() as NodeData).toString();

  // Function that receives an edge and returns the corresponding label. By default, call .toString() over the data
  private edgeLabelFormatter: (edge: cytoscape.EdgeSingular) => string = (
    edge
  ) => (edge.data() as EdgeData).toString();

  private static sanitizeDotLabel(label: string) {
    return label.replaceAll("\n", "\\l").replaceAll("\r", "");
  }

  addNodeAttribute(
    attrString: string,
    predicate: (node: cytoscape.NodeSingular) => boolean = () => true
  ) {
    this.nodeAttrs.push({ attr: attrString, predicate: predicate });
  }

  addEdgeAttribute(
    attrString: string,
    predicate: (edge: cytoscape.EdgeSingular) => boolean = () => true
  ) {
    this.edgeAttrs.push({ attr: attrString, predicate: predicate });
  }

  setNodeLabelFormatter(
    nodeLabelFormatter: (node: cytoscape.NodeSingular) => string
  ) {
    this.nodeLabelFormatter = nodeLabelFormatter;
  }

  setEdgeLabelFormatter(
    edgeLabelFormatter: (edge: cytoscape.EdgeSingular) => string
  ) {
    this.edgeLabelFormatter = edgeLabelFormatter;
  }

  getNodeAttributes(node: cytoscape.NodeSingular) {
    return this.nodeAttrs
      .filter((obj) => obj.predicate(node))
      .map((obj) => obj.attr)
      .join(" ");
  }

  getEdgeAttributes(edge: cytoscape.EdgeSingular) {
    return this.edgeAttrs
      .filter((obj) => obj.predicate(edge))
      .map((obj) => obj.attr)
      .join(" ");
  }

  getNodeLabel(node: cytoscape.NodeSingular) {
    return DotFormatter.sanitizeDotLabel(this.nodeLabelFormatter(node));
  }

  getEdgeLabel(edge: cytoscape.EdgeSingular) {
    return DotFormatter.sanitizeDotLabel(this.edgeLabelFormatter(edge));
  }
}
