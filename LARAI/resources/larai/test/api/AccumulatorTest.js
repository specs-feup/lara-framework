laraImport("lara.util.Accumulator");

const acc = new Accumulator();

// Add as tuple
acc.add("step1", "step2");

// Add as array
acc.add(["step1", "step2"]);

// Add with common subchain
acc.add(["step1", "step2.1"]);

// Add just subchain
acc.add("step1");
acc.add("step1");
acc.add(["step1"]);

// Get by tuple
console.log("Tuple get:" + acc.get("step1", "step2"));

// Get by array
console.log("Array get:" + acc.get(["step1", "step2"]));

// Use keys to print contents
for(let key of acc.keys()) {
    console.log("key: " + key.join(", ") + " -> value: " + acc.get(key));
}
