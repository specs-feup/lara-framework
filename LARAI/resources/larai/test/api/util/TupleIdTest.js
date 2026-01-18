import TupleId from "@specs-feup/lara/api/lara/util/TupleId.js";

const tupleId = new TupleId();

console.log("id:", tupleId.getId("a", "b"));
console.log("id:", tupleId.getId("a", "b", "c"));
console.log("id:", tupleId.getId("a", "b"));
console.log("id:", tupleId.getId("a", "hasOwnProperty", "d"));
console.log("id:", tupleId.getId("a", "hasOwnProperty", "d"));
console.log("id:", tupleId.getId("a", "hasOwnProperty"));

console.log("Tuples:");
const tuples = tupleId.getTuples();
for (let key in tuples) {
    console.log(key + " -> " + tuples[key]);
}
