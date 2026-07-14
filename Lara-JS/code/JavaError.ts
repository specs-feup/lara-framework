export default interface JavaError {
  cause: {
    getMessage: () => string;
    getStackTrace: () => string[];
    getMessageP: () => string;
    getStackTraceP: () => string[];
  };
}

// oxlint-disable-next-line typescript/no-explicit-any
export function isJavaError(obj: any): obj is JavaError {
  return obj.cause != undefined;
}
