package Instances;

import IO_Package.Enum_IO;
import IO_Package.IO_Manager;
import IO_Package.Reader;
import Instances.Agents.Agent;
import Instances.Maps.*;

import java.util.HashMap;
import java.util.Stack;

public class InstanceBuilder_BGU implements I_InstanceBuilder {


    // testme - check properties ( continue if properties = null )


    private final String INDICATOR_AGENTS = "Agents:";
    protected final String SEPARATOR_AGENTS = ",";
    private final String INDICATOR_MAP = "Grid:";
    private final String SEPARATOR_DIMENSIONS = ",";
    private final String SEPARATOR_MAP = "";


    private final Stack<MAPF_Instance> instanceStack = new Stack<>();



    /*      =Cell Types=   */
    private final char EMPTY = '.';
    private final char WALL = '@';

    private HashMap<Character,Enum_MapCellType> cellTypeHashMap = new HashMap<>(){{
        put(EMPTY,Enum_MapCellType.EMPTY);
        put(WALL,Enum_MapCellType.WALL);
    }};




    /*  =Default Values=    */
    private final int defaultNumOfDimensions = 2;
    private final Integer defaultObstaclePercentage = -1;
    private final int[] defaultDimensions = new int[0];
    private final int[] defaultNumOfAgents = new int[0];





    private MAPF_Instance getInstance(String instanceName, InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties) {

        // Try to open file
        Reader reader=new Reader();
        Enum_IO enum_io =reader.openFile(instancePath.path);
        if( !enum_io.equals(Enum_IO.OPENED) ){
            return null; // couldn't open the file
        }


        /*  =Init values=  */
        MAPF_Instance mapf_instance = null;
        GraphMap graphMap = null;
        Agent[] agents = null;
        MapDimensions mapDimensionsFromFile = null;


        /*  =Get data from reader=  */

        String nextLine = reader.getNextLine(); // First line

        while ( nextLine != null ){

            switch (nextLine){

                case INDICATOR_MAP:
                    String dimensionsAsString = reader.getNextLine();
                    mapDimensionsFromFile = getMapDimensions(dimensionsAsString);
                    // Checks validity of dimensions:
                    if ( mapDimensionsFromFile == null || ! this.checkMapDimensions(mapDimensionsFromFile, instanceProperties, reader)){
                        break;
                    }



                    String[] mapAsStrings = I_InstanceBuilder.buildMapAsStringArray(reader, mapDimensionsFromFile);

                    // If instanceProperties is not null check the obstacle percentage
                    Integer obstaclePercentage = ( instanceProperties == null ? this.defaultObstaclePercentage : instanceProperties.getObstaclePercentage());
                    // build map
                    graphMap = I_InstanceBuilder.buildGraphMap(mapAsStrings, this.SEPARATOR_MAP, mapDimensionsFromFile, this.cellTypeHashMap, obstaclePercentage);

                    // done - missing check validity of num of obstacles in graphMap
                    break; // end case

                case INDICATOR_AGENTS:
                    agents = buildAgents(reader, this.defaultNumOfDimensions); // currently supports only 2D

                    if (agents == null || instanceProperties == null){
                        break; // No need to check the num of agents
                    }


                    int index = I_InstanceBuilder.equalsAny(agents.length, instanceProperties.numOfAgents);
                    // index equals -1 if agents.length doesn't match any of the values in numOfAgents
                    if( instanceProperties.numOfAgents.length > 0 && index == -1 ){
                        agents = null;
                    }

                    break; // end case


            } // switch end

            nextLine = reader.getNextLine();
        }

        reader.closeFile(); // No more data in the file

        if ( instanceName == null || graphMap == null || agents == null){
            return null; // Invalid parameters
        }

        mapf_instance = new MAPF_Instance(instanceName, graphMap, agents);
        return mapf_instance;

    }

    @Override
    public void prepareInstances(String instanceName, InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties){
        MAPF_Instance mapf_instance = this.getInstance(instanceName, instancePath, instanceProperties);
        if ( mapf_instance != null ){
            this.instanceStack.push(mapf_instance);
        }
    }



    @Override
    public MAPF_Instance getNextExistingInstance(){
        if( ! this.instanceStack.empty() ){
            return this.instanceStack.pop();
        }
        return null;
    }




