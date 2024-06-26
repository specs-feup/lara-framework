export default interface WeaverConfiguration {
  weaverName: string;
  weaverPrettyName: string;
  weaverFileName?: string;
  jarPath: string;
  javaWeaverQualifiedName: string;
  importForSideEffects?: string[];
}
