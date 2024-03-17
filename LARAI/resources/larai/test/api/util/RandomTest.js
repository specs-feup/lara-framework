laraImport("lara.util.Random");

const rand = new Random();
// Just to test it works
rand.next();


const randSeed1 = new Random(1);
println(randSeed1.next());
println(randSeed1.next());
println(randSeed1.next());
println(randSeed1.next());


const randSeed2 = new Random(1678485728385);
println(randSeed2.next());
println(randSeed2.next());
println(randSeed2.next());
println(randSeed2.next());