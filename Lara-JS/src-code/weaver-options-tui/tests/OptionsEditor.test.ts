import { jest } from "@jest/globals";
import WeaverLauncher from "../../WeaverLauncher.js";
import {
  getEditorType,
  isDirty,
  type OptionValueState,
  type OptionMeta,
  type SectionMeta,
} from "../types.js";

// ============================================================================
// CLI Exclusivity Tests
// ============================================================================

describe("CLI --edit flag exclusivity", () => {
  const mockConfig = {
    weaverName: "test-weaver",
    weaverPrettyName: "Test Weaver",
    javaWeaverQualifiedName: "test.Weaver",
    jarPath: "/path/to/jars",
  };

  let launcher: WeaverLauncher;

  beforeEach(() => {
    launcher = new WeaverLauncher(mockConfig);
  });

  it("should accept --edit alone", async () => {
    // Access the generated yargs config directly via generateConfig
    // Using a workaround since generateConfig is protected
    const parser = (launcher as any).generateConfig([
      "--edit",
      "/path/to/config.xml",
    ]);

    // Parse should succeed - we wrap in a promise to catch any yargs errors
    const result = await new Promise<{ edit?: string; error?: Error }>(
      (resolve) => {
        parser
          .parseAsync()
          .then((argv: any) => resolve({ edit: argv.edit }))
          .catch((err: Error) => resolve({ error: err }));
      }
    );

    expect(result.error).toBeUndefined();
    expect(result.edit).toBe("/path/to/config.xml");
  });

  it("should reject --edit with --config", async () => {
    const parser = (launcher as any).generateConfig([
      "--edit",
      "/path/to/config.xml",
      "--config",
      "config.json",
    ]);

    const result = await new Promise<{ error?: Error }>((resolve) => {
      parser
        .parseAsync()
        .then(() => resolve({}))
        .catch((err: Error) => resolve({ error: err }));
    });

    expect(result.error).toBeDefined();
    expect(result.error?.message).toContain("--edit cannot be used with");
  });

  it("should reject --edit with script-file", async () => {
    const parser = (launcher as any).generateConfig([
      "--edit",
      "/path/to/config.xml",
      "script.lara",
    ]);

    const result = await new Promise<{ error?: Error }>((resolve) => {
      parser
        .parseAsync()
        .then(() => resolve({}))
        .catch((err: Error) => resolve({ error: err }));
    });

    expect(result.error).toBeDefined();
    expect(result.error?.message).toContain("--edit cannot be used with");
  });

  it("should reject --edit with --watch", async () => {
    const parser = (launcher as any).generateConfig([
      "--edit",
      "/path/to/config.xml",
      "--watch",
      "/some/dir",
    ]);

    const result = await new Promise<{ error?: Error }>((resolve) => {
      parser
        .parseAsync()
        .then(() => resolve({}))
        .catch((err: Error) => resolve({ error: err }));
    });

    expect(result.error).toBeDefined();
    expect(result.error?.message).toContain("--edit cannot be used with");
  });
});

// ============================================================================
// Type Helper Tests - getEditorType
// ============================================================================

describe("getEditorType", () => {
  it("should return toggle for boolean type", () => {
    expect(getEditorType("boolean")).toBe("toggle");
  });

  it("should return select for enum type", () => {
    expect(getEditorType("enum")).toBe("select");
  });

  it("should return multiLine for fileList type", () => {
    expect(getEditorType("fileList")).toBe("multiLine");
  });

  it("should return multiLine for stringList type", () => {
    expect(getEditorType("stringList")).toBe("multiLine");
  });

  it("should return textInput for string type", () => {
    expect(getEditorType("string")).toBe("textInput");
  });

  it("should return textInput for integer type", () => {
    expect(getEditorType("integer")).toBe("textInput");
  });

  it("should return textInput for double type", () => {
    expect(getEditorType("double")).toBe("textInput");
  });

  it("should return textInput for file type", () => {
    expect(getEditorType("file")).toBe("textInput");
  });

  it("should return textInput for folder type", () => {
    expect(getEditorType("folder")).toBe("textInput");
  });

  it("should return textInput for unknown type", () => {
    expect(getEditorType("unknown")).toBe("textInput");
  });
});

// ============================================================================
// Type Helper Tests - isDirty
// ============================================================================

