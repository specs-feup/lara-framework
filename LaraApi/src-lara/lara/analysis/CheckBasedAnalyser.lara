import lara.analysis.Analyser;
import lara.analysis.Checker;
import lara.analysis.CheckResult;
import lara.analysis.ResultFormatManager;
import weaver.Query;

// Analyser that scan code to detect unsafe functions

var CheckBasedAnalyser = function() {
    Analyser.call(this);

    this.checkers = [];
    this.resultFormatManager = new ResultFormatManager();
    this.unsafeCounter = 0;
    this.warningCounter = 0;
    this.fixFlag = 0;
};

CheckBasedAnalyser.prototype = Object.create(Analyser.prototype);

CheckBasedAnalyser.prototype.addChecker = function(checker) {
    this.checkers.push(checker);
}

CheckBasedAnalyser.prototype.enableFixing = function() {
    this.fixFlag = 1;
}

CheckBasedAnalyser.prototype.disableFixing = function() {
    this.fixFlag = 0;
}

/**
* Check file for unsafe functions, each one of them being specified by a checker
* @param {JoinPoint} $startNode
* @return fileResult
*/
CheckBasedAnalyser.prototype.analyse = function($startNode) {
    // Analyser based on a list of checkers, each one of them is designed to spot one type of function.
    // The analysis is performed node by node.

    var checkResultList = [];
    if ($startNode === undefined) {
        $startNode = Query.root();
    }
    for (var $node of Query.searchFrom($startNode)) {
        for (var checker of this.checkers) {
        	var result = checker.check($node);
        	if (result === undefined) {
			       continue;
        	}
        	checkResultList.push(result);
        }
    }
    // We have now a list of checker's name each leading to a list of CheckResult

    if (this.fixFlag == 1) {
        for(var checkResult of checkResultList) {
            checkResult.performFix();
        }
    }
    this.resultFormatManager.setAnalyserResultList(checkResultList);
    var fileResult = this.resultFormatManager.formatResultList($startNode);
    if (fileResult === undefined) {
		return;
    }
    return fileResult;

}
