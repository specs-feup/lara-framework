import TupleId from "@specs-feup/lara/api/lara/util/TupleId.js";

const tupleId = new TupleId();

console.log(tupleId.getId("a", "b"));
console.log(tupleId.getId("a", "b", "c"));
console.log(tupleId.getId("a", "b"));
console.log(tupleId.getId("a", "hasOwnProperty", "d"));
console.log(tupleId.getId("a", "hasOwnProperty", "d"));
console.log(tupleId.getId("a", "hasOwnProperty"));

console.log("Tuples:");
const tuples = tupleId.getTuples();
for (let key in tuples) {
    console.log(key + " -> " + tuples[key]);
}
