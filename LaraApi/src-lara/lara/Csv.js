import Io from "./Io.js";
import JavaTypes from "./util/JavaTypes.js";
import { println } from "../core/output.js";
/**
 * Methods related with the CSV format.
 */
export default class Csv {
    /**
     * Reads a CSV file and returns an object with the data inside the file.
     */
    static read(path, delimiter) {
        const file = Io.getPath(path);
        if (!Io.isFile(file)) {
            throw "Io.readCsv: path '" + path + "' is not a file";
        }
        return Csv.parse(new JavaTypes.CsvReader(file, ","));
    }
    /**
     * @param contents -
     */
    static parse(contents, delimiter = ",") {
        const CsvReader = JavaTypes.CsvReader;
        let csvReader = undefined;
        if (typeof contents === "string") {
            csvReader = new CsvReader(contents, delimiter);
        }
        // Undocumented option
        else if (CsvReader.class.isInstance(contents)) {
            csvReader = contents;
        }
        else {
            throw "Csv.parse: Unsupported type for argument 'contents'";
        }
        const csvData = {};
        csvData.header = [];
        for (var headerValue of csvReader.getHeader()) {
            csvData.header.push(headerValue.trim());
        }
        csvData.lines = [];
        //var headerList = csvReader.getHeader();
        let lineCounter = 0;
        while (csvReader.hasNext()) {
            lineCounter++;
            // Read line
            const values = csvReader.next();
            // Check line has the same number of elements as headerList
            if (values.length !== csvData.header.length) {
                println("Csv.parse: number of elements mismatch, header has " +
                    csvData.header.length +
                    "elements, line " +
                    lineCounter +
                    " has " +
                    values.length +
                    ". Skipping line");
                continue;
            }
            // Create line
            const line = [];
            for (const value of values) {
                line.push(value.trim());
            }
            csvData.lines.push(line);
        }
        csvReader.close();
        return csvData;
    }
    /**
     * Converts an object in a specific format to a CSV string.
     *
     * @param data - an aggregate of values with the same keys, uses the keys of the given aggregate as the name of the entries, and the keys of the values as column entries.
     * @param separator - the separator character to use. By default is ;.
     * @returns the CSV corresponding to the object.
     */
    static generate(data, separator = ";") {
        let csv = "";
        const ln = "\n";
        // Colect columns, to build header
        csv = "Name";
        const columns = [];
        const seenColumns = new Set();
        Object.entries(data).forEach((benchData) => {
            for (const benchKey in benchData) {
                if (seenColumns.has(benchKey)) {
                    continue;
                }
                seenColumns.add(benchKey);
                csv += separator + benchKey;
                columns.push(benchKey);
            }
        });
        csv += ln;
        Object.keys(data).forEach((benchName) => {
            csv += benchName;
            const benchData = data[benchName];
            for (const benchKey of columns) {
                let benchValue = benchData[benchKey];
                benchValue = benchValue !== undefined ? benchValue : "";
                csv += separator + benchValue;
            }
            csv += ln;
        });
        return csv;
    }
}
//# sourceMappingURL=Csv.js.map