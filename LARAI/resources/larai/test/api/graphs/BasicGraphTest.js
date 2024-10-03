laraImport("lara.graphs.Graphs");
laraImport("lara.graphs.NodeData");
laraImport("lara.graphs.EdgeData");

const graph = Graphs.newGraph();

const node1 = Graphs.addNode(graph, { a: "test", b: true });
console.log("Node 1 id() is string: " + (typeof node1.id() === "string"));
console.log("Node 1 data() is NodeData: " + (node1.data() instanceof NodeData));
console.log(
    "Node 1 data correct: " +
        (node1.data().a === "test" && node1.data().b === true)
);

let node2Data = new NodeData();
node2Data.a = "test2";
node2Data.b = 10;
const node2 = Graphs.addNode(graph, node2Data);
console.log("Node 2 id() is string: " + (typeof node2.id() === "string"));
console.log("Node 2 data() is NodeData: " + (node2.data() instanceof NodeData));
console.log(
    "Node 2 data correct: " +
        (node2.data().a === "test2" && node2.data().b === 10)
);

const edge1 = Graphs.addEdge(graph, node1, node2, { type: "raw" });
console.log("Edge 1 id() is string: " + (typeof edge1.id() === "string"));
console.log("Edge 1 data() is EdgeData: " + (edge1.data() instanceof EdgeData));
console.log("Edge 1 data correct: " + (edge1.data().type === "raw"));

let edge2Data = new EdgeData();
edge2Data.type = "managed";
const edge2 = Graphs.addEdge(graph, node1, node2, edge2Data);
console.log("Edge 2 id() is string: " + (typeof edge2.id() === "string"));
console.log("Edge 2 data() is EdgeData: " + (edge2.data() instanceof EdgeData));
console.log("Edge 2 data correct: " + (edge2.data().type === "managed"));
