/**
 * Main TUI App component for the Weaver Options Editor.
 *
 * Provides a full-screen scrollable form interface for editing
 * weaver configuration options with keyboard navigation.
 */

import React, { useState, useCallback, useMemo } from "react";
import { Box, Text, useApp, useInput, useStdout } from "ink";
import type {
  OptionMeta,
  SectionMeta,
  OptionValueState,
  LoadedConfig,
  ValidationResult,
} from "./types.js";
import { isDirty, getEditorType } from "./types.js";
import { EditorArea } from "./EditorArea.js";

// ============================================================================
// Types
// ============================================================================

export interface AppProps {
  loadedConfig: LoadedConfig;
  onSave: (values: Map<string, OptionValueState>) => Promise<void>;
  onValidate: (keyId: string, value: string) => Promise<ValidationResult>;
}

interface AppState {
  values: Map<string, OptionValueState>;
  selectedIndex: number; // Index in the flattened selectable list
  editingKeyId: string | null; // Currently editing key, null if not editing
  searchQuery: string;
  isSearching: boolean;
  errorMessage: string | null;
  showExitPrompt: boolean;
}

/** Represents a row in the flattened list */
type ListItem =
  | { type: "section"; name: string }
  | {
      type: "option";
      keyId: string;
      meta: OptionMeta;
      selectableIndex: number;
    };

// ============================================================================
// Helper Components
// ============================================================================

interface HeaderProps {
  filePath: string;
  isDirty: boolean;
}

/** Header bar showing file path and dirty indicator */
function Header({ filePath, isDirty }: HeaderProps): React.ReactElement {
  return (
    <Box
      borderStyle="single"
      borderBottom={true}
      borderTop={false}
      borderLeft={false}
      borderRight={false}
      paddingX={1}
    >
      <Text bold color="cyan">
        Weaver Options Editor
      </Text>
      <Text> – </Text>
      <Text color="white">{filePath}</Text>
      {isDirty && (
        <Text color="yellow" bold>
          {" "}
          *
        </Text>
      )}
    </Box>
  );
}

interface SectionHeaderProps {
  name: string;
}

/** Styled section name row (non-selectable) */
function SectionHeader({ name }: SectionHeaderProps): React.ReactElement {
  return (
    <Box marginTop={1} marginBottom={0}>
      <Text bold color="blue">
        ── {name} ──
      </Text>
    </Box>
  );
}

interface OptionRowProps {
  meta: OptionMeta;
  value: OptionValueState | undefined;
  isSelected: boolean;
}

/** Option row showing label, value, and ghost text */
function OptionRow({
  meta,
  value,
  isSelected,
}: OptionRowProps): React.ReactElement {
  const valueState = value ?? { kind: "unset" as const };
  const hasValue = valueState.kind === "set";

  // Build display value and ghost text
  // Logic:
  // - If unset with default: show only "(unset – implicit default: {default})"
  // - If unset with no default: show only "(unset)"
  // - If set and equals default: show "{value} (default)"
  // - If set and not default: show just "{value}"
  let displayValue = "";
  let ghostText = "";

  if (!hasValue) {
    if (meta.defaultValue !== undefined && meta.defaultValue !== "") {
      displayValue = `(unset – implicit default: ${meta.defaultValue})`;
    } else {
      displayValue = "(unset)";
    }
  } else {
    displayValue = valueState.value;
    if (
      meta.defaultValue !== undefined &&
      valueState.value === meta.defaultValue
    ) {
      ghostText = "(default)";
    }
  }

  const bgColor = isSelected ? "blue" : undefined;
  const labelColor = isSelected ? "white" : "green";
  const valueColor = hasValue ? (isSelected ? "white" : "gray") : "gray";

  return (
    <Box paddingX={1}>
      <Box width="100%">
        <Text backgroundColor={bgColor} color={labelColor}>
          {isSelected ? "▸ " : "  "}
          {meta.label}
        </Text>
        <Text backgroundColor={bgColor}> </Text>
        <Text
          backgroundColor={bgColor}
          color={valueColor}
          dimColor={!hasValue}
        >
          {displayValue}
        </Text>
        {ghostText && (
          <Text backgroundColor={bgColor} color="gray" dimColor>
            {" "}
            {ghostText}
          </Text>
        )}
      </Box>
    </Box>
  );
}

interface FooterProps {
  isSearching: boolean;
  searchQuery: string;
  errorMessage: string | null;
}

