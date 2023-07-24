laraImport("lara.util.Replacer");

/*
let countBefore = 0;
for (const key in globalThis) {
  countBefore++;
}
println("Global this # keys before:" + countBefore);
*/
globalThis["testing"] = "Test string";

laraImport("lara.util.PrintOnce");
//await laraImport("lara.util.PrintOnce");
//LaraI.evalMjs("await laraImport('lara.util.PrintOnce');");


/*
let countAfter = 0;
for (const key in globalThis) {
  countAfter++;
}
println("Global this # keys after:" + countAfter);
*/

PrintOnce.message("a");
PrintOnce.message("b");
PrintOnce.message("a");
