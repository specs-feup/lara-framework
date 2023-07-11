import JavaTypes from "./util/JavaTypes.js";

/**
 * @class
 */
export default class JavaInterop {

    /**
    * 		Converts a JS array into a java.util.List.
    **/
    static arrayToList(array: Array<any>) {
     
        const ArrayListClass = JavaTypes.getJavaArrayList();
        const list = new ArrayListClass();
     
        for(const index in array) {
         
            const element = array[index];
            list.add(element);
        }
     
        return list;
    };
 
    /**
    * 		Converts a JS array into a java.util.List where all objects are Strings.
    **/
    static arrayToStringList(array: Array<any>) {
        return JavaInterop.arrayToList(array.map(value => value.toString()));
    }
 
    /**
    * @param {object} value - value to test
    * @param {String} classname - the full qualified name of the Java class of the value 
    */
    static isInstance(value: any, classname: string) {

        if( value !== undefined){
            return value instanceof Java.type(classname);
        }
    }
 
 
    isList(value: any) {
        return JavaInterop.isInstance(value, "java.util.List");
    }
 
 
    getClass(classname: string) {
     
        return Java.type(classname).class;
    }
}