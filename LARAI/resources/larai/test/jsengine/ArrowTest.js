const elements = [
    'Hydrogen',
    'Helium',
    'Lithium',
    'Beryllium'
];

// This statement returns the array: [8, 6, 7, 9]
console.log(elements.map(function(element) {return element.length;}));


// The regular function above can be written as the arrow function below
console.log(elements.map((element) => {
    return element.length;
})); // [8, 6, 7, 9]

// When there is only one parameter, we can remove the surrounding parentheses
console.log(elements.map(element => {
    return element.length;
})); // [8, 6, 7, 9]

// When the only statement in an arrow function is `return`, we can remove `return` and remove
// the surrounding curly brackets
console.log(elements.map(element => element.length)); // [8, 6, 7, 9]

console.log(elements.map(() => 10)); // [10, 10, 10, 10]



/*	
    // In this case, because we only need the length property, we can use destructuring parameter:
    // Notice that the `length` corresponds to the property we want to get whereas the
    // obviously non-special `lengthFooBArX` is just the name of a variable which can be changed
    // to any valid variable name you want
    //elements.map(({ length: lengthFooBArX }) => lengthFooBArX); // [8, 6, 7, 9]
    
    // This destructuring parameter assignment can also be written as seen below. However, note that in
    // this example we are not assigning `length` value to the made up property. Instead, the literal name
    // itself of the variable `length` is used as the property we want to retrieve from the object.
    //elements.map(({ length }) => length); // [8, 6, 7, 9] 
*/