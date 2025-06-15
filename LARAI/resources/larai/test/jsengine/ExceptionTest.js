function foo() {
    bar();
}

function bar() {
    throw "throwing exception in bar()";
}

foo();