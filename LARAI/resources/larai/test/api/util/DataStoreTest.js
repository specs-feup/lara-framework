laraImport("lara.util.DataStore");
laraImport("weaver.WeaverOptions");

const dataStore = new DataStore(WeaverOptions.getData());
console.log("GET:" + dataStore.get("javascript"));
console.log("TYPE:" + dataStore.getType("javascript"));
dataStore.put("javascript", false);
console.log("GET AFTER PUT:" + dataStore.get("javascript"));
dataStore.put("javascript", true);

console.log("DataStore Context folder: " + dataStore.getContextFolder());
