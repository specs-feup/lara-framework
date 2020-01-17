var esprimaAst = esprima.parseScript(esprimaCode,{loc:true,comment:true});
var esprimaAstAsString = JSON.stringify(esprimaAst);
