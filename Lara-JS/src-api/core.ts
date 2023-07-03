//import { coreFoo } from "core/coretest.js";

// @ts-ignore
//globalThis.coreFoo = coreFoo;
//console.log("HEY")

const prefix = "lara-js/api/core/"
const coreImports = ["coretest.js"];

// Test
for(const coreImport of coreImports) {
    const foo = Object.entries(await import(prefix+coreImport));
    foo.forEach(([key, value]) => {
        // @ts-ignore
        globalThis[key] = value;
    });
}

