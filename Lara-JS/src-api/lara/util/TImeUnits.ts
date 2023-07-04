class TimeUnits{
    
    _timerUnit: any;
    _cppTimeUnit: any;
    _unitsString: any;
    _magnitudeFactorSeconds: any;
    _magnitudeFactorNanoseconds: any;
    unit: any;
    
    constructor(unit: any){

        this._timerUnit = {
            NANOSECONDS: 1,
            MICROSECONDS: 2,
            MILLISECONDS: 3,
            SECONDS: 4,
            MINUTES: 5,
            HOURS: 6,
            DAYS: 7,
        };

        // C++ std::chrono macros
        this._cppTimeUnit = {
            1: "nanoseconds",
            2: "microseconds",
            3: "milliseconds",
            4: "seconds",
            5: "minutes",
            6: "hours",
            7: undefined,
        };

        // units string value for output
        this._unitsString = {
            1: "ns",
            2: "us",
            3: "ms",
            4: "s",
            5: "minutes",
            6: "hours",
            7: "days",
        };

        // C conversion from seconds to other magintudes through multiplication
        this._magnitudeFactorSeconds = {
            1: 1000000000,
            2: 1000000,
            3: 1000,
            4: 1,
            5: "1/60",
            6: "1/3600",
            7: "1/86400",
        };
        // Java conversion from nanoseconds to other magintudes through multiplication
        this._magnitudeFactorNanoseconds = {
            1: 1,
            2: 1000,
            3: 1000000,
            4: 1000000000,
            5: 60000000000,
            6: 3600000000000,
            7: 86400000000000,
        };

        let decodedUnit = this._decodeUnit(unit);

        // Read the unit or use the default
        this.unit = decodedUnit === undefined ? this._timerUnit.MILLISECONDS : decodedUnit;
    }

    getUnitsString(){
        return this._unitsString[this.unit];
    }
    
    getCppTimeUnit() {
        return this._cppTimeUnit[this.unit];
    }
    
    getMagnitudeFactorFromSeconds() {
        return this._magnitudeFactorSeconds[this.unit];
    }

    getMagnitudeFactorFromNanoseconds() {
        return this._magnitudeFactorNanoseconds[this.unit];
    }


    toNanos(duration: number) {
	    return duration * this._magnitudeFactorNanoseconds[this.unit];
    }

    _decodeUnit(unit: any) {
        
        if(unit === undefined) {
            return unit;
        }

        if(typeof unit  === "number") {
            if(unit < 1 || unit > 7) {
                throw "Expected a number between 1 (nanoseconds) and 7 (days). You can also use strings (e.g., \"MICROSECONDS\")";
            }
            return unit;
        }
        if(typeof unit  === "string") {
            const numberUnit: any = this._timerUnit[unit];
            if(numberUnit === undefined) {
                let names: Array<string> = [];
                for(let propertyName in this._timerUnit) {
                    names.push(propertyName);
                }
                throw "Time unit '"+ unit +"' not supported, available values: " + names.join(", ");
            }
            return numberUnit;
        }
        throw "Type '" + (typeof unit) + "' not supported as input, use 'string' or 'number'";
    }
}