/** Footer showing keybindings or search input */
function Footer({
  isSearching,
  searchQuery,
  errorMessage,
}: FooterProps): React.ReactElement {
  return (
    <Box
      borderStyle="single"
      borderTop={true}
      borderBottom={false}
      borderLeft={false}
      borderRight={false}
      paddingX={1}
      flexDirection="column"
    >
      {errorMessage && (
        <Box>
          <Text color="red" bold>
            Error: {errorMessage}
          </Text>
        </Box>
      )}
      {isSearching ? (
        <Box>
          <Text color="yellow">Search: </Text>
          <Text>{searchQuery}</Text>
          <Text color="gray" dimColor>
            █
          </Text>
          <Text color="gray" dimColor>
            {" "}
            (Enter to confirm, Esc to cancel)
          </Text>
        </Box>
      ) : (
        <Box>
          <Text color="gray">
            <Text color="cyan">/</Text> search{"  "}
            <Text color="cyan">↑↓</Text> navigate{"  "}
            <Text color="cyan">Enter</Text> edit{"  "}
            <Text color="cyan">s</Text> save{"  "}
            <Text color="cyan">q</Text> quit
          </Text>
        </Box>
      )}
    </Box>
  );
}

interface ExitPromptProps {
  onSave: () => void;
  onDiscard: () => void;
  onCancel: () => void;
}

/** Exit prompt dialog */
function ExitPrompt({
  onSave,
  onDiscard,
  onCancel,
}: ExitPromptProps): React.ReactElement {
  return (
    <Box
      borderStyle="double"
      borderColor="yellow"
      paddingX={2}
      paddingY={1}
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
    >
      <Text bold color="yellow">
        You have unsaved changes!
      </Text>
      <Box marginTop={1}>
        <Text>
          Save changes? (<Text color="green">y</Text>es /{" "}
          <Text color="red">n</Text>o / <Text color="gray">Esc</Text> to cancel)
        </Text>
      </Box>
    </Box>
  );
}

// ============================================================================
// Main App Component
// ============================================================================

