interface TimerUnitI {
  cppTimeUnit: string | undefined;
  unitString: string;
  magnitudeFactorSeconds: number | string;
  magnitudeFactorNanoseconds: number;
}

export enum TimerUnit {
  NANOSECONDS = 1,
  MICROSECONDS = 2,
  MILLISECONDS = 3,
  SECONDS = 4,
  MINUTES = 5,
  HOURS = 6,
  DAYS = 7,
}

const timerUnitData: Record<TimerUnit, TimerUnitI> = {
  [TimerUnit.NANOSECONDS]: {
    cppTimeUnit: "nanoseconds",
    unitString: "ns",
    magnitudeFactorSeconds: 1000000000,
    magnitudeFactorNanoseconds: 1,
  },
  [TimerUnit.MICROSECONDS]: {
    cppTimeUnit: "microseconds",
    unitString: "us",
    magnitudeFactorSeconds: 1000000,
    magnitudeFactorNanoseconds: 1000,
  },
  [TimerUnit.MILLISECONDS]: {
    cppTimeUnit: "milliseconds",
    unitString: "ms",
    magnitudeFactorSeconds: 1000,
    magnitudeFactorNanoseconds: 1000000,
  },
  [TimerUnit.SECONDS]: {
    cppTimeUnit: "seconds",
    unitString: "s",
    magnitudeFactorSeconds: 1,
    magnitudeFactorNanoseconds: 1000000000,
  },
  [TimerUnit.MINUTES]: {
    cppTimeUnit: "minutes",
    unitString: "minutes",
    magnitudeFactorSeconds: "1 / 60",
    magnitudeFactorNanoseconds: 60000000000,
  },
  [TimerUnit.HOURS]: {
    cppTimeUnit: "hours",
    unitString: "hours",
    magnitudeFactorSeconds: "1 / 3600",
    magnitudeFactorNanoseconds: 3600000000000,
  },
  [TimerUnit.DAYS]: {
    cppTimeUnit: undefined,
    unitString: "days",
    magnitudeFactorSeconds: "1 / 86400",
    magnitudeFactorNanoseconds: 86400000000000,
  },
};

export default class TimeUnits {
  #unit: TimerUnit;

  constructor(unit: TimerUnit = TimerUnit.MILLISECONDS) {
    this.#unit = unit;
  }

  get unit() {
    return this.#unit;
  }

  set unit(unit: TimerUnit) {
    this.#unit = unit;
  }

  getUnitsString() {
    return timerUnitData[this.#unit].unitString;
  }

  getCppTimeUnit() {
    return timerUnitData[this.#unit].cppTimeUnit;
  }

  getMagnitudeFactorFromSeconds() {
    return timerUnitData[this.#unit].magnitudeFactorSeconds;
  }

  getMagnitudeFactorFromNanoseconds() {
    return timerUnitData[this.#unit].magnitudeFactorNanoseconds;
  }

  toNanos(duration: number) {
    return duration * this.getMagnitudeFactorFromNanoseconds();
  }
}
