import Weaver from "@specs-feup/lara/api/weaver/Weaver.js";

const ArrayList = Java.type('java.util.ArrayList');
const javaList = new ArrayList();
javaList.add(42);
javaList.add(13);	
const arrayFromList = Weaver.toJs(javaList);
console.log(arrayFromList.map(number => number + 1));