    /***  =Validity check=  ***/

    /***
     *
     * @param mapDimensionsFromFile - Board size from file
     * @param instanceProperties - Board size from properties
     * @param reader  - closes the reader for invalid values
     * @return boolean - if dimensions are valid.
     */
    private boolean checkMapDimensions(MapDimensions mapDimensionsFromFile, InstanceProperties instanceProperties, Reader reader){

        if( mapDimensionsFromFile == null || mapDimensionsFromFile.numOfDimensions < 1){
            reader.closeFile();
            return false; // Bad dimensions values
        }

        if( instanceProperties == null || instanceProperties.mapSize.numOfDimensions == 0) {
            return true; // Missing properties values
        }


        // Equals to all the values in the array
        if( mapDimensionsFromFile.equals(instanceProperties.mapSize)){
            return true; // Valid Board size
        }

        // Invalid values, close reader
        reader.closeFile();
        return false;

    }



    /***  =Build Agents=  ***/

    protected Agent buildSingleAgent(int dimensions, String line){

        String[] agentLine = line.split(this.SEPARATOR_AGENTS);

        if( agentLine.length < 1){
            return null; // invalid agent line
        }


        int agentID = Integer.parseInt(agentLine[0]);


        if(dimensions == 2) {
            /*      source values    */
            int source_xValue = Integer.valueOf(agentLine[3]);
            int source_yValue = Integer.valueOf(agentLine[4]);
            Coordinate_2D source = new Coordinate_2D(source_xValue, source_yValue);
            /*      Target values    */
            int target_xValue = Integer.valueOf(agentLine[1]);
            int target_yValue = Integer.valueOf(agentLine[2]);
            Coordinate_2D target = new Coordinate_2D(target_xValue, target_yValue);

            return new Agent(agentID, source, target);
        }


        if(dimensions == 3) {
            /*      source values    */
            int source_xValue = Integer.valueOf(agentLine[4]);
            int source_yValue = Integer.valueOf(agentLine[5]);
            int source_zValue = Integer.valueOf(agentLine[6]);
            Coordinate_3D source = new Coordinate_3D(source_xValue, source_yValue, source_zValue);
            /*      Target values    */
            int target_xValue = Integer.valueOf(agentLine[1]);
            int target_yValue = Integer.valueOf(agentLine[2]);
            int target_zValue = Integer.valueOf(agentLine[3]);
            Coordinate_3D target = new Coordinate_3D(target_xValue, target_yValue, target_zValue);

            return new Agent(agentID, source, target);
        }

        return null; // Bad dimensions input
    }


    private Agent[] buildAgents(Reader reader, int dimensions) {

        String nextLine = reader.getNextLine(); // expected num of agents
        if( nextLine == null || ! IO_Manager.isPositiveInt(nextLine)) {
            return null; // num of agents should be a positive int
        }

        int numOfAgents = Integer.parseInt(nextLine);
        Agent[] agents = new Agent[numOfAgents];

        for (int i = 0; i < agents.length; i++) {
            nextLine = reader.getNextLine();

            Agent agentToAdd = buildSingleAgent(dimensions, nextLine);
            if ( agentToAdd == null ){
                return null; // Bad agent line
            }
            agents[i] = agentToAdd;
        }

        return agents;
    }



    /***  =Build Map and Dimensions=  ***/

    private MapDimensions getMapDimensions(String dimensionsAsString) {

        int[] dimensions = null;
        if(dimensionsAsString.contains(SEPARATOR_DIMENSIONS)) {
            String[] splittedLine = dimensionsAsString.split(SEPARATOR_DIMENSIONS);
            dimensions = new int[splittedLine.length];

            for (int i = 0; i < dimensions.length; i++) {
                if ( IO_Manager.isPositiveInt( splittedLine[i] )){
                    dimensions[i] = Integer.parseInt(splittedLine[i]);
                }else{
                    return null; // dimensions should be positive integers
                }
            }

        }else {
            return null; // Missing expected separator
        }

        return new MapDimensions(dimensions);
    }





    @Override
    public InstanceManager.InstancePath[] getInstancesPaths(String directoryPath) {
        // done - get list of paths from directoryPath
        InstanceManager.InstancePath[] pathArray = IO_Manager.getFilesFromDirectory(directoryPath);

        return pathArray;
    }


}
