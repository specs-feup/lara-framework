export class IdGeneratorClass {
  idCounter = new Map<string, number>();

  next(key = "") {
    const currentId = this.idCounter.get(key);

    if (currentId !== undefined) {
      this.idCounter.set(key, currentId + 1);
    } else {
      this.idCounter.set(key, 0);
    }

    return `${key}${this.idCounter.get(key) ?? ""}`;
  }
}

const IdGenerator = new IdGeneratorClass();

export default IdGenerator;
