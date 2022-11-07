"use strict";

class VitisHlsReportParser {
    reportPath;

    constructor(reportPath) {
        this.reportPath = reportPath;
    }

    #xmlToJson(xml) {
        //parses only the "leaves" of the XML string, which is enough for us. For now.
        const regex = /(?:<([a-zA-Z'-\d_]*)(?:\s[^>]*)*>)((?:(?!<\1).)*)(?:<\/\1>)|<([a-zA-Z'-]*)(?:\s*)*\/>/gm;

        const json = {};
        for (const match of xml.matchAll(regex)) {
            const key = match[1] || match[3];
            const val = match[2] && this.#xmlToJson(match[2]);
            json[key] = ((val && Object.keys(val).length) ? val : match[2]) || null;
        }
        return json;
    }

    getSanitizedJSON() {
        const raw = this.#getRawJSON();

        const fmax = this.calculateMaxFrequency(raw["EstimatedClockPeriod"]);
        const execTime = this.calculateExecutionTime(raw["Latency"], fmax);

        var json = {
            "platform": raw["Part"],
            "topFun": raw["TopModelName"],
            "clockTarget": raw["TargetClockPeriod"],
            "clockEstim": raw["EstimatedClockPeriod"],
            "fmax": fmax,
            "execTime": execTime,
            "FF": raw["FF"],
            "LUT": raw["LUT"],
            "BRAM": raw["BRAM_18K"],
            "DSP": raw["DSP"],
            "latency": raw["Latency"],
            "availFF": raw["AVAIL_FF"],
            "availLUT": raw["AVAIL_LUT"],
            "availBRAM": raw["AVAIL_BRAM"],
            "availDSP": raw["AVAIL_DSP"],
            "perFF": raw["FF"] * 100 / raw["AVAIL_FF"],
            "perLUT": raw["LUT"] * 100 / raw["AVAIL_LUT"],
            "perBRAM": raw["BRAM_18K"] * 100 / raw["AVAIL_BRAM"],
            "perDSP": raw["DSP"] * 100 / raw["AVAIL_DSP"]
        };
        return json;
    }

    #getRawJSON() {
        const xml = Io.readFile(this.reportPath);
        return this.#xmlToJson(xml);
    }

    calculateMaxFrequency(clockEstim) {
        return (1 / clockEstim) * 1000;
    }

    calculateExecutionTime(latency, freqMHz) {
        const freqHz = freqMHz * 1e6;
        return latency / freqHz;
    }
}