package Instances;

import IO_Package.Enum_IO;
import IO_Package.IO_Manager;
import IO_Package.Reader;
import Instances.Agents.Agent;
import Instances.Maps.*;

import java.util.*;

public class InstanceBuilder_MovingAI implements I_InstanceBuilder {


    public static final String FILE_TYPE_MAP = ".map";
    public static final String FILE_TYPE_SCENARIO = ".scen";

    // Indicators
    private final String INDICATOR_MAP = "map";
    private final String INDICATOR_HEIGHT = "height";
    private final String INDICATOR_WIDTH = "width";

    // Separators
    private final String SEPARATOR_DIMENSIONS = " ";
    private final String SEPARATOR_MAP = "";
    private final String SEPARATOR_SCENARIO = "\t";


    /*  =Default Values=    */
    private final Float defaultObstacleRate = (float)-1;
    private final int defaultNumOfAgents = 10;
    private final int defaultNumOfBatches = 5;
    private final int defaultNumOfAgentsInSingleBatch = 10;

    /*  =Default Index Values=    */
    // Line example: "1	maps/rooms/8room_000.map	512	512	500	366	497	371	6.24264"
    //    Start: ( 500 , 366 )
    //    Goal: ( 497 , 371 )
    private final int INDEX_AGENT_SOURCE_XVALUE = 4;
    private final int INDEX_AGENT_SOURCE_YVALUE = 5;
    private final int INDEX_AGENT_TARGET_XVALUE = 6;
    private final int INDEX_AGENT_TARGET_YVALUE = 7;



    private final ArrayList<MAPF_Instance> instanceList = new ArrayList<>();



    /*      =Cell Types=   */
    private final char EMPTY = '.';
    private final char WALL = '@';
    private final char TREE = 'T';


    private HashMap<Character, Enum_MapCellType> cellTypeHashMap = new HashMap<Character, Enum_MapCellType>(){{
        put(EMPTY,Enum_MapCellType.EMPTY);
        put(WALL,Enum_MapCellType.WALL);
        put(TREE,Enum_MapCellType.TREE);
    }};








    @Override
    public void prepareInstances(String instanceName, InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties) {

        if (!(instancePath instanceof InstanceManager.Moving_AI_Path)) {
            return;
        }

        InstanceManager.Moving_AI_Path moving_ai_path = (InstanceManager.Moving_AI_Path) instancePath;


        MAPF_Instance mapf_instance = null;
        GraphMap graphMap = getMap(moving_ai_path, instanceProperties);
        if( graphMap == null ){
            return;
        }


        // create agent properties
        int[] numOfAgentsFromProperties = (instanceProperties == null ? new int[]{this.defaultNumOfAgents} : instanceProperties.numOfAgents);


        // done - add implementation of 'getAgents'
        int numOfBatches = this.getNumOfBatches(numOfAgentsFromProperties);
        ArrayList<String> agentLinesQueue = getAgentLines(moving_ai_path, numOfBatches * this.defaultNumOfAgentsInSingleBatch); //

        for (int i = 0; i < numOfAgentsFromProperties.length; i++) {

            Agent[] agents = getAgents(agentLinesQueue,numOfAgentsFromProperties[i]);

            if (instanceName == null || agents == null) {
                continue; // Invalid parameters
            }

            mapf_instance = new MAPF_Instance(instanceName, graphMap, agents);

            this.instanceList.add(mapf_instance);

        }

    }

    // Returns an array of agents using the line queue
    private Agent[] getAgents(ArrayList<String> agentLinesList, int numOfAgents) {

        if( agentLinesList == null ){
            return null;
        }

        Agent[] arrayOfAgents = new Agent[numOfAgents];
        int numOfAgentsByBatches = this.getNumOfBatches(new int[]{numOfAgents});

        // Iterate over all the agents in numOfAgentsByBatches
        for (int id = 0; !agentLinesList.isEmpty() && id < numOfAgentsByBatches * this.defaultNumOfAgentsInSingleBatch; id++) {

            /* Straight from the API :
                The remove() and poll() methods differ only in their behavior when the queue is empty,
                the remove() method throws an exception, while the poll() method returns null   */

            if( id < numOfAgents ){
                Agent agentToAdd = buildSingleAgent(id ,agentLinesList.remove(0));
                arrayOfAgents[id] =  agentToAdd; // Wanted agent to add
            }else {
                agentLinesList.remove(0);
            }
        }

        return arrayOfAgents;
    }

    private Agent buildSingleAgent(int id, String agentLine) {

        // done - build agent from string
        String[] splitLine = agentLine.split(this.SEPARATOR_SCENARIO);
        // Init coordinates
        int source_xValue = Integer.parseInt(splitLine[this.INDEX_AGENT_SOURCE_XVALUE]);
        int source_yValue = Integer.parseInt(splitLine[this.INDEX_AGENT_SOURCE_YVALUE]);
        Coordinate_2D source = new Coordinate_2D(source_xValue, source_yValue);
        int target_xValue = Integer.parseInt(splitLine[this.INDEX_AGENT_TARGET_XVALUE]);
        int target_yValue = Integer.parseInt(splitLine[this.INDEX_AGENT_TARGET_YVALUE]);
        Coordinate_2D target = new Coordinate_2D(target_xValue, target_yValue);


        return new Agent(id, source, target);
    }


