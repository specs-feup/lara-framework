const config = {
  preset: "ts-jest/presets/default-esm",
  testEnvironment: "lara-js/jest/jestEnvironment.js",
  //globalSetup: "lara-js/jest/jestGlobalSetup.js",
  //globalTeardown: "lara-js/jest/jestGlobalTeardown.js",
  setupFiles: ["lara-js/jest/setupFiles/sharedJavaModule.js"],
  moduleNameMapper: {
    "lara-js/api/(.+).js": "lara-js/src-api/$1",
    "(.+)\\.js": "$1",
  },
};

export default config;
