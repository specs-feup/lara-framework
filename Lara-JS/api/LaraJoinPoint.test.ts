import {
  clearJoinpointMappers,
  getJoinpointMappers,
  registerJoinpointMapper,
  wrapJoinPoint,
} from "./LaraJoinPoint.ts";
import JavaTypes from "./lara/util/JavaTypes.ts";

// wrapJoinPoint on values that are not join points. Join points are covered by
// Clava-JS, which has a weaver with real join point classes.

const javaList = (...items: unknown[]) => {
  const list = new JavaTypes.ArrayList();
  for (const item of items) list.add(item);
  return list;
};
const DayOfWeek = JavaTypes.getType("java.time.DayOfWeek");
const Month = JavaTypes.getType("java.time.Month");

describe("wrapJoinPoint", () => {
  // Module state is shared between test files, so restore the mappers afterwards
  const savedMappers = [...getJoinpointMappers()];
  beforeEach(() => {
    clearJoinpointMappers();
    registerJoinpointMapper({});
  });
  afterAll(() => {
    clearJoinpointMappers();
    getJoinpointMappers().push(...savedMappers);
  });

  it("returns values unchanged when no mapper is registered", () => {
    clearJoinpointMappers();
    const list = javaList(1);
    expect(wrapJoinPoint(list)).toBe(list);
    expect(wrapJoinPoint(null)).toBe(null);
  });

  it("passes JS values through", () => {
    const plain = { a: 1 };
    const noProto = Object.create(null);
    expect(wrapJoinPoint(null)).toBeUndefined();
    expect(wrapJoinPoint(undefined)).toBeUndefined();
    expect(wrapJoinPoint(3)).toBe(3);
    expect(wrapJoinPoint("s")).toBe("s");
    expect(wrapJoinPoint(false)).toBe(false);
    expect(wrapJoinPoint(plain)).toBe(plain);
    expect(wrapJoinPoint(noProto)).toBe(noProto);
    expect(wrapJoinPoint([1, [null, "x"]])).toEqual([1, [undefined, "x"]]);
    expect(wrapJoinPoint(new Int32Array([1, 2]))).toEqual([1, 2]);
  });

  it("converts any Java List to an array of wrapped elements", () => {
    const LinkedList = JavaTypes.getType("java.util.LinkedList");
    const linked = new LinkedList();
    linked.add(DayOfWeek.FRIDAY);
    const unmodifiable = JavaTypes.Collections.unmodifiableList(javaList("u"));
    const listOf = JavaTypes.getType("java.util.List").of(DayOfWeek.MONDAY, "b");

    expect(wrapJoinPoint(javaList(1, "a", DayOfWeek.MONDAY))).toEqual([1, "a", "MONDAY"]);
    expect(wrapJoinPoint(javaList(javaList(DayOfWeek.SUNDAY), javaList()))).toEqual([
      ["SUNDAY"],
      [],
    ]);
    expect(wrapJoinPoint(linked)).toEqual(["FRIDAY"]);
    // JDK-internal classes: calling their methods through node-java aborts the process
    expect(wrapJoinPoint(unmodifiable)).toEqual(["u"]);
    expect(wrapJoinPoint(listOf)).toEqual(["MONDAY", "b"]);
  });

  it("converts Java enums to their string", () => {
    expect(wrapJoinPoint(DayOfWeek.MONDAY)).toBe("MONDAY");
    expect(wrapJoinPoint(Month.MAY)).toBe("MAY");
  });

  it("returns other Java objects as-is", () => {
    const file = new JavaTypes.File("a.txt");
    const map = new JavaTypes.HashMap();
    const optional = JavaTypes.getType("java.util.Optional").empty();
    const javaClass = file.getClass();

    expect(wrapJoinPoint(file)).toBe(file);
    expect(wrapJoinPoint(map)).toBe(map);
    expect(wrapJoinPoint(optional)).toBe(optional);
    expect(wrapJoinPoint(javaClass)).toBe(javaClass);
  });

  it("keeps per-class results apart across repeated, interleaved calls", () => {
    // Exercises the per-class cache: every class is seen many times, in mixed order
    const file = new JavaTypes.File("b.txt");
    const values = [
      javaList("x"),
      DayOfWeek.TUESDAY,
      file,
      Month.JUNE,
      javaList(),
      DayOfWeek.MONDAY,
      7,
    ];
    const expected = [["x"], "TUESDAY", file, "JUNE", [], "MONDAY", 7];
    for (let i = 0; i < 5; i++) {
      expect(values.map(wrapJoinPoint)).toEqual(expected);
      expect(values.toReversed().map(wrapJoinPoint)).toEqual(expected.toReversed());
    }
  });
});
