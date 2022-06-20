laraImport("weaver.util.ActionAwareCache");
laraImport("weaver.Weaver");

const data = {foo: "bar"}
const testCache = new ActionAwareCache(data);

println("DATA BEFORE ACTION: ");
printlnObject(testCache.data);

Weaver.getWeaverEngine().getRootJp().report();

println("DATA AFTER ACTION: ");
printlnObject(testCache.data);

