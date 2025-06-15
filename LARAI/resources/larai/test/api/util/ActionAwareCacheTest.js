import ActionAwareCache from "@specs-feup/lara/api/weaver/util/ActionAwareCache.js";
import Weaver from "@specs-feup/lara/api/weaver/Weaver.js";
import { printlnObject } from "@specs-feup/lara/api/core/output.js";

const data = {foo: "bar"}
const testCache = new ActionAwareCache(data);

console.log("DATA BEFORE ACTION: ");
printlnObject(testCache.data);

Weaver.getWeaverEngine().getRootJp().report();

console.log("DATA AFTER ACTION: ");
printlnObject(testCache.data);

