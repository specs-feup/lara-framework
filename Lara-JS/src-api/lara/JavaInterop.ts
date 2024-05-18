import JavaTypes, { JavaClasses } from "./util/JavaTypes.js";

export default class JavaInterop {
  /**
   * Converts a JS array into a java.util.List.
   */
  static arrayToList<T>(array: Array<T>): JavaClasses.List<T> {
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
   *
   * @deprecated Use JavaTypes.instanceOf instead
   */
  static isInstance<T>(value: T, classname: string) {
    return JavaTypes.instanceOf(value, classname);
  }

  static isList<T>(value: T) {
    return JavaTypes.instanceOf(value, "java.util.List");
  }

  /**
   * @deprecated Use JavaTypes instead
   */
  static getClass(classname: string) {
    return Java.type(classname).class;
  }
}
