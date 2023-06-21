export default interface JavaError {
  cause: {
    getMessage: () => string;
    getStackTrace: () => string[];
    getMessageP: () => string;
    getStackTraceP: () => string[];
  };
}
