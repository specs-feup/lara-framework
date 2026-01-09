import Weaver from "@specs-feup/lara/api/weaver/Weaver.js";
import JavaTypes from "@specs-feup/lara/api/lara/util/JavaTypes";

const ArrayList = JavaTypes.ArrayList;
const javaList = new ArrayList();
javaList.add(42);
javaList.add(13);	
const arrayFromList = Weaver.toJs(javaList);
console.log(arrayFromList.map(number => number + 1));
