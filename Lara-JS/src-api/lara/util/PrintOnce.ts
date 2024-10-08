export default class PrintOnce {
  static messagesSet = new Set<string>();

  static message(message: string) {
    if (message === undefined) {
      return;
    }

    if (this.messagesSet.has(message)) {
      return;
    }

    this.messagesSet.add(message);

    console.log(message);
  }
}
