import { laraImport } from "@specs-feup/lara/api/lara/core/LaraCore.js";

laraImport("LaraImportFromMjsJs");
laraImport("LaraImportFromMjsMjs");

var fromJs = new LaraImportFromMjsJs();
fromJs.print();

var fromMjs = new LaraImportFromMjsMjs();
fromMjs.print();
