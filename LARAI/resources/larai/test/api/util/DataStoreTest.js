import lara.util.DataStore;
import weaver.WeaverOptions;

aspectdef DataStoreTest
	
	
	var dataStore = new DataStore(WeaverOptions.getData());
	println("GET:" + dataStore.get("javascript"));
	println("TYPE:" + dataStore.getType("javascript"));
	dataStore.put("javascript", false);
	println("GET AFTER PUT:" + dataStore.get("javascript"));
	dataStore.put("javascript", true);
	
	println("DataStore Context folder: " + dataStore.getContextFolder());
end
