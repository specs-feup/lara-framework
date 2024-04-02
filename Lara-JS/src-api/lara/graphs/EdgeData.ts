/**
 * Base class for data of graph edges.
 */
export default class EdgeData {
  id: string | undefined;
  source: string | undefined;
  target: string | undefined;

  constructor(id?: string, source?: string, target?: string) {
    this.id = id;
    this.source = source;
    this.target = target;
  }
}