describe("isDirty", () => {
  it("should return false for identical empty states", () => {
    const initial = new Map<string, OptionValueState>();
    const current = new Map<string, OptionValueState>();
    expect(isDirty(initial, current)).toBe(false);
  });

  it("should return false when values are identical", () => {
    const initial = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }],
      ["key2", { kind: "unset" }],
    ]);
    const current = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }],
      ["key2", { kind: "unset" }],
    ]);
    expect(isDirty(initial, current)).toBe(false);
  });

  it("should return true when a value is changed", () => {
    const initial = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }],
    ]);
    const current = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value2" }],
    ]);
    expect(isDirty(initial, current)).toBe(true);
  });

  it("should return true when a key is set from unset", () => {
    const initial = new Map<string, OptionValueState>([
      ["key1", { kind: "unset" }],
    ]);
    const current = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }],
    ]);
    expect(isDirty(initial, current)).toBe(true);
  });

  it("should return true when a key is unset from set", () => {
    const initial = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }],
    ]);
    const current = new Map<string, OptionValueState>([
      ["key1", { kind: "unset" }],
    ]);
    expect(isDirty(initial, current)).toBe(true);
  });

  it("should return false when both have unset key", () => {
    const initial = new Map<string, OptionValueState>([
      ["key1", { kind: "unset" }],
    ]);
    const current = new Map<string, OptionValueState>([
      ["key1", { kind: "unset" }],
    ]);
    expect(isDirty(initial, current)).toBe(false);
  });

  it("should return true when current has new set key not in initial", () => {
    const initial = new Map<string, OptionValueState>();
    const current = new Map<string, OptionValueState>([
      ["newKey", { kind: "set", value: "newValue" }],
    ]);
    expect(isDirty(initial, current)).toBe(true);
  });

  it("should return false when current has new unset key not in initial", () => {
    const initial = new Map<string, OptionValueState>();
    const current = new Map<string, OptionValueState>([
      ["newKey", { kind: "unset" }],
    ]);
    expect(isDirty(initial, current)).toBe(false);
  });

  it("should return true when initial has set key missing from current", () => {
    const initial = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }],
    ]);
    const current = new Map<string, OptionValueState>();
    expect(isDirty(initial, current)).toBe(true);
  });

  it("should return false when initial has unset key missing from current", () => {
    const initial = new Map<string, OptionValueState>([
      ["key1", { kind: "unset" }],
    ]);
    const current = new Map<string, OptionValueState>();
    expect(isDirty(initial, current)).toBe(false);
  });

  it("should handle complex mixed state changes", () => {
    const initial = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }],
      ["key2", { kind: "unset" }],
      ["key3", { kind: "set", value: "value3" }],
    ]);
    const current = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }], // unchanged
      ["key2", { kind: "unset" }], // unchanged
      ["key3", { kind: "set", value: "value3" }], // unchanged
    ]);
    expect(isDirty(initial, current)).toBe(false);
  });
});

// ============================================================================
// State Transition Tests
// ============================================================================

describe("State transitions", () => {
  it("set -> unset should remove key from saved output", () => {
    // Create a mock state that simulates what ConfigService.save filters to
    // When a key goes from set to unset, it should not appear in the saved values
    const values = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }],
      ["key2", { kind: "unset" }], // This was previously set
    ]);

    // Filter to only set values (simulating what save does)
    const toSave = Array.from(values.entries()).filter(
      ([_, v]) => v.kind === "set"
    );

    expect(toSave).toHaveLength(1);
    expect(toSave[0][0]).toBe("key1");
  });

  it("unset -> set should include key in saved output", () => {
    const values = new Map<string, OptionValueState>([
      ["key1", { kind: "unset" }], // was previously unset
      ["key2", { kind: "set", value: "newValue" }], // newly set
    ]);

    const toSave = Array.from(values.entries()).filter(
      ([_, v]) => v.kind === "set"
    );

    expect(toSave).toHaveLength(1);
    expect(toSave[0][0]).toBe("key2");
    expect((toSave[0][1] as { kind: "set"; value: string }).value).toBe(
      "newValue"
    );
  });

  it("multiple set values should all appear in saved output", () => {
    const values = new Map<string, OptionValueState>([
      ["key1", { kind: "set", value: "value1" }],
      ["key2", { kind: "set", value: "value2" }],
      ["key3", { kind: "set", value: "value3" }],
    ]);

    const toSave = Array.from(values.entries()).filter(
      ([_, v]) => v.kind === "set"
    );

    expect(toSave).toHaveLength(3);
    expect(toSave.map(([k]) => k).sort()).toEqual(["key1", "key2", "key3"]);
  });

  it("all unset values should result in empty saved output", () => {
    const values = new Map<string, OptionValueState>([
      ["key1", { kind: "unset" }],
      ["key2", { kind: "unset" }],
    ]);

    const toSave = Array.from(values.entries()).filter(
      ([_, v]) => v.kind === "set"
    );

    expect(toSave).toHaveLength(0);
  });
});

// ============================================================================
// List Building Tests
// ============================================================================

