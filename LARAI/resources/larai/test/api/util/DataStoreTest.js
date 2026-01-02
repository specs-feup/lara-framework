import DataStore from "@specs-feup/lara/api/lara/util/DataStore.js";
import WeaverOptions from "@specs-feup/lara/api/weaver/WeaverOptions.js";

const dataStore = new DataStore(WeaverOptions.getData());
const keyName = "debug";
const originalValue = dataStore.get(keyName);

console.log("GET:" + originalValue);
console.log("TYPE:" + dataStore.getType(keyName));
dataStore.put(keyName, !originalValue);
console.log("GET AFTER PUT:" + dataStore.get(keyName));
dataStore.put(keyName, originalValue);

console.log("DataStore Context folder: " + dataStore.getContextFolder());
