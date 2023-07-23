const prefix = "lara-js/api/";
const coreImports = ["core/output.js", "lara/core/LaraCore.js"];

for (const coreImport of coreImports) {
  const foo = Object.entries(await import(prefix + coreImport));
  foo.forEach(([key, value]) => {
    // @ts-ignore
    globalThis[key] = value;
  });
}
