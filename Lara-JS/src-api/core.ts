const prefix = "lara-js/api/core/";
const coreImports = ["output.js"];

for (const coreImport of coreImports) {
  const foo = Object.entries(await import(prefix + coreImport));
  foo.forEach(([key, value]) => {
    // @ts-ignore
    globalThis[key] = value;
  });
}
