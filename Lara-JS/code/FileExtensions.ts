/**
 * This is supposed to be an enum, but Node.js v25 does bot support TS' enums, only erasable-syntax.
 * Revert to an enum when Node.js supports it, or when we move to a different engine that supports it.
 * This and the "type" declaration below.
 */
export const FileExtensions = {
  JS: ".js",
  MJS: ".mjs",
  CJS: ".cjs",
} as const;
export type FileExtensions = typeof FileExtensions[keyof typeof FileExtensions];

/**
 * Checks if a file extension is valid.
 *
 * @param extension - The file extension to check.
 * @returns `true` if the file extension is valid, `false` otherwise.
 */
export const isValidFileExtension = (extension: string): boolean => {
  return Object.values(FileExtensions).includes(extension as FileExtensions);
};
