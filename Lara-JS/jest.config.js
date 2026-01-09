const config = {
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
  projects: ["src-api", "src-code"],
};

export default config;
