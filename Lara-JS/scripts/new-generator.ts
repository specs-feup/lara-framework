import * as ts from "typescript";
import { create } from "xmlbuilder2";
import * as fs from "fs";

// Function to parse a TypeScript file and extract class method details
function parseTypeScriptFile(filePath: string) {
    const sourceFile = ts.createSourceFile(
        filePath,
        fs.readFileSync(filePath, "utf8"),
        ts.ScriptTarget.Latest,
        true
    );

    const actions: any[] = [];

    function visit(node: ts.Node) {
        if (ts.isClassDeclaration(node) && node.name) {
            const className = node.name.text;
            node.members.forEach(member => {
                if (ts.isMethodDeclaration(member) && member.name) {
                    const methodName = (member.name as ts.Identifier).text;
                    const parameters = member.parameters.map(param => {
                        return {
                            name: param.name.getText(),
                            type: getType(param.type),
                        };
                    });
                    actions.push({ className, methodName, parameters });
                }
            });
        }
        ts.forEachChild(node, visit);
    }

    visit(sourceFile);
    return actions;
}

// Function to map TypeScript types to XML types
function getType(typeNode?: ts.TypeNode): string {
    if (!typeNode) return "string"; // Default type
    const typeText = typeNode.getText();
    const typeMap: { [key: string]: string } = {
        string: "string",
        number: "int",
        boolean: "boolean",
        any: "string",
    };
    return typeMap[typeText] || "string";
}

// Function to generate XML from extracted methods
function generateXML(actions: any[]): string {
    const root = create({ version: "1.0" }).ele("LangSpec");
    actions.forEach(action => {
        const actionNode = root.ele("action", {
            name: action.methodName,
            class: action.className,
            tooltip: `Generated action for ${action.methodName}`
        });
        action.parameters.forEach(param => {
            actionNode.ele("parameter", {
                name: param.name,
                type: param.type,
            });
        });
    });
    return root.end({ prettyPrint: true });
}

// Example usage
const filePath = "../src-api/LaraJoinPoint.ts"; // Change to your actual TypeScript file
const actions = parseTypeScriptFile(filePath);
const xmlOutput = generateXML(actions);
fs.writeFileSync("output.xml", xmlOutput);
console.log("Generated XML:", xmlOutput);
