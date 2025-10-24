/* global global */

import { jest } from "@jest/globals";

jest.mock("java-bridge", () => global.__SHARED_MODULE__);
