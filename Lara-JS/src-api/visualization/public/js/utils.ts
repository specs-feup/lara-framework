/**
 * @file utils.ts
 * @brief Utility functions for the visualization tool.
 */

/**
 * @brief Counts the number of occurrences of a character in a string.
 * 
 * @param str String to search
 * @param char Target character
 * @returns Number of occurrences of char in str
 */
const countChar = (str: string, char: string): number => {
  let count = 0;
  for (const c of str) {
    if (c === char)
      count++;
  }
  return count;
}

export { countChar };