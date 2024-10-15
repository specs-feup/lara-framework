import { laraImport } from "@specs-feup/lara/api/lara/core/LaraCore.js";

laraImport("LaraImportFromJsJs")
laraImport("LaraImportFromJsMjs")

var fromJs = new LaraImportFromJsJs()
fromJs.print()

var fromMjs = new LaraImportFromJsMjs()
fromMjs.print()