interface TimerUnitI {
  cppTimeUnit: string | undefined;
  unitString: string;
  magnitudeFactorSeconds: number | string;
  magnitudeFactorNanoseconds: number;
}

/**
 * This is supposed to be an enum, but Node.js v25 does bot support TS' enums, only erasable-syntax.
 * Revert to an enum when Node.js supports it, or when we move to a different engine that supports it.
 * This and the "type" declaration below.
 */
export const TimerUnit = {
  NANOSECONDS: 1,
  MICROSECONDS: 2,
  MILLISECONDS: 3,
  SECONDS: 4,
  MINUTES: 5,
  HOURS: 6,
  DAYS: 7,
} as const;
export type TimerUnit = typeof TimerUnit[keyof typeof TimerUnit];

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
  private timerUnit: TimerUnit;

  constructor(unit: TimerUnit = TimerUnit.MILLISECONDS) {
    this.timerUnit = unit;
  }

  get unit() {
    return this.timerUnit;
  }

  set unit(unit: TimerUnit) {
    this.timerUnit = unit;
  }

  getUnitsString() {
    return timerUnitData[this.timerUnit].unitString;
  }

  getCppTimeUnit() {
    return timerUnitData[this.timerUnit].cppTimeUnit;
  }

  getMagnitudeFactorFromSeconds() {
    return timerUnitData[this.timerUnit].magnitudeFactorSeconds;
  }

  getMagnitudeFactorFromNanoseconds() {
    return timerUnitData[this.timerUnit].magnitudeFactorNanoseconds;
  }

  toNanos(duration: number) {
    return duration * this.getMagnitudeFactorFromNanoseconds();
  }
}
