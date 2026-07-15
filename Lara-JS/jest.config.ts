import { defineConfig } from "jest";

export default defineConfig({
  preset: "ts-jest/presets/default-esm",
  testEnvironment: "node",
  moduleNameMapper: {
    "(.+)\\.js": "$1",
  },
  notify: true,
  notifyMode: "always",
  //verbose: true,
  collectCoverage: false,
  coverageDirectory: "coverage",
  coverageReporters: ["text", "lcov"],
  collectCoverageFrom: ["**/*[^.d].(t|j)s"],
  coverageProvider: "v8",
  projects: ["api", "code"],
});
