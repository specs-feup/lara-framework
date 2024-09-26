const config = {
  preset: "ts-jest/presets/default-esm",
  testEnvironment: "@specs-feup/lara/jest/jestEnvironment.js",
  //globalSetup: "@specs-feup/lara/jest/jestGlobalSetup.js",
  //globalTeardown: "@specs-feup/lara/jest/jestGlobalTeardown.js",
  setupFiles: ["@specs-feup/lara/jest/setupFiles/sharedJavaModule.js"],
  moduleNameMapper: {
    "@specs-feup/lara/api/(.+).js": "@specs-feup/lara/src-api/$1",
    "(.+)\\.js": "$1",
  },
};

export default config;
