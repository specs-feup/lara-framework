# LARA Visualization Tool

Web tool for visualization and analysis of the AST and its source code.

## Integration

Internally, the tool follows a system for interaction with the compiler, to be able to apply code linkage and correction, among others, while still being independent of the specific compiler. This system is an implementation of the Factory Method pattern, and the integration with Clava is illustrated in the following diagram:

![Compiler Abstracted System](./compiler-abstracted-system.svg)

To integrate the tool in another compiler:

1. Implement the `GenericAstConverter` interface, with its functions properly implemented. More information can be found in their documentation.
2. Create a class that extends `GenericVisualizationTool`, and override `getAstConverter` so that it returns an instance of the class declared in the previous step.
3. Use an instance of the previous class as the entry point of the visualization tool API, for the compiler in question.

## Usage

After integration, and being `VisualizationTool` the extended derived class of `GenericVisualizationTool`, to launch or update the visualization tool, execute the following statement:

```js
await VisualizationTool.visualize();
```

Once ready, Clava will provide the URL that should be opened in the browser to access the web interface. The function can also change the AST root and URL domain and port.

Other properties will allow the user to know other important information from the server:

```js
VisualizationTool.isLaunched;  // true if the server is running
VisualizationTool.url;         // URL where the server is running
VisualizationTool.port;        // port to which the server is listening
VisualizationTool.hostname;    // hostname to which the server is listening
```

For more details, refer to the `GenericVisualizationTool` documentation.