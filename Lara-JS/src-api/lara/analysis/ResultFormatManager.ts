import lara.Io;
import weaver.Query;
import lara.analysis.ResultList;
import lara.analysis.CheckResult;

// Class to format the results from the analyser into a resultList 

var ResultFormatManager = function() {
	this.analyserResultList = {};
};

/**
* Create a new ResultList object with the filename 
* @param {JoinPoint} $file
* @return resultList
*/
ResultFormatManager.prototype.formatResultList = function($file) {
	if (Object.entries(this.analyserResultList).length === 0) {
    		return;
	}
	var resultList = new ResultList($file.name);
	for (var analyserResult of this.analyserResultList) {
		if (analyserResult.name === undefined) {
			continue;
		}
		resultList.append(analyserResult);
	}
	return resultList;
}

ResultFormatManager.prototype.setAnalyserResultList = function(analyserResultList) {
	this.analyserResultList = analyserResultList;
}

