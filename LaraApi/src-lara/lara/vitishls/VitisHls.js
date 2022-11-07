"use strict";

laraImport("clava.Clava");
laraImport("lara.util.ProcessExecutor");
laraImport("lara.vitishls.VitisHlsReportParser");
laraImport("lara.Tool");
laraImport("lara.ToolUtils");

class VitisHls extends Tool {
    topFunction;
    platform;
    clock;
    vitisDir;
    vitisProjName;
    sourceFiles = [];

    constructor(disableWeaving) {
        super("VITIS-HLS", disableWeaving);

        this.topFunction = undefined;
        this.platform = undefined;
        this.clock = undefined;
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
            throw new Error("[" + this.toolName + "] Clock value must be a positive integer!");
        } else {
            this.clock = clock;
        }
        return this;
    }

    addSource(file) {
        this.sourceFiles.push(file);
    }

    synthesize(verbose = true) {
        println("[" + this.toolName + "] Setting up Vitis HLS executor");

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
        println("[" + this.toolName + "] Executing Vitis HLS");

        const pe = new ProcessExecutor();
        pe.setWorkingDir(this.vitisDir);
        pe.setPrintToConsole(verbose);
        pe.execute("vitis_hls", "-f", "script.tcl");

        println("[" + this.toolName + "] Finished executing Vitis HLS");
    }

    #getTclInputFiles() {
        var str = "";
        const weavingFolder = ToolUtils.parsePath(Clava.getWeavingFolder());

        // make sure the files are woven
        Io.deleteFolderContents(weavingFolder);
        Clava.writeCode(weavingFolder);

        // if no files were added, we assume that every woven file should be used
        if (this.sourceFiles.length == 0) {
            println("[" + this.toolName + "] No source files specified, assuming current AST is the input");
            for (var file of Io.getFiles(Clava.getWeavingFolder())) {
                const exts = [".c", ".cpp", ".h", ".hpp"];
                const res = exts.some(ext => file.name.includes(ext));
                if (res)
                    str += "add_files " + weavingFolder + "/" + file.name + "\n"
            }
        }
        else {
            for (const file of this.sourceFiles) {
                str += "add_files " + weavingFolder + "/" + file + "\n";
            }
        }
        return str;
    }

    #generateTclFile() {
        var cmd = "open_project " + this.vitisProjName + "\n";
        cmd += "set_top " + this.topFunction + "\n";
        cmd += this.#getTclInputFiles();
        cmd += "open_solution \"solution1\" -flow_target vitis\n";
        cmd += "set_part {" + this.platform + "}\n";
        cmd += "create_clock -period " + this.clock + " -name default\n";
        cmd += "csynth_design\n";
        cmd += "exit\n";

        Io.writeFile(this.vitisDir + "/script.tcl", cmd);
    }

    getSynthesisReport() {
        println("[" + this.toolName + "] Processing synthesis report");

        const parser = new VitisHlsReportParser(this.#getSynthesisReportPath());
        const json = parser.getSanitizedJSON();

        println("[" + this.toolName + "] Finished processing synthesis report");
        return json;
    }

    preciseStr(n, decimalPlaces) {
        return (+n).toFixed(decimalPlaces);
    }

    prettyPrintReport(report) {
        println("----------------------------------------");
        println("Vitis HLS synthesis report");
        println("");
        println("Targeted a " + report["platform"] + ", with target clock " + this.preciseStr(report["clockTarget"], 2) + "ns");
        println("");
        println("Achieved an estimated clock of " + this.preciseStr(report["clockEstim"], 2) +
            "ns (" + this.preciseStr(report["fmax"], 2) + "MHz)");
        println("");
        println("Latency of " + report["latency"] + " cycles for top function " + report["topFun"]);
        println("Estimated execution time of " + report["execTime"] + "s");
        println();
        println("Resource usage:");
        println("FF:   " + report["FF"] + " (" + this.preciseStr(report["perFF"], 2) + "%)");
        println("LUT:  " + report["LUT"] + " (" + this.preciseStr(report["perLUT"], 2) + "%)");
        println("BRAM: " + report["BRAM"] + " (" + this.preciseStr(report["perBRAM"], 2) + "%)");
        println("DSP:  " + report["DSP"] + " (" + this.preciseStr(report["perDSP"], 2) + "%)");
        println("----------------------------------------");
    }
}