/**
 * Copyright 2020 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.weavingreport;

import org.junit.Test;
import org.lara.interpreter.profile.ReportField;
import org.lara.interpreter.profile.WeavingReport;

import pt.up.fe.specs.util.SpecsStrings;

public class WeaverReportTester {

    @Test
    public void testEnumReportInc() {
        var report = new WeavingReport();

        var incs = 10_000_000;

        // Warm-up
        for (int i = 0; i < incs; i++) {
            report.inc(ReportField.JOIN_POINTS);
        }

        // Measure
        var start = System.nanoTime();
        for (int i = 0; i < incs; i++) {
            report.inc(ReportField.JOIN_POINTS);
        }
        System.out.println(SpecsStrings.takeTime("Enum inc (" + incs + "): ", start));
        // System.out.println("Get: " + report.get(ReportField.JOIN_POINTS));
    }

    @Test
    public void testEnumReportIncAmount() {
        var report = new WeavingReport();

        var incs = 10_000_000;

        // Warm-up
        for (int i = 0; i < incs; i++) {
            report.inc(ReportField.JOIN_POINTS, 10);
        }

        // Measure
        var start = System.nanoTime();
        for (int i = 0; i < incs; i++) {
            report.inc(ReportField.JOIN_POINTS, 10);
        }
        System.out.println(SpecsStrings.takeTime("Enum inc amount (" + incs + ")", start));
        // System.out.println("Get: " + report.get(ReportField.JOIN_POINTS));
    }

    @Test
    public void testDataKeyReportIncInt() {
        var report = new DataKeyReport();

        var incs = 10_000_000;

        // Warm-up
        for (int i = 0; i < incs; i++) {
            report.incInt(DataKeyReport.JOIN_POINTS);
        }

        // Measure
        var start = System.nanoTime();
        for (int i = 0; i < incs; i++) {
            report.incInt(DataKeyReport.JOIN_POINTS);
        }
        System.out.println(SpecsStrings.takeTime("DataKey incInt (" + incs + "): ", start));
        // System.out.println("Get: " + report.get(ReportField.JOIN_POINTS));
    }

    @Test
    public void testDataKeyReportIncIntAmount() {
        var report = new DataKeyReport();

        var incs = 10_000_000;

        // Warm-up
        for (int i = 0; i < incs; i++) {
            report.incInt(DataKeyReport.JOIN_POINTS, 10);
        }

        // Measure
        var start = System.nanoTime();
        for (int i = 0; i < incs; i++) {
            report.incInt(DataKeyReport.JOIN_POINTS, 10);
        }
        System.out.println(SpecsStrings.takeTime("DataKey incInt amount (" + incs + "): ", start));
        // System.out.println("Get: " + report.get(ReportField.JOIN_POINTS));
    }

    @Test
    public void testDataKeyReportInc() {
        var report = new DataKeyReport();

        var incs = 10_000_000;

        // Warm-up
        for (int i = 0; i < incs; i++) {
            report.inc(DataKeyReport.JOIN_POINTS);
        }

        // Measure
        var start = System.nanoTime();
        for (int i = 0; i < incs; i++) {
            report.inc(DataKeyReport.JOIN_POINTS);
        }
        System.out.println(SpecsStrings.takeTime("DataKey inc (" + incs + "): ", start));
        // System.out.println("Get: " + report.get(ReportField.JOIN_POINTS));
    }

    @Test
    public void testDataKeyReportIncAmount() {
        var report = new DataKeyReport();

        var incs = 10_000_000;

        // Warm-up
        for (int i = 0; i < incs; i++) {
            report.inc(DataKeyReport.JOIN_POINTS, 10);
        }

        // Measure
        var start = System.nanoTime();
        for (int i = 0; i < incs; i++) {
            report.inc(DataKeyReport.JOIN_POINTS, 10);
        }
        System.out.println(SpecsStrings.takeTime("DataKey inc amount (" + incs + "): ", start));
        // System.out.println("Get: " + report.get(ReportField.JOIN_POINTS));
    }
}
