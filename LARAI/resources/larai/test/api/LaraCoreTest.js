import { arrayFromArgs } from "@specs-feup/lara/api/lara/core/LaraCore.js";

function arrayFromArgsTest() {
    return arrayFromArgs(arguments);
}

function arrayFromArgsAfterOneTest() {
    return arrayFromArgs(arguments, 1);
}

// Single element
console.log(arrayFromArgsTest("Hello").length);

// Several elements
console.log(arrayFromArgsTest("Hello", "World").length);

// Single array
console.log(arrayFromArgsTest(["Hello", "World"]).length);

// Single element after 1
console.log(arrayFromArgsAfterOneTest("Hello").length);

// Several elements after 1
console.log(arrayFromArgsAfterOneTest("Hello", "World").length);
