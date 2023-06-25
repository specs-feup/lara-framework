//import { coreFoo } from "core/coretest.js";
// @ts-ignore
//globalThis.coreFoo = coreFoo;
//console.log("HEY")
const coreImports = ["core/coretest.js"];
for (const coreImport of coreImports) {
    const foo = Object.entries(await import(coreImport));
    foo.forEach(([key, value]) => {
        // @ts-ignore
        globalThis[key] = value;
    });
}
export {};
//# sourceMappingURL=core.js.map