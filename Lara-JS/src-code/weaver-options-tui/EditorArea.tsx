/**
 * EditorArea component and type-specific editor controls for the Weaver Options TUI.
 *
 * This module provides the editing interface that appears at the bottom of the screen
 * when a user selects an option to edit. It includes specialized editors for different
 * option types: boolean toggles, enum selects, text inputs, and multi-line lists.
 */

import React, { useState, useCallback } from "react";
import { Box, Text, useInput } from "ink";
import { TextInput } from "@inkjs/ui";
import type {
  OptionMeta,
  OptionValueState,
  ValidationResult,
  EditorType,
} from "./types.js";
import { getEditorType } from "./types.js";

/**
 * Props for the EditorArea component.
 */
interface EditorAreaProps {
  /** Metadata for the option being edited */
  meta: OptionMeta;
  /** Current value state of the option */
  value: OptionValueState;
  /** Callback when user confirms a new value */
  onConfirm: (newValue: OptionValueState) => void;
  /** Callback when user cancels editing */
  onCancel: () => void;
  /** Callback to validate a value before confirming */
  onValidate: (keyId: string, rawValue: string) => Promise<ValidationResult>;
}

/**
 * Props shared by all editor components.
 */
interface BaseEditorProps {
  meta: OptionMeta;
  value: OptionValueState;
  onConfirm: (newValue: OptionValueState) => void;
  onCancel: () => void;
}

/**
 * Props for editors that support validation.
 */
interface ValidatingEditorProps extends BaseEditorProps {
  onValidate: (keyId: string, rawValue: string) => Promise<ValidationResult>;
  setValidationError: (error: string | null) => void;
}

/**
 * Custom select option type for the simple select implementation.
 */
interface SelectOption {
  label: string;
  value: string;
}

/**
 * SimpleSelect - A custom select component that properly renders options vertically.
 * 
 * This replaces @inkjs/ui Select to avoid rendering issues where options overlap.
 * Uses Box with flexDirection="column" and proper cursor navigation.
 */
function SimpleSelect({
  options,
  defaultValue,
  onSelect,
}: {
  options: SelectOption[];
  defaultValue?: string;
  onSelect: (value: string) => void;
}): React.ReactElement {
  const defaultIndex = options.findIndex((opt) => opt.value === defaultValue);
  const [focusedIndex, setFocusedIndex] = useState(
    defaultIndex >= 0 ? defaultIndex : 0
  );

  useInput((input, key) => {
    if (key.upArrow) {
      setFocusedIndex((prev) => (prev <= 0 ? options.length - 1 : prev - 1));
    } else if (key.downArrow) {
      setFocusedIndex((prev) => (prev >= options.length - 1 ? 0 : prev + 1));
    } else if (key.return) {
      const selected = options[focusedIndex];
      if (selected) {
        onSelect(selected.value);
      }
    }
  });

  return (
    <Box flexDirection="column">
      {options.map((option, index) => (
        <Box key={option.value}>
          <Text color={index === focusedIndex ? "blue" : undefined}>
            {index === focusedIndex ? "❯ " : "  "}
            {option.label}
            {option.value === defaultValue && index !== focusedIndex && (
              <Text color="green"> ✓</Text>
            )}
          </Text>
        </Box>
      ))}
    </Box>
  );
}

/**
 * BooleanEditor - Toggle between (unset), true, and false.
 *
 * Uses a custom SimpleSelect component to provide a dropdown-style
 * selection for boolean values.
 */
function BooleanEditor({
  meta,
  value,
  onConfirm,
  onCancel,
}: BaseEditorProps): React.ReactElement {
  const trueLabel = "true" + (meta.defaultValue === "true" ? " (default)" : "");
  const falseLabel = "false" + (meta.defaultValue === "false" ? " (default)" : "");
  
  const options: SelectOption[] = [
    { label: "(unset)", value: "unset" },
    { label: trueLabel, value: "true" },
    { label: falseLabel, value: "false" },
  ];

  const currentValue = value.kind === "unset" ? "unset" : value.value;

  useInput((input, key) => {
    if (key.escape) {
      onCancel();
    }
  });

  return (
    <SimpleSelect
      options={options}
      defaultValue={currentValue}
      onSelect={(selected: string) => {
        if (selected === "unset") {
          onConfirm({ kind: "unset" });
        } else {
          onConfirm({ kind: "set", value: selected });
        }
      }}
    />
  );
}

/**
 * SelectEditor - For enum types, shows all allowed values plus (unset).
 *
 * Provides a dropdown selection from the predefined allowed values
 * specified in the option metadata.
 */
