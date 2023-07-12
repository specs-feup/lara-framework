import JavaTypes from "./util/JavaTypes.js";

export default class JavaInterop {
  /**
   * Converts a JS array into a java.util.List.
   */
  static arrayToList<T>(array: Array<T>) {
    const ArrayListClass = JavaTypes.ArrayList;
    const list = new ArrayListClass();

    array.forEach((element) => list.add(element));

    return list;
  }

  /**
   * Converts a JS array into a java.util.List where all objects are Strings.
   */
  static arrayToStringList<T>(array: Array<T>) {
    return JavaInterop.arrayToList(array.map((value) => String(value)));
  }

  /**
   * @param value - Value to test
   * @param classname - The full qualified name of the Java class of the value
   */
  static isInstance<T>(value: T, classname: string) {
    return value instanceof Java.type(classname);
  }

  isList<T>(value: T) {
    return JavaInterop.isInstance(value, "java.util.List");
  }

  getClass(classname: string) {
    return Java.type(classname).class;
  }
}