    // Returns agentLines from scenario file as a queue
    private ArrayList<String> getAgentLines(InstanceManager.Moving_AI_Path moving_ai_path, int numOfNeededAgents) {

        // Open scenario file
        Reader reader = new Reader();
        Enum_IO enum_io = reader.openFile(moving_ai_path.scenarioPath);
        if( !enum_io.equals(Enum_IO.OPENED) ){
            return null; // couldn't open the file
        }


        /*  =Get data from reader=  */
        String nextLine = reader.getNextLine(); // First line

        ArrayList<String> agentsLines = new ArrayList<>(); // Init queue of agents lines

        // Add lines as the num of needed agents
        for (int i = 0; nextLine != null && i < numOfNeededAgents ; i++) {
            nextLine = reader.getNextLine(); // next line
            agentsLines.add(nextLine);
        }

        return agentsLines;
    }


    private GraphMap getMap( InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties ){

        Reader reader = new Reader();
        Enum_IO enum_io = reader.openFile(instancePath.path);
        if( !enum_io.equals(Enum_IO.OPENED) ){
            return null; // couldn't open the file
        }


        /*  =Init values=  */
        GraphMap graphMap = null;
        MapDimensions dimensionsFromProperties = ( instanceProperties == null || instanceProperties.mapSize == null ? new MapDimensions() : instanceProperties.mapSize);
        MapDimensions dimensionsFromFile = new MapDimensions();



        /*  =Get data from reader=  */
        String nextLine = reader.getNextLine(); // First line

        while ( nextLine != null ){

            if(nextLine.startsWith(this.INDICATOR_HEIGHT)){
                String[] splitedLineHeight = nextLine.split(this.SEPARATOR_DIMENSIONS);
                if( IO_Manager.isPositiveInt(splitedLineHeight[1])){
                    dimensionsFromFile.yAxis_length = Integer.parseInt(splitedLineHeight[1]);
                    dimensionsFromFile.numOfDimensions++;
                    if( dimensionsFromProperties.yAxis_length > 0
                            && dimensionsFromFile.yAxis_length != dimensionsFromProperties.yAxis_length ){
                        reader.closeFile();
                        return null; // Bad yAxis length
                    }
                }

            }else if ( nextLine.startsWith(this.INDICATOR_WIDTH) ){
                String[] splitedLineWidth = nextLine.split(this.SEPARATOR_DIMENSIONS);
                if( IO_Manager.isPositiveInt(splitedLineWidth[1])){
                    dimensionsFromFile.xAxis_length = Integer.parseInt(splitedLineWidth[1]);
                    dimensionsFromFile.numOfDimensions++;
                    if( dimensionsFromProperties.xAxis_length > 0
                            && dimensionsFromFile.xAxis_length != dimensionsFromProperties.xAxis_length ){
                        reader.closeFile();
                        return null; // Bad xAxis length
                    }
                }

            }else if( nextLine.startsWith(this.INDICATOR_MAP) ){
                String[] mapAsStrings = I_InstanceBuilder.buildMapAsStringArray(reader, dimensionsFromFile);

                // If instanceProperties is not null check the obstacle percentage
                float obstacleRate = ( instanceProperties == null ? this.defaultObstacleRate : instanceProperties.getObstacleRate());
                // build map
                graphMap = I_InstanceBuilder.buildGraphMap(mapAsStrings, this.SEPARATOR_MAP, dimensionsFromFile, this.cellTypeHashMap, obstacleRate);

                break;
            }

            nextLine = reader.getNextLine();
        }

        reader.closeFile(); // No more data in the file

        return graphMap;

    }







    @Override
    public MAPF_Instance getNextExistingInstance(){
        if( ! this.instanceList.isEmpty() ){
            return this.instanceList.remove(0);
        }
        return null;
    }






    @Override
    public InstanceManager.InstancePath[] getInstancesPaths(String directoryPath) {
        InstanceManager.InstancePath[] pathArray = IO_Manager.getFilesFromDirectory(directoryPath);
        if(pathArray == null){
            return null;
        }

        ArrayList<InstanceManager.InstancePath> list = new ArrayList<>();

        for (InstanceManager.InstancePath instancePath : pathArray ) {
            if ( instancePath.path.endsWith(this.FILE_TYPE_MAP) ){

                String scenario = instancePath.path + this.FILE_TYPE_SCENARIO;
                list.add( new InstanceManager.Moving_AI_Path(instancePath.path,scenario));
            }
        }

        pathArray = new InstanceManager.InstancePath[list.size()];
        for (int i = 0; i < pathArray.length; i++) {
            pathArray[i] = list.get(i);
        }
        return pathArray;
    }




    private int getNumOfBatches(int[] values){
        if( values == null || values.length == 0){
            return this.defaultNumOfBatches; // default num of batches
        }
        int curBatch = 0;

        for (int i = 1; i < values.length + 1; i++) {
            // Examples:
            // values[i-1] = 15 -> division = 1.5 -> ( 1.5 > 1 ) addition = 1
            // values[i-1] = 20 -> division = 2.0 -> ( 2.0 !> 2 )addition = 0
            double division = values[i-1] / 10.0;
            int addition = (division > (int)division ? 1 : 0);
            curBatch = curBatch + (int)division + addition;
        }

        return curBatch;

    }


}