export function App({
  loadedConfig,
  onSave,
  onValidate,
}: AppProps): React.ReactElement {
  const { exit } = useApp();
  const { stdout } = useStdout();
  const terminalHeight = stdout?.rows ?? 24;

  // Initialize state
  const [state, setState] = useState<AppState>(() => ({
    values: new Map(loadedConfig.initialState),
    selectedIndex: 0,
    editingKeyId: null,
    searchQuery: "",
    isSearching: false,
    errorMessage: null,
    showExitPrompt: false,
  }));

  const { options, sections } = loadedConfig.metadata;
  const { filePath } = loadedConfig;

  // Check if current state is dirty
  const dirty = useMemo(
    () => isDirty(loadedConfig.initialState, state.values),
    [loadedConfig.initialState, state.values]
  );

  // Build flattened list with filtering
  const { flattenedList, selectableItems } = useMemo(() => {
    const list: ListItem[] = [];
    const selectable: Array<{ keyId: string; meta: OptionMeta }> = [];
    const query = state.searchQuery.toLowerCase();

    for (const section of sections) {
      // Filter keys in this section
      const matchingKeys = section.keyIds.filter((keyId) => {
        if (!query) return true;
        const meta = options.get(keyId);
        if (!meta) return false;
        return (
          meta.label.toLowerCase().includes(query) ||
          keyId.toLowerCase().includes(query)
        );
      });

      // Skip section if no matching keys
      if (matchingKeys.length === 0) continue;

      // Add section header
      list.push({ type: "section", name: section.name });

      // Add matching keys
      for (const keyId of matchingKeys) {
        const meta = options.get(keyId);
        if (!meta) continue;

        const selectableIndex = selectable.length;
        selectable.push({ keyId, meta });
        list.push({ type: "option", keyId, meta, selectableIndex });
      }
    }

    return { flattenedList: list, selectableItems: selectable };
  }, [sections, options, state.searchQuery]);

  // Calculate visible rows for virtualization
  // Account for header (3 lines), footer (3-4 lines), and some buffer
  // When editor is open, reduce visible rows to account for editor panel (~10 lines)
  const editorHeight = state.editingKeyId ? 10 : 0;
  const visibleRows = Math.max(1, terminalHeight - 8 - editorHeight);

  // Calculate the visual height of the flattened list
  // Section headers have marginTop={1}, so they take 2 visual rows
  const totalVisualRows = useMemo(() => {
    let count = 0;
    for (const item of flattenedList) {
      if (item.type === "section") {
        count += 2; // Section header with marginTop takes 2 rows
      } else {
        count += 1;
      }
    }
    return count;
  }, [flattenedList]);

  // Calculate scroll offset based on visual rows
  const scrollOffset = useMemo(() => {
    // Find the visual row index of the selected item
    let selectedVisualRow = 0;
    for (let i = 0; i < flattenedList.length; i++) {
      const item = flattenedList[i];
      if (
        item.type === "option" &&
        item.selectableIndex === state.selectedIndex
      ) {
        break;
      }
      // Count visual rows
      if (item.type === "section") {
        selectedVisualRow += 2;
      } else {
        selectedVisualRow += 1;
      }
    }

    // Keep selected item in view with some padding
    const padding = 2;
    let offset = 0;
    
    // If selected item would be above the visible area, scroll up
    if (selectedVisualRow < offset + padding) {
      offset = Math.max(0, selectedVisualRow - padding);
    }
    
    // If selected item would be below the visible area, scroll down
    if (selectedVisualRow >= offset + visibleRows - padding) {
      offset = selectedVisualRow - visibleRows + padding + 1;
    }
    
    // Clamp to valid range
    const maxOffset = Math.max(0, totalVisualRows - visibleRows);
    offset = Math.max(0, Math.min(offset, maxOffset));
    
    return offset;
  }, [flattenedList, state.selectedIndex, visibleRows, totalVisualRows]);

  // Get visible items based on visual scroll offset
  const visibleItems = useMemo(() => {
    // Convert visual row offset to item index
    let visualRowCount = 0;
    let startIndex = 0;
    
    // Find the first item to display based on scroll offset
    for (let i = 0; i < flattenedList.length; i++) {
      if (visualRowCount >= scrollOffset) {
        startIndex = i;
        break;
      }
      const item = flattenedList[i];
      if (item.type === "section") {
        visualRowCount += 2;
      } else {
        visualRowCount += 1;
      }
      startIndex = i + 1;
    }
    
    // Collect items that fit in the visible area
    const result: ListItem[] = [];
    let currentVisualRows = 0;
    
    for (let i = startIndex; i < flattenedList.length && currentVisualRows < visibleRows; i++) {
      const item = flattenedList[i];
      result.push(item);
      if (item.type === "section") {
        currentVisualRows += 2;
      } else {
        currentVisualRows += 1;
      }
    }
    
    return result;
  }, [flattenedList, scrollOffset, visibleRows]);

  // Navigation helpers
  const moveSelection = useCallback(
    (delta: number) => {
      setState((prev) => {
        const maxIndex = selectableItems.length - 1;
        if (maxIndex < 0) return prev;
        const newIndex = Math.max(0, Math.min(maxIndex, prev.selectedIndex + delta));
        return { ...prev, selectedIndex: newIndex };
      });
    },
    [selectableItems.length]
  );

  const enterSearchMode = useCallback(() => {
    setState((prev) => ({
      ...prev,
      isSearching: true,
      searchQuery: "",
    }));
  }, []);

  const clearSearch = useCallback(() => {
    setState((prev) => ({
      ...prev,
      isSearching: false,
      searchQuery: "",
      selectedIndex: 0,
    }));
  }, []);

  const exitSearchMode = useCallback(() => {
    setState((prev) => ({
      ...prev,
      isSearching: false,
      selectedIndex: 0,
    }));
  }, []);

  const enterEditMode = useCallback(() => {
    if (selectableItems.length === 0) return;
    const selected = selectableItems[state.selectedIndex];
    if (!selected) return;
    setState((prev) => ({
      ...prev,
      editingKeyId: selected.keyId,
      errorMessage: null,
    }));
  }, [selectableItems, state.selectedIndex]);

  const handleEditConfirm = useCallback(
    (newValue: OptionValueState) => {
      setState((prev) => {
        if (!prev.editingKeyId) return prev;
        const newValues = new Map(prev.values);
        newValues.set(prev.editingKeyId, newValue);
        return {
          ...prev,
          values: newValues,
          editingKeyId: null,
          errorMessage: null,
        };
      });
    },
    []
  );

  const handleEditCancel = useCallback(() => {
    setState((prev) => ({
      ...prev,
      editingKeyId: null,
    }));
  }, []);

  const handleSave = useCallback(async () => {
    try {
      setState((prev) => ({ ...prev, errorMessage: null }));
      await onSave(state.values);
      setState((prev) => ({ ...prev, errorMessage: null }));
    } catch (error) {
      const message =
        error instanceof Error ? error.message : "Failed to save";
      setState((prev) => ({ ...prev, errorMessage: message }));
    }
  }, [onSave, state.values]);

  const handleQuit = useCallback(() => {
    if (dirty) {
      setState((prev) => ({ ...prev, showExitPrompt: true }));
    } else {
      exit();
    }
  }, [dirty, exit]);

  const handleExitSave = useCallback(async () => {
    await handleSave();
    exit();
  }, [handleSave, exit]);

  const handleExitDiscard = useCallback(() => {
    exit();
  }, [exit]);

  const handleExitCancel = useCallback(() => {
    setState((prev) => ({ ...prev, showExitPrompt: false }));
  }, []);

  // Input handling
  useInput(
    (input, key) => {
      // Handle exit prompt
      if (state.showExitPrompt) {
        if (input === "y" || input === "Y") {
          handleExitSave();
          return;
        }
        if (input === "n" || input === "N") {
          handleExitDiscard();
          return;
        }
        if (key.escape) {
          handleExitCancel();
          return;
        }
        return;
      }

      // Handle search mode
      if (state.isSearching) {
        if (key.escape) {
          clearSearch();
          return;
        }
        if (key.return) {
          exitSearchMode();
          return;
        }
        if (key.backspace || key.delete) {
          setState((prev) => ({
            ...prev,
            searchQuery: prev.searchQuery.slice(0, -1),
            selectedIndex: 0,
          }));
          return;
        }
        // Append printable characters to search
        if (input && !key.ctrl && !key.meta) {
          setState((prev) => ({
            ...prev,
            searchQuery: prev.searchQuery + input,
            selectedIndex: 0,
          }));
        }
        return;
      }

      // Handle editing mode - let EditorArea handle it
      if (state.editingKeyId) {
        return;
      }

      // Navigation mode
      if (input === "/") {
        enterSearchMode();
        return;
      }
      if (input === "s") {
        handleSave();
        return;
      }
      if (input === "q") {
        handleQuit();
        return;
      }
      if (key.upArrow) {
        moveSelection(-1);
        return;
      }
      if (key.downArrow) {
        moveSelection(1);
        return;
      }
      if (key.pageUp) {
        moveSelection(-10);
        return;
      }
      if (key.pageDown) {
        moveSelection(10);
        return;
      }
      if (key.return) {
        enterEditMode();
        return;
      }
    },
    { isActive: !state.editingKeyId }
  );

  // Get the currently editing option's metadata
  const editingMeta = state.editingKeyId
    ? options.get(state.editingKeyId)
    : null;
  const editingValue = state.editingKeyId
    ? state.values.get(state.editingKeyId)
    : null;

  return (
    <Box flexDirection="column" height={terminalHeight}>
      <Header filePath={filePath} isDirty={dirty} />

      <Box flexDirection="column" flexGrow={1} flexShrink={1} overflow="hidden" height={visibleRows}>
        {visibleItems.length === 0 ? (
          <Box paddingX={1} paddingY={1}>
            <Text color="gray" dimColor>
              {state.searchQuery
                ? "No matching options found."
                : "No options available."}
            </Text>
          </Box>
        ) : (
          visibleItems.map((item, idx) =>
            item.type === "section" ? (
              <SectionHeader key={`section-${item.name}-${idx}`} name={item.name} />
            ) : (
              <OptionRow
                key={item.keyId}
                meta={item.meta}
                value={state.values.get(item.keyId)}
                isSelected={state.selectedIndex === item.selectableIndex}
              />
            )
          )
        )}
      </Box>

      {state.editingKeyId && editingMeta && (
        <EditorArea
          meta={editingMeta}
          value={editingValue ?? { kind: "unset" }}
          onConfirm={handleEditConfirm}
          onCancel={handleEditCancel}
          onValidate={onValidate}
        />
      )}

      <Footer
        isSearching={state.isSearching}
        searchQuery={state.searchQuery}
        errorMessage={state.errorMessage}
      />

      {state.showExitPrompt && (
        <ExitPrompt
          onSave={handleExitSave}
          onDiscard={handleExitDiscard}
          onCancel={handleExitCancel}
        />
      )}
    </Box>
  );
}

export default App;
