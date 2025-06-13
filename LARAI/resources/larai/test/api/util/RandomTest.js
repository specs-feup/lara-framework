import Random from "@specs-feup/lara/api/lara/util/Random.js";

const rand = new Random();
// Just to test it works
rand.next();


const randSeed1 = new Random(1);
console.log(randSeed1.next());
console.log(randSeed1.next());
console.log(randSeed1.next());
console.log(randSeed1.next());


const randSeed2 = new Random(1678485728385);
console.log(randSeed2.next());
console.log(randSeed2.next());
console.log(randSeed2.next());
console.log(randSeed2.next());