laraImport("lara.graphs.Graphs");
laraImport("lara.graphs.NodeData");
laraImport("lara.graphs.EdgeData");

var graph = Graphs.newGraph();

var node1 = Graphs.addNode(graph, { a: "test", b: true });
println("Node 1 id() is string: " + (typeof node1.id() === "string"));
println("Node 1 data() is NodeData: " + (node1.data() instanceof NodeData));
println(
  "Node 1 data correct: " +
    (node1.data().a === "test" && node1.data().b === true)
);

var node2Data = new NodeData();
node2Data.a = "test2";
node2Data.b = 10;
var node2 = Graphs.addNode(graph, node2Data);
println("Node 2 id() is string: " + (typeof node2.id() === "string"));
println("Node 2 data() is NodeData: " + (node2.data() instanceof NodeData));
println(
  "Node 2 data correct: " +
    (node2.data().a === "test2" && node2.data().b === 10)
);

var edge1 = Graphs.addEdge(graph, node1, node2, { type: "raw" });
println("Edge 1 id() is string: " + (typeof edge1.id() === "string"));
println("Edge 1 data() is EdgeData: " + (edge1.data() instanceof EdgeData));
println("Edge 1 data correct: " + (edge1.data().type === "raw"));

var edge2Data = new EdgeData();
edge2Data.type = "managed";
var edge2 = Graphs.addEdge(graph, node1, node2, edge2Data);
println("Edge 2 id() is string: " + (typeof edge2.id() === "string"));
println("Edge 2 data() is EdgeData: " + (edge2.data() instanceof EdgeData));
println("Edge 2 data correct: " + (edge2.data().type === "managed"));
