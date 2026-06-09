import { jest } from "@jest/globals";

declare global {
  var __SHARED_MODULE__: typeof import("java");
}

jest.mock("java", () => global.__SHARED_MODULE__);