function SelectEditor({
  meta,
  value,
  onConfirm,
  onCancel,
}: BaseEditorProps): React.ReactElement {
  const UNSET_SENTINEL = "__UNSET__";

  const options = [
    { label: "(unset)", value: UNSET_SENTINEL },
    ...(meta.allowedValues ?? []).map((v) => ({
      label: v + (meta.defaultValue === v ? " (default)" : ""),
      value: v,
    })),
  ];

  const currentValue = value.kind === "unset" ? UNSET_SENTINEL : value.value;

  useInput((input, key) => {
    if (key.escape) {
      onCancel();
    }
  });

  return (
    <SimpleSelect
      options={options}
      defaultValue={currentValue}
      onSelect={(selected: string) => {
        if (selected === UNSET_SENTINEL) {
          onConfirm({ kind: "unset" });
        } else {
          onConfirm({ kind: "set", value: selected });
        }
      }}
    />
  );
}

/**
 * TextEditor - For string, integer, double, file, and folder types.
 *
 * Provides a single-line text input with validation support.
 * Uses uncontrolled TextInput from @inkjs/ui with defaultValue.
 * - Empty input results in unset
 * - Tab key to quickly unset
 * - Escape to cancel
 * - Enter to validate and confirm
 */
function TextEditor({
  meta,
  value,
  onConfirm,
  onCancel,
  onValidate,
  setValidationError,
}: ValidatingEditorProps): React.ReactElement {
  const defaultValue = value.kind === "set" ? value.value : "";

  useInput((input, key) => {
    if (key.escape) {
      onCancel();
    } else if (key.tab) {
      // Tab to unset
      onConfirm({ kind: "unset" });
    }
  });

  const handleSubmit = useCallback(
    async (submitted: string) => {
      if (submitted === "") {
        onConfirm({ kind: "unset" });
        return;
      }

      const result = await onValidate(meta.keyId, submitted);
      if (result.valid) {
        onConfirm({ kind: "set", value: result.decodedValue ?? submitted });
        setValidationError(null);
      } else {
        setValidationError(result.error ?? "Invalid value");
      }
    },
    [meta.keyId, onConfirm, onValidate, setValidationError]
  );

  return (
    <Box>
      <Text>Value: </Text>
      <TextInput
        defaultValue={defaultValue}
        onSubmit={handleSubmit}
        placeholder={meta.defaultValue ?? ""}
      />
    </Box>
  );
}

/**
 * MultiLineEditor - For fileList and stringList types.
 *
 * Provides an interface for editing lists of items:
 * - Items are displayed as a list
 * - New items can be added via text input
 * - Items can be removed with 'd' key when selected
 * - Enter on empty input saves the list
 * - Tab to unset the entire list
 * - Escape to cancel
 *
 * Values are stored as semicolon-separated strings.
 */
function MultiLineEditor({
  meta,
  value,
  onConfirm,
  onCancel,
  onValidate,
  setValidationError,
}: ValidatingEditorProps): React.ReactElement {
  /**
   * Parse a semicolon or newline separated string into an array of items.
   * Handles empty strings and trims whitespace.
   */
  const parseList = useCallback((v: string): string[] => {
    if (!v || v.trim() === "") {
      return [];
    }
    return v
      .split(/[;\n]/)
      .map((s) => s.trim())
      .filter((s) => s.length > 0);
  }, []);

  /**
   * Format an array of items as a semicolon-separated string.
   */
  const formatList = useCallback((items: string[]): string => items.join(";"), []);

  // Parse the initial value
  const initialItems = value.kind === "set" ? parseList(value.value) : [];
  
  const [items, setItems] = useState<string[]>(initialItems);
  const [selectedItemIndex, setSelectedItemIndex] = useState(initialItems.length > 0 ? 0 : -1);
  // Track for TextInput remounting (uncontrolled component reset)
  const [inputKey, setInputKey] = useState(0);
  // Track if user is actively typing in the input (to avoid 'd' key conflicts)
  const [isInputFocused, setIsInputFocused] = useState(true);

  useInput((input, key) => {
    if (key.escape) {
      onCancel();
    } else if (key.tab) {
      onConfirm({ kind: "unset" });
    } else if (key.upArrow && items.length > 0) {
      setIsInputFocused(false);
      setSelectedItemIndex((prev) =>
        prev <= 0 ? items.length - 1 : prev - 1
      );
    } else if (key.downArrow && items.length > 0) {
      setIsInputFocused(false);
      setSelectedItemIndex((prev) =>
        prev >= items.length - 1 ? 0 : prev + 1
      );
    } else if (input === "d" && !isInputFocused && selectedItemIndex >= 0) {
      // Delete selected item only when not typing in input
      setItems((prev) => {
        if (selectedItemIndex >= prev.length) return prev;
        const newItems = prev.filter((_, i) => i !== selectedItemIndex);
        // Adjust selection after delete
        setSelectedItemIndex((prevIdx) => {
          if (newItems.length === 0) return -1;
          return Math.min(prevIdx, newItems.length - 1);
        });
        return newItems;
      });
    }
  });

  const addItem = useCallback(
    (item: string) => {
      const trimmed = item.trim();
      if (trimmed) {
        setItems((prev) => [...prev, trimmed]);
        // Increment key to remount uncontrolled TextInput with empty value
        setInputKey((prev) => prev + 1);
        // Reset focus to input after adding
        setIsInputFocused(true);
      }
    },
    []
  );

  const handleInputSubmit = useCallback(
    async (submitted: string) => {
      const trimmed = submitted.trim();
      
      if (trimmed) {
        // Add the item to the list
        addItem(trimmed);
      } else if (items.length > 0) {
        // Submit the list when pressing Enter on empty input
        const formatted = formatList(items);
        const result = await onValidate(meta.keyId, formatted);
        if (result.valid) {
          onConfirm({ kind: "set", value: formatted });
          setValidationError(null);
        } else {
          setValidationError(result.error ?? "Invalid list");
        }
      } else {
        // Empty list, unset the value
        onConfirm({ kind: "unset" });
      }
    },
    [items, formatList, onValidate, meta.keyId, onConfirm, setValidationError, addItem]
  );

  // Handle clicking/focusing on input
  const handleInputChange = useCallback(() => {
    setIsInputFocused(true);
  }, []);

  return (
    <Box flexDirection="column">
      <Text>
        Items ({items.length}):
        {items.length === 0 && <Text dimColor> (none)</Text>}
      </Text>
      <Box flexDirection="column" marginLeft={1}>
        {items.map((item, i) => (
          <Box key={`item-${i}-${item.substring(0, 20)}`}>
            <Text color={i === selectedItemIndex && !isInputFocused ? "cyan" : undefined}>
              {i === selectedItemIndex && !isInputFocused ? "▸ " : "  "}
              {item}
            </Text>
          </Box>
        ))}
      </Box>
      <Box marginTop={1}>
        <Text>{isInputFocused ? "▸ " : "  "}Add: </Text>
        <TextInput
          key={inputKey}
          defaultValue=""
          onSubmit={handleInputSubmit}
          onChange={handleInputChange}
          placeholder={
            meta.type === "fileList"
              ? "Enter file path, press Enter to add"
              : "Enter value, press Enter to add"
          }
        />
      </Box>
      <Box marginTop={1}>
        <Text dimColor>
          {items.length > 0
            ? "↑/↓ navigate • 'd' delete selected • Enter (empty) to save"
            : "Type value and Enter to add • Enter (empty) to save"}
        </Text>
      </Box>
    </Box>
  );
}

