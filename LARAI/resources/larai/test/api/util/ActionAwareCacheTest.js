import ActionAwareCache from "@specs-feup/lara/api/weaver/util/ActionAwareCache.js";
import Weaver from "@specs-feup/lara/api/weaver/Weaver.js";

const data = {foo: "bar"}
const testCache = new ActionAwareCache(data);

println("DATA BEFORE ACTION: ");
printlnObject(testCache.data);

Weaver.getWeaverEngine().getRootJp().report();

println("DATA AFTER ACTION: ");
printlnObject(testCache.data);

