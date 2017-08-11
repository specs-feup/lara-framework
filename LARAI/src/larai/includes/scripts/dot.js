
function Edge(s, d) {

    var source = s;
    var destination = d;
    var options = {};

    this.setOption = function (key, value) {

        options[key] = value;
    };

    this.toDot = function () {

        //alert('inside edge toDot');
        var code = '\t' + s + ' -> ' + d + '\n';
        return code;
    };
};

function Graph(n) {

    var name = n;
    var edges = [];
    var nodeOptions = {};

    /* Adds an edge to the graph. */
    this.addEdge = function () {

        switch (arguments.length) {

            case 1:
                this.addEdge.oneArg.apply(this, arguments);
                break;
            case 2:
                this.addEdge.twoArgs.apply(this, arguments);
                break;
            default:
                errorln("pfff");
        }
    };

    /* Adds an edge object to the edges array. */
    this.addEdge.oneArg = function (edge) {

        edges.push(edge);
    };

    /* Creates an edge and adds it to the edges array. */
    this.addEdge.twoArgs = function (s, d) {

        edges.push(new Edge(s, d));
    };

    /* Sets an option for a node. */
    this.setNodeOption = function (nodeName, key, value) {

        if (nodeOptions[nodeName] === undefined) {

            nodeOptions[nodeName] = {};
        }
        nodeOptions[nodeName][key] = value;
    };

    /* Prints the graph in dot format. */
    this.toDot = function () {

        //alert('inside toDot');
        var dot = 'digraph ' + name + ' {\n';

        for (var i = 0; i < edges.length; i++) {

            //alert('inside loop ' + edges.length);
            dot += edges[i].toDot();
        }

        for (var node in nodeOptions) {

            dot += '\t' + node + '[';

            var separator = '';
            for (var option in nodeOptions[node]) {

                dot += separator + option + '="' + nodeOptions[node][option] + '"';
                separator = ', ';
            }

            dot += ']\n';
        }

        dot += '}';

        //alert('inside toDot, before returning');
        return dot;
    };
};

