laraImport("lara.util.Replacer");
/*
let countBefore = 0;
for (const key in globalThis) {
  countBefore++;
}
println("Global this # keys before:" + countBefore);
*/
laraImport("lara.util.PrintOnce");

// Antes desta chamada terminar, é feito o eval para fazer import e colocar no globalThis

// ... mas uma chamada ao PrintOnce aqui dá erro, ainda não está no globalThis
/*
let countAfter = 0;
for (const key in globalThis) {
  countAfter++;
}
println("Global this # keys after:" + countAfter);
*/
// Só depois disto terminar é que coloca no globalThis

PrintOnce.message("a");
PrintOnce.message("b");
PrintOnce.message("a");
