"use strict";

laraImport("clava.Clava");
laraImport("lara.util.ProcessExecutor");
laraImport("lara.vitishls.VitisHlsReportParser");
laraImport("lara.tool.Tool");
laraImport("lara.tool.ToolUtils");

class VitisHls extends Tool {
    topFunction;
    platform;
    clock;
    vitisDir;
    vitisProjName;
    sourceFiles = [];

    constructor(topFunction, clock = 10, platform = "xcvu5p-flva2104-1-e", disableWeaving = false) {
        super("VITIS-HLS", disableWeaving);

        this.topFunction = topFunction;
        this.platform = platform;
        this.setClock(clock);

        this.vitisDir = "VitisHLS";
        this.vitisProjName = "VitisHLSClavaProject";
    }

    setTopFunction(topFunction) {
        this.topFunction = topFunction;
        return this;
    }

    setPlatform(platform) {
        this.platform = platform;
        return this;
    }

    setClock(clock) {
        if (clock <= 0) {
            throw new Error(
                `[${this.toolName}] Clock value must be a positive integer!`
            );
        } else {
            this.clock = clock;
        }
        return this;
    }

    addSource(file) {
        this.sourceFiles.push(file);
    }

    synthesize(verbose = true) {
        println(`[${this.toolName}] Setting up Vitis HLS executor`);

        this.clean();
        this.#generateTclFile();
        this.#executeVitis(verbose);
        return Io.isFile(this.#getSynthesisReportPath());
    }

    clean() {
        Io.deleteFolderContents(this.vitisDir);
    }

    #getSynthesisReportPath() {
        var path = this.vitisDir + "/" + this.vitisProjName;
        path += "/solution1/syn/report/csynth.xml";
        return path;
    }

    #executeVitis(verbose) {
        println(`[${this.toolName}] Executing Vitis HLS`);

        const pe = new ProcessExecutor();
        pe.setWorkingDir(this.vitisDir);
        pe.setPrintToConsole(verbose);
        pe.execute("vitis_hls", "-f", "script.tcl");

        println(`[${this.toolName}]Finished executing Vitis HLS`);
    }

    #getTclInputFiles() {
        var str = "";
        const weavingFolder = ToolUtils.parsePath(Clava.getWeavingFolder());

        // make sure the files are woven
        Io.deleteFolderContents(weavingFolder);
        Clava.writeCode(weavingFolder);

        // if no files were added, we assume that every woven file should be used
        if (this.sourceFiles.length == 0) {
            println(
                "[" +
                this.toolName +
                "] No source files specified, assuming current AST is the input"
            );
            for (var file of Io.getFiles(Clava.getWeavingFolder())) {
                const exts = [".c", ".cpp", ".h", ".hpp"];
                const res = exts.some((ext) => file.name.includes(ext));
                if (res) str += "add_files " + weavingFolder + "/" + file.name + "\n";
            }
        } else {
            for (const file of this.sourceFiles) {
                str += "add_files " + weavingFolder + "/" + file + "\n";
            }
        }
        return str;
    }

    #generateTclFile() {
        const cmd = `
open_project ${this.vitisProjName}
set_top ${this.topFunction}
${this.#getTclInputFiles()}
open_solution \"solution1\" -flow_target vitis
set_part { ${this.platform}}
create_clock -period ${this.clock} -name default
csynth_design
exit
    `;

        Io.writeFile(this.vitisDir + "/script.tcl", cmd);
    }

    getSynthesisReport() {
        println(`[${this.toolName}] Processing synthesis report`);

        const parser = new VitisHlsReportParser(this.#getSynthesisReportPath());
        const json = parser.getSanitizedJSON();

        println(`[${this.toolName}] Finished processing synthesis report`);
        return json;
    }

    preciseStr(n, decimalPlaces) {
        return (+n).toFixed(decimalPlaces);
    }

    prettyPrintReport(report) {
        const period = this.preciseStr(report["clockEstim"], 2);
        const freq = this.preciseStr(report["clockTarget"], 2);

        const out = `
----------------------------------------
Vitis HLS synthesis report

Targeted a ${report["platform"]} with target clock ${freq} ns

Achieved an estimated clock of ${period} ns(${freq} MHz)

Latency of ${report["latency"]} cycles for top function ${report["topFun"]
            }
Estimated execution time of ${report["execTime"]} s

Resource usage:
FF:   ${report["FF"]} (${this.preciseStr(report["perFF"], 2)}%)
LUT:  ${report["LUT"]} (${this.preciseStr(report["perLUT"], 2)}%)
BRAM: ${report["BRAM"]} (${this.preciseStr(report["perBRAM"], 2)}%)
DSP:  ${report["DSP"]} (${this.preciseStr(report["perDSP"], 2)}%)
----------------------------------------`;

        println(out);
    }
}
