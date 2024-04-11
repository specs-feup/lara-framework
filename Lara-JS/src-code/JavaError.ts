export default interface JavaError {
  cause: {
    getMessage: () => string;
    getStackTrace: () => string[];
    getMessageP: () => string;
    getStackTraceP: () => string[];
  };
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function isJavaError(obj: any): obj is JavaError {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
  return obj.cause != undefined;
}
