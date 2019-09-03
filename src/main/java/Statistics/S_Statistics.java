package Statistics;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This is a static class, responsible for collecting and exporting metrics generated during experiments.
 * Many of its functions are optional.
 * A minimal use case would include:
 * a. adding {@link InstanceReport}s with {@link #newInstanceReport()}, and storing the results of experiments in them.
 * b. calling {@link #exportCSV(OutputStream, String[])} with a {@link java.io.FileOutputStream}, and an array of header fields.
 *
 * Example of a more advanced usecase:
 * a. calling {@link #addOutputStream(OutputStream, InstanceReportToString, HeaderToString)} with various {@link java.io.OutputStream}s.
 * b. calling {@link #setHeader(String[])} to set a header (enables valid csv output).
 * c1. adding {@link InstanceReport}s with {@link #newInstanceReport()}, and adding the results of experiments to them.
 * c2. after each report is filled, calling {@link InstanceReport#commit()} to immediately output its contents to the
 * output streams.
 */
public class S_Statistics {
    ////      MEMBERS      ////
    /**
     * Optional. Defines a header. Useful for formats such as CSV.
     */
    private static String[] header = new String[0];
    /**
     *
     */
    private final static List<InstanceReport> reports = new ArrayList<InstanceReport>();
    // the following three lists are managed together, so that any index refers to the same OutputStream in outputStreams
    /**
     * OutputStreams for {@link InstanceReport#commit() comitted} {@link InstanceReport}s to be output to.
     */
    private static List<OutputStream> outputStreams = new ArrayList<OutputStream>();
    /**
     * Functions to convert {@link InstanceReport}s to strings for output.
     */
    private static List<InstanceReportToString> instanceReportToStringsForOSs = new ArrayList<InstanceReportToString>();
    /**
     * Functions to convert the header to String to output at the start of an output stream. Can contain nulls, meaning
     * a header is not needed for the OutputStream of the same index.
     */
    private static List<HeaderToString> headerToStringsForOSs = new ArrayList<HeaderToString>();
    ////      INTERFACES      ////
    /**
     * Defines a function which converts an {@link InstanceReport} to a String.
     * This class contains static methods which comply with this interface, and may be used when this interface is
     * required.
     * @see #instanceReportToStringCSV(InstanceReport)
     */
    public interface InstanceReportToString{
        String instanceReportToString(InstanceReport instanceReport);
    }

    /**
     * Defines a function which converts a String array representing a header, to a String.
     * {@link S_Statistics} contains static methods which comply with this interface, and may be used when this
     * interface is required.
     * @see #headerArrayToStringCSV(String[])
     */
    public interface HeaderToString{
        String headerToString(String[] headerArray);
    }


    ////      SETTERS AND GETTERS      ////

    /**
     * Sets the {@link #header} and outputs the new header to all relevant streams.
     * @param newHeader the new header @NotNull
     * @throws IOException if an I/O error occurs when outputing the new header to one of the streams.
     */
    public static void setHeaderAndOutputNewHeader(String[] newHeader) throws IOException {
        if(newHeader != null){
            S_Statistics.header = newHeader;
            if(newHeader.length > 0 ){outputHeaderToAllRelevantStreams();}
        }
    }

    /**
     * Sets the {@link #header}. Doesn't output the new header to any streams.
     * @param newHeader the new header @NotNull
     */
    public static void setHeader(String[] newHeader) throws IOException {
        if(newHeader != null){S_Statistics.header = newHeader;}
    }

    private static String[] getHeader() {
        return Arrays.copyOf(S_Statistics.header, S_Statistics.header.length);
    }

    public static void clearHeader() {header = new String[0];}

    /**
     * Creates a new, empty, {@link InstanceReport}, saves a reference to it, and returns it.
     * @return a new instance of {@link InstanceReport}.
     */
    public static InstanceReport newInstanceReport(){
        InstanceReport newReport = new InstanceReport();
        S_Statistics.reports.add(newReport);
        return newReport;
    }

    public static boolean removeReport(InstanceReport report){
        return S_Statistics.reports.remove(report);
    }

    public static void clearReports(){
        S_Statistics.reports.clear();
    }

    /**
     * Clears all class fields, essentially resetting the class.
     */
    public static void clearAll(){
        clearHeader();
        clearReports();
        clearOutputStreams();
    }

    /**
     * Adds the given output stream to the list of OutputStreams. When {@link InstanceReport}s are committed, or when
     * {@link #exportAll()} is called, {@link InstanceReport}s will be written to this given OutputStream.
     * If headerToString isn't null, writes the current {@link #header} to the given {@link OutputStream}.
     * @param outputStream an output stream.
     * @param instanceReportToString function to convert {@link InstanceReport}s to Strings to write them to the given {@link OutputStream}.
     * @param headerToString function to convert the header to String to write it to the given {@link OutputStream}. If null, header will not be written.
     * @throws IOException if an I/O error occurs.
     */
    public static void addOutputStream(OutputStream outputStream, InstanceReportToString instanceReportToString,
                                       HeaderToString headerToString) throws IOException {
        if (outputStream != null && instanceReportToString != null) {
            outputStreams.add(outputStream);
            instanceReportToStringsForOSs.add(instanceReportToString);
            headerToStringsForOSs.add(headerToString); // null is interpreted as "no need for header"
            //output the header to the new stream if a header is needed and is set.
            if(header.length > 0 && headerToString != null){
                outputStream.write(headerToString.headerToString(header).getBytes());
            }
        }
    }

    public static void addOutputStream(OutputStream outputStream,
                                       InstanceReportToString instanceReportToString) throws IOException {
        addOutputStream(outputStream, instanceReportToString, null);
    }

    public static void removeOutputStream(OutputStream outputStream){
        int streamIndex = outputStreams.indexOf(outputStream);
        outputStreams.remove(streamIndex);
        headerToStringsForOSs.remove(streamIndex);
        instanceReportToStringsForOSs.remove(streamIndex);
    }

    public static void clearOutputStreams(){
        outputStreams.clear();
        headerToStringsForOSs.clear();
        instanceReportToStringsForOSs.clear();
    }

    ////      OUTPUT      ////

    // nicetohave groupBy, which gets a comparator to group by
    // nicetohave sort, which gets a comparator to sort by

    ////    conversions to strings      ////

    //      csv     //

//    /**
//     * Returns a string representation of the current {@link #header}, in a format compatible with CSV.
//     * @return a string representation of the current {@link #header}, in a format compatible with CSV.
//     */
//    public static String currentHeaderToStringCSV(){
//        return headerArrayToStringCSV(header);
//    }

    /**
     * Returns a string representation of the given header, in a format compatible with CSV.
     * @param delimiter the delimiter to use to delimit the fields.
     * @return a string representation of the given header, in a format compatible with CSV.
     */
    private static String headerToStringCSV(String[] headerArray, char delimiter){
        StringBuilder headerLine = new StringBuilder();
        for(String field : headerArray){
            headerLine.append(field);
            headerLine.append(delimiter);
        }
        headerLine.append('\n');
        return headerLine.toString();
    }

    /**
     * Returns a string representation of the given header, in a format compatible with CSV.
     * @return a string representation of the given header, in a format compatible with CSV.
     */
    public static String headerArrayToStringCSV(String[] headerArray){
        return headerToStringCSV(headerArray, ',');
    }

    /**
     * Returns a string representation of the information in an instanceReport, in a format compatible with CSV.
     * Because CSV requires all lines adhere to a single header, only fields present in {@link #header} will be included.
     * @param instanceReport the InstanceReport to convert to a string. @NotNull.
     * @param delimiter the delimiter to use to delimit the fields.
     * @return a string representation of the information in an instanceReport, in a format compatible with CSV.
     */
    private static String instanceReportToStringCSV(InstanceReport instanceReport, char delimiter){
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

    //      human readable      //

    /**
     * Returns a string representation of the information in an instanceReport, in a format that is suitable for easy
     * reading. Useful for outputing to a console to monitor the experiment.
     * @param instanceReport the InstanceReport to convert to a string. @NotNull.
     * @return a string representation of the information in an instanceReport, in a format readable format.
     */
    public static String instanceReportToHumanReadableString(InstanceReport instanceReport){
        return instanceReport.toString();
    }

    // nicetohave tosrting json

    ////      outputing to the streams      ////

    private static void outputHeaderToStream(OutputStream outputStream, String[] headerArray,
                                             HeaderToString headerToString) throws IOException {
        outputStream.write(headerToString.headerToString(headerArray).getBytes());
    }

    private static void outputHeaderToAllRelevantStreams() throws IOException {
        for (int i = 0; i < outputStreams.size(); i++) {
            HeaderToString headerToString = headerToStringsForOSs.get(i);
            if(headerToString != null){
                outputHeaderToStream(outputStreams.get(i), S_Statistics.header, headerToString);
            }
        }
    }

    private static void outputInstanceReportToStream(OutputStream outputStream, InstanceReport instanceReport,
                                                     InstanceReportToString instanceReportToString) throws IOException {
        outputStream.write(instanceReportToString.instanceReportToString(instanceReport).getBytes());
    }

    private static void outputInstanceReportToAllStreams(InstanceReport instanceReport) throws IOException {
        for (int i = 0; i < outputStreams.size(); i++) {
            outputInstanceReportToStream(outputStreams.get(i), instanceReport, instanceReportToStringsForOSs.get(i));
        }
    }

    private static void outputAllInstanceReportsToStream(OutputStream outputStream,
                                                         InstanceReportToString instanceReportToString) throws IOException {
        for (InstanceReport report :
                S_Statistics.reports) {
            outputInstanceReportToStream(outputStream, report, instanceReportToString);
        }
    }

    ////        OUTPUT API      ////

    /**
     * Writes the committed {@link InstanceReport} to all the OutputStreams in {@link #outputStreams}
     * @param instanceReport the committed {@link InstanceReport}
     * @throws IOException If an I/O error occurs.
     */
    static void commit(InstanceReport instanceReport) throws IOException {
        outputInstanceReportToAllStreams(instanceReport);
    }

    /**
     * Exports all the {@link InstanceReport}s to the given output stream.
     * @param out the OutputStream to write to.
     * @param instanceReportToString the function with which to convert {@link InstanceReport}s to Strings.
     * @param headerToString the function with which to convert the {@link #header} to a String.
     * @throws IOException if an I/O error occurs
     */
    public static void exportToOutputStream(OutputStream out, InstanceReportToString instanceReportToString, HeaderToString headerToString) throws IOException {
        if(headerToString != null) {
            outputHeaderToStream(out, S_Statistics.header, headerToString);
            for (InstanceReport report :
                    reports) {
                outputInstanceReportToStream(out, report, instanceReportToString);
            }
        }
    }

    /**
     * Exports all the {@link InstanceReport}s to all the saved OutputStreams.
     * @throws IOException if an I/O error occurs
     */
    public static void exportAll() throws IOException {
        outputHeaderToAllRelevantStreams();
        int i = 0;
        for (OutputStream outputStream :
                outputStreams) {
            outputAllInstanceReportsToStream(outputStream, instanceReportToStringsForOSs.get(i));
            i++;
        }
    }

    /**
     * Exports all the {@link InstanceReport}s to the given OutputStream, in CSV format.
     * @param outputStream the OutputStream to write to. Typically a {@link java.io.FileOutputStream}.
     * @param headerArray the desired header for the CSV output. Only {@link InstanceReport} fields which are contained
     *                    in this header will be written.
     * @throws IOException if an I/O error occurs
     */
    public static void exportCSV(OutputStream outputStream, String[] headerArray) throws IOException {
        outputHeaderToStream(outputStream, headerArray, S_Statistics::headerArrayToStringCSV);
        outputAllInstanceReportsToStream(outputStream, S_Statistics::instanceReportToStringCSV);
    }

}
