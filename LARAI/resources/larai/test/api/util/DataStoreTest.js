import DataStore from "@specs-feup/lara/api/lara/util/DataStore.js";
import WeaverOptions from "@specs-feup/lara/api/weaver/WeaverOptions.js";

const dataStore = new DataStore(WeaverOptions.getData());
console.log("GET:" + dataStore.get("javascript"));
console.log("TYPE:" + dataStore.getType("javascript"));
dataStore.put("javascript", false);
console.log("GET AFTER PUT:" + dataStore.get("javascript"));
dataStore.put("javascript", true);

console.log("DataStore Context folder: " + dataStore.getContextFolder());
