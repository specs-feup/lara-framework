function foo() {
    bar();
}

function bar() {
    throw new Error("throwing exception in bar()");
}

foo();