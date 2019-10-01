import Metrics.S_Metrics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Main {

    // Todo - set notes in all projects
    // imp
    // done
    // testme
    // blocking



    public static void main(String[] args) {

        try {
            S_Metrics.addOutputStream(System.out, S_Metrics::instanceReportToHumanReadableString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleRunManager simpleRunManager = new SimpleRunManager();
        simpleRunManager.addOnlinePrioritisedPlanningSolver();

        simpleRunManager.setExperiments();
//        simpleRunManager.addOnlineExperiment1();
//        simpleRunManager.addOnlineExperiment2();
//        simpleRunManager.addOnlineExperiment3();

        simpleRunManager.runAllExperiments();

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            S_Metrics.exportCSV(new FileOutputStream("C:\\Users\\John\\Desktop\\results\\" + df.format(System.currentTimeMillis()) + ".csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
