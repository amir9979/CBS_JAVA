public class Main {

    // Todo - set notes in all projects
    // imp
    // done
    // testme
    // blocking



    public static void main(String[] args) {

        simpleRun();


    }



    public static void simpleRun(){

        SimpleRunManager simpleRunManager = new SimpleRunManager();
        simpleRunManager.setSolvers();
        simpleRunManager.setExperiments();
        simpleRunManager.runAllExperiments();
    }



}
