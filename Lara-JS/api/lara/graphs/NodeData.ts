/**
 * Base class for data of graph nodes.
 *
 */
export default class NodeData {
  id: string | undefined;
  parent: string | undefined;

  constructor(id?: string, parent?: string) {
    this.id = id;
    this.parent = parent;
  }
}