/**
 * EditorArea - Main editor component that appears when editing an option.
 *
 * Displays the option label and description, delegates to the appropriate
 * type-specific editor based on the option type, and shows validation errors
 * and keyboard shortcuts.
 *
 * @example
 * ```tsx
 * <EditorArea
 *   meta={optionMeta}
 *   value={{ kind: "set", value: "hello" }}
 *   onConfirm={(newValue) => handleConfirm(newValue)}
 *   onCancel={() => setEditing(false)}
 *   onValidate={validateOption}
 * />
 * ```
 */
export function EditorArea({
  meta,
  value,
  onConfirm,
  onCancel,
  onValidate,
}: EditorAreaProps): React.ReactElement {
  const [validationError, setValidationError] = useState<string | null>(null);
  const editorType = getEditorType(meta.type);

  return (
    <Box
      flexDirection="column"
      borderStyle="single"
      borderColor="blue"
      paddingX={1}
    >
      <Box marginBottom={1}>
        <Text bold color="cyan">
          Editing: {meta.label}
        </Text>
      </Box>

      {meta.description && (
        <Box marginBottom={1}>
          <Text dimColor>{meta.description}</Text>
        </Box>
      )}

      {meta.defaultValue && (
        <Box marginBottom={1}>
          <Text dimColor>
            Default: <Text color="gray">{meta.defaultValue}</Text>
          </Text>
        </Box>
      )}

      <Box marginY={1}>
        {editorType === "toggle" && (
          <BooleanEditor
            meta={meta}
            value={value}
            onConfirm={onConfirm}
            onCancel={onCancel}
          />
        )}
        {editorType === "select" && (
          <SelectEditor
            meta={meta}
            value={value}
            onConfirm={onConfirm}
            onCancel={onCancel}
          />
        )}
        {editorType === "textInput" && (
          <TextEditor
            meta={meta}
            value={value}
            onConfirm={onConfirm}
            onCancel={onCancel}
            onValidate={onValidate}
            setValidationError={setValidationError}
          />
        )}
        {editorType === "multiLine" && (
          <MultiLineEditor
            meta={meta}
            value={value}
            onConfirm={onConfirm}
            onCancel={onCancel}
            onValidate={onValidate}
            setValidationError={setValidationError}
          />
        )}
      </Box>

      {validationError && (
        <Box marginY={1}>
          <Text color="red">✗ Error: {validationError}</Text>
        </Box>
      )}

      <Box borderStyle="single" borderColor="gray" paddingX={1} marginTop={1}>
        <Text dimColor>
          {editorType === "toggle" || editorType === "select"
            ? "↑/↓ to select • Enter to confirm • Escape to cancel"
            : "Enter to confirm • Escape to cancel • Tab to unset"}
        </Text>
      </Box>
    </Box>
  );
}

export default EditorArea;