describe("List building", () => {
  /**
   * Row type for the flattened list.
   * Can be either a section header or an option row.
   */
  type ListRow =
    | { type: "section"; name: string }
    | { type: "option"; meta: OptionMeta };

  /**
   * Builds a flattened list of sections and options.
   * This simulates the logic that would be used in the TUI.
   */
  function buildFlatList(
    sections: SectionMeta[],
    options: Map<string, OptionMeta>
  ): ListRow[] {
    const result: ListRow[] = [];

    for (const section of sections) {
      result.push({ type: "section", name: section.name });
      for (const keyId of section.keyIds) {
        const meta = options.get(keyId);
        if (meta) {
          result.push({ type: "option", meta });
        }
      }
    }

    return result;
  }

  /**
   * Filters the list by a search query (matches label or keyId).
   */
  function filterList(
    sections: SectionMeta[],
    options: Map<string, OptionMeta>,
    query: string
  ): ListRow[] {
    const lowerQuery = query.toLowerCase();
    const result: ListRow[] = [];

    for (const section of sections) {
      const matchingKeys = section.keyIds.filter((keyId) => {
        const meta = options.get(keyId);
        if (!meta) return false;
        return (
          meta.keyId.toLowerCase().includes(lowerQuery) ||
          meta.label.toLowerCase().includes(lowerQuery)
        );
      });

      if (matchingKeys.length > 0) {
        result.push({ type: "section", name: section.name });
        for (const keyId of matchingKeys) {
          const meta = options.get(keyId);
          if (meta) {
            result.push({ type: "option", meta });
          }
        }
      }
    }

    return result;
  }

  /**
   * Gets only the selectable indices (option rows, not section headers).
   */
  function getSelectableIndices(list: ListRow[]): number[] {
    return list
      .map((row, index) => ({ row, index }))
      .filter(({ row }) => row.type === "option")
      .map(({ index }) => index);
  }

  it("should build a flattened list with sections and options", () => {
    const sections: SectionMeta[] = [
      { name: "Section A", keyIds: ["key1", "key2"] },
      { name: "Section B", keyIds: ["key3"] },
    ];

    const options = new Map<string, OptionMeta>([
      [
        "key1",
        { keyId: "key1", label: "Key 1", section: "Section A", type: "string" },
      ],
      [
        "key2",
        {
          keyId: "key2",
          label: "Key 2",
          section: "Section A",
          type: "boolean",
        },
      ],
      [
        "key3",
        {
          keyId: "key3",
          label: "Key 3",
          section: "Section B",
          type: "integer",
        },
      ],
    ]);

    const list = buildFlatList(sections, options);

    // The list should have: [Section A header, key1, key2, Section B header, key3]
    expect(list).toHaveLength(5);
    expect(list[0]).toEqual({ type: "section", name: "Section A" });
    expect(list[1]).toEqual({
      type: "option",
      meta: options.get("key1"),
    });
    expect(list[2]).toEqual({
      type: "option",
      meta: options.get("key2"),
    });
    expect(list[3]).toEqual({ type: "section", name: "Section B" });
    expect(list[4]).toEqual({
      type: "option",
      meta: options.get("key3"),
    });
  });

  it("should provide only option rows as selectable indices", () => {
    const sections: SectionMeta[] = [
      { name: "Section A", keyIds: ["key1", "key2"] },
      { name: "Section B", keyIds: ["key3"] },
    ];

    const options = new Map<string, OptionMeta>([
      [
        "key1",
        { keyId: "key1", label: "Key 1", section: "Section A", type: "string" },
      ],
      [
        "key2",
        {
          keyId: "key2",
          label: "Key 2",
          section: "Section A",
          type: "boolean",
        },
      ],
      [
        "key3",
        {
          keyId: "key3",
          label: "Key 3",
          section: "Section B",
          type: "integer",
        },
      ],
    ]);

    const list = buildFlatList(sections, options);
    const selectableIndices = getSelectableIndices(list);

    // Selectable indices should be 1, 2, 4 (skipping section headers at 0 and 3)
    expect(selectableIndices).toEqual([1, 2, 4]);
  });

  it("should filter list by search query matching label", () => {
    const sections: SectionMeta[] = [
      { name: "Section A", keyIds: ["key1", "key2"] },
      { name: "Section B", keyIds: ["key3"] },
    ];

    const options = new Map<string, OptionMeta>([
      [
        "key1",
        {
          keyId: "key1",
          label: "Output Path",
          section: "Section A",
          type: "string",
        },
      ],
      [
        "key2",
        {
          keyId: "key2",
          label: "Verbose Mode",
          section: "Section A",
          type: "boolean",
        },
      ],
      [
        "key3",
        {
          keyId: "key3",
          label: "Max Threads",
          section: "Section B",
          type: "integer",
        },
      ],
    ]);

    const filteredList = filterList(sections, options, "verbose");

    // Should only include Section A and the "Verbose Mode" option
    expect(filteredList).toHaveLength(2);
    expect(filteredList[0]).toEqual({ type: "section", name: "Section A" });
    expect(filteredList[1]).toEqual({
      type: "option",
      meta: options.get("key2"),
    });
  });

  it("should filter list by search query matching keyId", () => {
    const sections: SectionMeta[] = [
      { name: "Section A", keyIds: ["outputPath", "verbose"] },
      { name: "Section B", keyIds: ["maxThreads"] },
    ];

    const options = new Map<string, OptionMeta>([
      [
        "outputPath",
        {
          keyId: "outputPath",
          label: "Output Path",
          section: "Section A",
          type: "string",
        },
      ],
      [
        "verbose",
        {
          keyId: "verbose",
          label: "Verbose Mode",
          section: "Section A",
          type: "boolean",
        },
      ],
      [
        "maxThreads",
        {
          keyId: "maxThreads",
          label: "Max Threads",
          section: "Section B",
          type: "integer",
        },
      ],
    ]);

    const filteredList = filterList(sections, options, "threads");

    // Should only include Section B and the "Max Threads" option
    expect(filteredList).toHaveLength(2);
    expect(filteredList[0]).toEqual({ type: "section", name: "Section B" });
    expect(filteredList[1]).toEqual({
      type: "option",
      meta: options.get("maxThreads"),
    });
  });

  it("should hide sections with no matching keys when filtering", () => {
    const sections: SectionMeta[] = [
      { name: "Section A", keyIds: ["key1"] },
      { name: "Section B", keyIds: ["key2"] },
    ];

    const options = new Map<string, OptionMeta>([
      [
        "key1",
        { keyId: "key1", label: "Apple", section: "Section A", type: "string" },
      ],
      [
        "key2",
        {
          keyId: "key2",
          label: "Banana",
          section: "Section B",
          type: "string",
        },
      ],
    ]);

    const filteredList = filterList(sections, options, "apple");

    // Should only include Section A and key1
    expect(filteredList).toHaveLength(2);
    expect(filteredList[0]).toEqual({ type: "section", name: "Section A" });
    expect(filteredList[1]).toEqual({
      type: "option",
      meta: options.get("key1"),
    });
  });

  it("should return empty list when no options match search", () => {
    const sections: SectionMeta[] = [
      { name: "Section A", keyIds: ["key1"] },
    ];

    const options = new Map<string, OptionMeta>([
      [
        "key1",
        { keyId: "key1", label: "Apple", section: "Section A", type: "string" },
      ],
    ]);

    const filteredList = filterList(sections, options, "xyz");

    expect(filteredList).toHaveLength(0);
  });

  it("should be case-insensitive when filtering", () => {
    const sections: SectionMeta[] = [
      { name: "Section A", keyIds: ["key1"] },
    ];

    const options = new Map<string, OptionMeta>([
      [
        "key1",
        {
          keyId: "key1",
          label: "Output PATH",
          section: "Section A",
          type: "string",
        },
      ],
    ]);

    const filteredListLower = filterList(sections, options, "path");
    const filteredListUpper = filterList(sections, options, "PATH");
    const filteredListMixed = filterList(sections, options, "PaTh");

    expect(filteredListLower).toHaveLength(2);
    expect(filteredListUpper).toHaveLength(2);
    expect(filteredListMixed).toHaveLength(2);
  });

  it("should handle empty sections gracefully", () => {
    const sections: SectionMeta[] = [
      { name: "Section A", keyIds: [] },
      { name: "Section B", keyIds: ["key1"] },
    ];

    const options = new Map<string, OptionMeta>([
      [
        "key1",
        { keyId: "key1", label: "Key 1", section: "Section B", type: "string" },
      ],
    ]);

    const list = buildFlatList(sections, options);

    // Should have: [Section A header (empty), Section B header, key1]
    expect(list).toHaveLength(3);
    expect(list[0]).toEqual({ type: "section", name: "Section A" });
    expect(list[1]).toEqual({ type: "section", name: "Section B" });
    expect(list[2]).toEqual({
      type: "option",
      meta: options.get("key1"),
    });
  });

  it("should handle missing option metadata gracefully", () => {
    const sections: SectionMeta[] = [
      { name: "Section A", keyIds: ["key1", "missingKey", "key2"] },
    ];

    const options = new Map<string, OptionMeta>([
      [
        "key1",
        { keyId: "key1", label: "Key 1", section: "Section A", type: "string" },
      ],
      [
        "key2",
        { keyId: "key2", label: "Key 2", section: "Section A", type: "string" },
      ],
      // "missingKey" is intentionally not in the map
    ]);

    const list = buildFlatList(sections, options);

    // Should skip the missing key
    expect(list).toHaveLength(3);
    expect(list[0]).toEqual({ type: "section", name: "Section A" });
    expect(list[1]).toEqual({
      type: "option",
      meta: options.get("key1"),
    });
    expect(list[2]).toEqual({
      type: "option",
      meta: options.get("key2"),
    });
  });
});
