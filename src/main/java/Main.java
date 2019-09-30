import Metrics.S_Metrics;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    // Todo - set notes in all projects
    // imp
    // done
    // testme
    // blocking



    public static void main(String[] args) {

        try {
            S_Metrics.addOutputStream(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleRunManager simpleRunManager = new SimpleRunManager();
        simpleRunManager.addPrioritisedPlanningSolver();
        simpleRunManager.setExperiments();

        simpleRunManager.runAllExperiments();

        try {
            S_Metrics.exportCSV(new FileOutputStream("C:\\Users\\John\\Desktop\\results"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
