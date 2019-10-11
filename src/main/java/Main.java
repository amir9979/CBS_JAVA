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

        simpleRun();

        //moragRequestedRun();



    }

    public static void simpleRun(){

        SimpleRunManager simpleRunManager = new SimpleRunManager();
        simpleRunManager.addOnlinePrioritisedPlanningSolver();

//        simpleRunManager.setExperiments();
        simpleRunManager.addOnlineExperiment1();
        simpleRunManager.addOnlineExperiment2();
        simpleRunManager.addOnlineExperiment3();

        simpleRunManager.runAllExperiments();
    }


    public static void moragRequestedRun(){


        MoragRequested_RunManager moragRequested_runManager = new MoragRequested_RunManager();
        moragRequested_runManager.setSolvers();
        moragRequested_runManager.setExperiments();

        moragRequested_runManager.runAllExperiments();


        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            S_Metrics.exportCSV(new FileOutputStream("C:\\Users\\John\\Desktop\\results\\" + df.format(System.currentTimeMillis()) + ".csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Get Instance by absolute path */

        String path = IO_Manager.buildPath(
                new String[]{   IO_Manager.testResources_Directory,
                        "Instances\\\\Instance-8-15-5-17 - hard one - cost 29 and some corridors"}
        );
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);

        InstanceManager manager = new InstanceManager(new InstanceBuilder_BGU());


        MAPF_Instance instance = moragRequested_runManager.getInstanceWithAbsolutePath(manager, instancePath);
    }

}
