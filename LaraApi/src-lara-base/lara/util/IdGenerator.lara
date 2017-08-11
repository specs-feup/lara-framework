
var IdGenerator = {
	idCounter : {}
};


IdGenerator.next = function(key) {

	if(key === undefined) {
		key = "";
	}

	var currentId = this.idCounter[key];

	if(currentId === undefined) {
		currentId = 0;
	}
	
	this.idCounter[key] = currentId + 1;
	
	return key + currentId;
}
