package Instances;

import IO_Package.Enum_IO;
import IO_Package.IO_Manager;
import IO_Package.Reader;
import Instances.Agents.Agent;
import Instances.Maps.*;

import java.util.HashMap;

public class InstanceBuilder_BGU implements I_InstanceBuilder {


    private final String INDICATOR_AGENTS = "Agents:";
    private final String SEPARATOR_AGENTS = ",";
    private final String INDICATOR_MAP = "Grid:";
    private final String SEPARATOR_DIMENSIONS = ",";


    /*      =Cell Types=   */
    private final char EMPTY = '.';
    private final char WALL = '@';

    private HashMap<Character,Enum_MapCellType> cellTypeHashMap;

    public InstanceBuilder_BGU(){
        this.initCellTypeHashMap();
    }

    public static MAPF_Instance getInstance(String instanceName, String instancePath) { //return to InstanceManager.InstancePath instancePath


        Reader reader=new Reader();
        Enum_IO enum_io =reader.openFile(instancePath); //return to instancePath.path
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
        int numOfDimensions = -1;
        GraphMap graphMap = null;
        Agent[] agents = null;
        int instance_id = Integer.parseInt(nextLine); // get instance id


        /*  =Get data from reader=  */

        nextLine = reader.getNextLine(); // Second line

        while ( nextLine != null ){

            switch (nextLine){

                case "Grid:":
                    String dimensionsAsString = reader.getNextLine();
                    int[] dimensions = getDimensions(dimensionsAsString);
                    if (dimensions == null){
                        reader.closeFile();
                        return null; // unexpected dimensions line
                    }
                    numOfDimensions = dimensions.length;
                    String[] mapAsStrings = buildMapAsStringArray(reader, dimensions);
                    // build map
                    graphMap = buildGraphMap(mapAsStrings, numOfDimensions);
                    break;

                case "Agents:":

                    if ( numOfDimensions < 1 ){
                        reader.closeFile();
                        return null; // Missing dimensions
                    }
                    agents = buildAgents(reader, numOfDimensions);

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

    public static Agent buildSingleAgent(int dimensions, String line){

        String[] agentLine = line.split(","); ///remembet t return to this. seperator agent

        if( agentLine == null || agentLine.length < 1){
            return null; // invalid agent line
        }

        for (int i = 0; i < agentLine.length; i++) {
            if( ! IO_Manager.isPositiveInt(agentLine[i])){
                return null; // dimensions should be a positive int
            }
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
            int target_zValue = Integer.valueOf(agentLine[2]);
            Coordinate_3D target = new Coordinate_3D(target_xValue, target_yValue, target_zValue);

            return new Agent(agentID, source, target);
        }

        return null; // Bad dimensions input
    }

    public static Agent[] buildAgents(Reader reader, int dimensions) {

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

    public static int[] getDimensions(String dimensionsAsString) {

        int[] dimensions = null;
        if(dimensionsAsString.contains(",")) {
            String[] splittedLine = dimensionsAsString.split(",");
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

    public static String[] buildMapAsStringArray(Reader reader, int[] dimensions){

        int xAxis_length = dimensions[0];
        String[] mapAsStringArray = new String[xAxis_length];
        for (int xIndex = 0; xIndex < xAxis_length; xIndex++) {

            String nextLine = reader.getNextLine();
            if ( nextLine != null ){
                mapAsStringArray[xIndex] = nextLine;
//                System.out.println(mapAsStringArray[xIndex]);
            }else {
                return null; // unexpected num of lines
            }
        }
        return mapAsStringArray;
    }

    public static GraphMap buildGraphMap(String[] mapAsStrings, int numOfDimensions) {

        switch ( numOfDimensions ){
            case 2:
                Enum_MapCellType[][] mapAsCellType_2D = build_2D_cellTypeMap(mapAsStrings);
                return MapFactory.newSimple4Connected2D_GraphMap(mapAsCellType_2D);

            case 3:
                Enum_MapCellType[][][] mapAsCellType_3D = build_3D_cellTypeMap(mapAsStrings);
                return null; // niceToHave - change to newSimple 4Connected 3D_GraphMap if exists in MapFactory
        }


        return null; // If something went wrong ( should return in switch-case )
    }

    public static Enum_MapCellType[][] build_2D_cellTypeMap(String[] mapAsStrings) {

        int xAxis_length = mapAsStrings.length;
        int yAxis_length = mapAsStrings[0].length();


        Enum_MapCellType[][] cellTypeMap = new Enum_MapCellType[xAxis_length][yAxis_length];

        for (int xIndex = 0; xIndex < xAxis_length; xIndex++) {

            for (int yIndex = 0; yIndex < yAxis_length; yIndex++) {
                if((mapAsStrings[xIndex].charAt(yIndex))=='.'){
                    Enum_MapCellType cellType = Enum_MapCellType.EMPTY;
                    cellTypeMap[xIndex][yIndex] = cellType;
                }
                else {
                    Enum_MapCellType cellType = Enum_MapCellType.WALL;
                    cellTypeMap[xIndex][yIndex] = cellType;
                }
            }

        }
        return cellTypeMap;
    }

    private static Enum_MapCellType[][][] build_3D_cellTypeMap(String[] mapAsStrings) {

        // niceToHave - no need to implement for now
        return null;
    }

    private void initCellTypeHashMap() {

        this.cellTypeHashMap = new HashMap<>();
        this.cellTypeHashMap.put(this.EMPTY,Enum_MapCellType.EMPTY);
        this.cellTypeHashMap.put(this.WALL,Enum_MapCellType.WALL);

    }

    @Override
    public MAPF_Instance getInstance(String instanceName, InstanceManager.InstancePath instancePath) {
        return null;
    }

    @Override
    public InstanceManager.InstancePath[] getInstancesPaths(String directoryPath) {
        return new InstanceManager.InstancePath[0];
    }

    public static void main(String[] args) {


        int[]boardSize=new int[2];
        InstanceProperties instanceProperties=new InstanceProperties(boardSize,0,7,1);
        I_InstanceBuilder i_instanceBuilder=new InstanceBuilder_BGU();
        getInstance("Instance-16-0-7-0", "Instance-16-0-7-0");

        //buildSingleAgent
//        String line="0,5,2,9,7\n";
//        int dimentions=2;
//        Coordinate_2D end=new Coordinate_2D(5,2);
//        Coordinate_2D start=new Coordinate_2D(9,7);
//        buildSingleAgent(dimentions,line);


//        buildMapAsStringArray
//        Reader reader=new Reader();
//        reader.openFile("Instance-16-0-7-0");
//
//        reader.getNextLine();
//        reader.getNextLine();
//        reader.getNextLine();
//
//        int [] dimentions= new int[2];
//        dimentions[0]=16;
//        dimentions[1]=16;
//        buildMapAsStringArray(reader,dimentions);




    }

}
