package Statistics;


import Experiments.A_Experiment;
import Instances.MAPF_Instance;
import javafx.util.Pair;
import jdk.internal.joptsimple.util.KeyValuePair;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This is a static class, responsible for collecting and exporting metrics generated during experiments.
 * Many of its functions are optional.
 * A minimal use case would include:
 * a. adding {@link InstanceReport}s with {@link #newInstanceReport()}, and storing the results of experiments in them.
 * b. calling {@link #exportCSV(OutputStream, String[])} with a {@link java.io.FileOutputStream}, and an array of header fields.
 *
 * A more advanced usecase:
 * a. calling {@link #addOutputStream(OutputStream, InstanceReportToString, boolean)} with various {@link java.io.OutputStream}s.
 * b. calling {@link #setHeader(String[])} to set a header (enables valid csv output).
 * c. adding {@link InstanceReport}s with {@link #newInstanceReport()}, and adding the results of experiments to them.
 * d. after each report is filled, calling {@link InstanceReport#commit()} to immediately output its contents to the
 * output streams.
 */
public class S_Statistics {
    ////      MEMBERS      ////
    private static String[] header = new String[0];
    private final static List<InstanceReport> reports = new ArrayList<InstanceReport>();
    ////      CONSTANTS AND INTERFACES      ////
    public static class StandardFields{
        public final static String expandedNodes = "Expanded Nodes";
        public final static String generatedNodes = "Generated Nodes";
        public final static String startTime = "Start Time";
        public final static String endTime = "End Time";
        public final static String elapsedTime = "Elapsed Time";
    }
    public interface InstanceReportToString{
        String instanceReportToString(InstanceReport instanceReport);
    }

    ////      SETTERS AND GETTERS      ////

    public static void setHeader(String[] newHeader){
        header = newHeader;
        //todo also output the new header to all relevant output streams.
    }

    private static String[] getHeader() {
        return header;
    }

    public static void clearHeader() {header = new String[0];}

    public static InstanceReport newInstanceReport(){
        InstanceReport newReport = new InstanceReport();
        reports.add(newReport);
        return newReport;
    }

    public static boolean removeReport(InstanceReport report){
        return reports.remove(report);
    }

    public static void clearReports(){
        reports.clear();
    }

    public static void clearAll(){
        clearHeader();;
        clearReports();
    }

    public static void addOutputStream(OutputStream outputStream, InstanceReportToString irTosTring, boolean needsHeader){
        //imp
    }

    ////      OUTPUT      ////

    //imp export
    //imp outputing to given output streams. associate each output stream with f(InstanceReport):String . also require optional initializer and finalizer g(): String .
    // imp groupBy, which gets a comparator to group by
    // imp sort, which gets a comparator to sort by

    // imp tosrting csv
    // imp tosrting json

    /**
     * Returns a string representation of the current {@link #header}, in a format compatible with CSV.
     * @param delimiter the delimiter to use to delimit the fields.
     * @return a string representation of the current {@link #header}, in a format compatible with CSV.
     */
    private static String headerToStringCSV(char delimiter){
        StringBuilder headerLine = new StringBuilder();
        for(String field : header){
            headerLine.append(field);
            headerLine.append(delimiter);
        }
        headerLine.append('\n');
        return headerLine.toString();
    }

    /**
     * Returns a string representation of the current {@link #header}, in a format compatible with CSV.
     * @return a string representation of the current {@link #header}, in a format compatible with CSV.
     */
    private static String headerToStringCSV(){
        return headerToStringCSV(',');
    }

    /**
     * Returns a string representation of the information in an instanceReport, in a format compatible with CSV.
     * Because CSV requires all lines adhere to a single header, only fields present in {@link #header} will be included.
     * @param instanceReport the InstanceReport to convert to a string. @NotNull.
     * @param delimiter the delimiter to use to delimit the fields.
     * @return a string representation of the information in an instanceReport, in a format compatible with CSV.
     */
    public static String instanceReportToStringCSV(InstanceReport instanceReport, char delimiter){
        StringBuilder reportLine = new StringBuilder();

        for(String field : header){
            if(instanceReport.hasField(field)){
                reportLine.append(instanceReport.getValue(field));
            }
            reportLine.append(delimiter);
        }
        reportLine.append('\n');
        return reportLine.toString();
    }

    /**
     * Returns a string representation of the information in an instanceReport, in a format compatible with CSV.
     * Because CSV requires all lines adhere to a single header, only fields present in {@link #header} will be included.
     * @param instanceReport the InstanceReport to convert to a string. @NotNull.
     * @return a string representation of the information in an instanceReport, in a format compatible with CSV.
     */
    public static String instanceReportToStringCSV(InstanceReport instanceReport){
        return  instanceReportToStringCSV(instanceReport, ',');
    }

    //imp
    private static LinkedHashMap<String, String> instanceReportToSortedMap(InstanceReport instanceReport){
        return null; //imp
    }

    //imp
    static void commit(InstanceReport instanceReport){

    }

    public static void exportCSV(OutputStream outputStream, String[] header){
        //imp
    }

}
