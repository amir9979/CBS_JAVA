package Instances;

import IO_Package.Enum_IO;
import IO_Package.IO_Manager;
import IO_Package.Reader;
import Instances.Agents.Agent;
import Instances.Maps.*;

import java.util.HashMap;

public class InstanceBuilder_BGU implements I_InstanceBuilder {


    // Blocking - check properties ( continue if properties = null )


    private final String INDICATOR_AGENTS = "Agents:";
    private final String SEPARATOR_AGENTS = ",";
    private final String INDICATOR_MAP = "Grid:";
    private final String SEPARATOR_DIMENSIONS = ",";


    /*      =Cell Types=   */
    private final char EMPTY = '.';
    private final char WALL = '@';

    private HashMap<Character,Enum_MapCellType> cellTypeHashMap = new HashMap<Character, Enum_MapCellType>(){{
        put(EMPTY,Enum_MapCellType.EMPTY);
        put(WALL,Enum_MapCellType.WALL);
    }};




    public MAPF_Instance getInstance(String instanceName, InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties) {


        Reader reader=new Reader();
        Enum_IO enum_io =reader.openFile(instancePath.path);
        if( !enum_io.equals(Enum_IO.OPENED) ){
            return null; // couldn't open the file
        }

        String nextLine = reader.getNextLine(); // first line
        if( nextLine == null || ! IO_Manager.isPositiveInt(nextLine)){
            reader.closeFile();
            return null; // first line isn't an index indicator
        }

        /*  =Init values=  */
        MAPF_Instance mapf_instance = null;
        int[] dimensionsFromProperties = instanceProperties.boardSize;
        GraphMap graphMap = null;
        Agent[] agents = null;
        int instance_id = Integer.parseInt(nextLine); // get instance id,


        /*  =Get data from reader=  */

        nextLine = reader.getNextLine(); // Second line

        while ( nextLine != null ){

            switch (nextLine){

                case INDICATOR_MAP:
                    String dimensionsAsString = reader.getNextLine();
                    int[] dimensions = getDimensions(dimensionsAsString);

                    // Checks validity with instanceProperties:
                    // numOfDimensions = dimensions from instanceProperties
                    if (dimensions == null || dimensions.length != dimensionsFromProperties.length){
                        reader.closeFile();
                        return null; // unexpected dimensions line
                    }

                    for (int i = 0; i < dimensionsFromProperties.length; i++) {
                        if( dimensions[i] != dimensionsFromProperties[i]){
                            reader.closeFile();
                            return null; // unexpected dimensions line
                        }
                    }
                    String[] mapAsStrings = this.buildMapAsStringArray(reader, dimensions);

                    // build map
                    graphMap = buildGraphMap(mapAsStrings, dimensionsFromProperties.length, instanceProperties.obstacleRate);

                    // todo - missing check validity of num of obstacles in graphMap
                    break;

                case INDICATOR_AGENTS:
                    agents = buildAgents(reader, dimensionsFromProperties.length);

                    // Checks validity with instanceProperties
                    if (agents == null || agents.length != instanceProperties.numOfAgents){
                        agents = null; // different than instanceProperties
                    }
                    break;


            } // switch end

            nextLine = reader.getNextLine();
        }

        reader.closeFile(); // No more data in the file

        if ( instanceName == null || graphMap == null || agents == null){
            return null; // Invalid parameters
        }

        instanceName = instanceName + "-" + instance_id; // Example: "Instance-16-0-7" + "-" + "0"
        mapf_instance = new MAPF_Instance(instanceName, graphMap, agents);
        return mapf_instance;

    }




    /***  =Build Agents=  ***/

    private Agent buildSingleAgent(int dimensions, String line){

        String[] agentLine = line.split(this.SEPARATOR_AGENTS);

        if( agentLine == null || agentLine.length < 1){
            return null; // invalid agent line
        }

        for (int i = 0; i < agentLine.length; i++) {
            if( ! IO_Manager.isPositiveInt(agentLine[i])){
                return null; // dimensions should be a positive int
            }
        }

        int agentID = Integer.parseInt(agentLine[0]);

        if(dimensions == 2 && agentLine.length == 5) {
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


        if(dimensions == 3 && agentLine.length == 7) {
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


    private Agent[] buildAgents(Reader reader,int dimensions) {

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

    private int[] getDimensions(String dimensionsAsString) {

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

        return dimensions; // Example: {16,16}
    }




    private String[] buildMapAsStringArray(Reader reader, int[] dimensions){

        int xAxis_length = dimensions[0];
        String[] mapAsStringArray = new String[xAxis_length];
        for (int xIndex = 0; xIndex < xAxis_length; xIndex++) {

            String nextLine = reader.getNextLine();
            if ( nextLine != null ){
                mapAsStringArray[xIndex] = nextLine;
            }else {
                return null; // unexpected num of lines
            }
        }
        return mapAsStringArray;
    }


    private GraphMap buildGraphMap(String[] mapAsStrings, int numOfDimensions, float obstacleRate) {

        switch ( numOfDimensions ){
            case 2:
                Enum_MapCellType[][] mapAsCellType_2D = build_2D_cellTypeMap(mapAsStrings, obstacleRate);
                return MapFactory.newSimple4Connected2D_GraphMap(mapAsCellType_2D);

            case 3:
                Enum_MapCellType[][][] mapAsCellType_3D = build_3D_cellTypeMap(mapAsStrings);
                return null; // niceToHave - change to newSimple 4Connected 3D_GraphMap if exists in MapFactory
        }


        return null; // If something went wrong ( should return in switch-case )
    }


    private Enum_MapCellType[][] build_2D_cellTypeMap(String[] mapAsStrings ,float obstacleRate) {
        // done - convert String[] to Enum_MapCellType[][] using this.cellTypeHashMap

        int xAxis_length = mapAsStrings.length;
        int yAxis_length = mapAsStrings[0].length();
        int numOfWalls = 0; // used to check obstacle rate


        Enum_MapCellType[][] cellTypeMap = new Enum_MapCellType[xAxis_length][yAxis_length];

        for (int xIndex = 0; xIndex < xAxis_length; xIndex++) {
            for (int yIndex = 0; yIndex < yAxis_length; yIndex++) {

                // done - convert using this.cellTypeHashMap
                Enum_MapCellType cellType = cellTypeHashMap.get(mapAsStrings[xIndex].charAt(yIndex));

                if ( cellType.equals(Enum_MapCellType.WALL)){
                    numOfWalls++; // add one wall to counter
                }
                cellTypeMap[xIndex][yIndex] = cellType;
            }
        }


//        if ( numOfWalls/(xAxis_length*yAxis_length) != obstacleRate){
//            // todo - check with Dor that this is correct
//            return null; // Invalid obstacle rate
//        }

        return cellTypeMap;
    }

    private Enum_MapCellType[][][] build_3D_cellTypeMap(String[] mapAsStrings) {
        // niceToHave - no need to implement for now
        return null;
    }





    @Override
    public InstanceManager.InstancePath[] getInstancesPaths(String directoryPath) {
        // done - get list of paths from directoryPath
        InstanceManager.InstancePath[] pathArray = IO_Manager.getFilesFromDirectory(directoryPath);

        return pathArray;
    }


}
