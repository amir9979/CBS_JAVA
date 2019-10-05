package Instances;

import IO_Package.Enum_IO;
import IO_Package.IO_Manager;
import IO_Package.Reader;
import Instances.Agents.Agent;
import Instances.Maps.Coordinate_2D;
import Instances.Maps.Coordinate_3D;
import Instances.Maps.Enum_MapCellType;
import Instances.Maps.GraphMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class InstanceBuilder_MovingAI implements I_InstanceBuilder {


    private final String INDICATOR_MAP = "map";
    private final String FILE_TYPE_MAP = ".map";
    private final String FILE_TYPE_SCENARIO = ".scen";
    private final String INDICATOR_HEIGHT = "height";
    private final String INDICATOR_WIDTH = "width";

    private final String SEPARATOR_DIMENSIONS = " ";

    private final int defaultNumOfAgents = 10;

    private final Stack<MAPF_Instance> instanceStack = new Stack<>();



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
    // todo - change to void!
    public void prepareInstances(String instanceName, InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties) {
        if (!(instancePath instanceof InstanceManager.Moving_AI_Path)) {
            return;
        }
        InstanceManager.Moving_AI_Path moving_ai_path = (InstanceManager.Moving_AI_Path) instancePath;

        MAPF_Instance mapf_instance = null;
        GraphMap graphMap = getMap(moving_ai_path, instanceProperties);

        // create agent properties
        int[] numOfAgentsFromProperties = (instanceProperties == null ? new int[]{this.defaultNumOfAgents}
        : instanceProperties.numOfAgents);

        int numOfBatches = 5;

        String[][] batchesLines = getBatchesLines(moving_ai_path,numOfBatches);

        int curBatch = 0;
        int prevNumOfAgents = 0;

        for (int i = 0; i < numOfAgentsFromProperties.length; i++) {
           // curBatch = curBatch + (prevNumOfAgents / 10) + 1;
            //AgentsProperties agentsProperties = new AgentsProperties(numOfAgentsFromProperties[i], curBatch);
            //Agent[] agents = getAgents(batchesLines, agentsProperties);
            int numberOfAgents =numOfAgentsFromProperties[i];
            Agent[] agents = getAgents(moving_ai_path, instanceProperties,2,numberOfAgents) ;
            //prevNumOfAgents = numOfAgentsFromProperties[i];

            if (instanceName == null || graphMap == null || agents == null) {
                continue; // Invalid parameters
            }
            mapf_instance = new MAPF_Instance(instanceName, graphMap, agents);

            this.instanceStack.push(mapf_instance);
        }

    }

    private String[][] getBatchesLines(InstanceManager.Moving_AI_Path moving_ai_path, int numOfBatches) {
        String[][] batchLines = new String [numOfBatches][10];

        Reader reader = new Reader();
        Enum_IO enum_io = reader.openFile(moving_ai_path.path);
        if (!enum_io.equals(Enum_IO.OPENED)) {
            return null; // couldn't open the file
        }

        /*  =Get data from reader=  */
        String nextLine = reader.getNextLine(); // First line

        for(int i=0 ; i<numOfBatches;i++){
            for (int j=0;j<10;j++){
                nextLine = reader.getNextLine();
                batchLines[i][j]=nextLine;
            }
        }
        reader.closeFile(); // No more data in the file

        return batchLines;
    }


    private GraphMap getMap( InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties ){

        Reader reader = new Reader();
        Enum_IO enum_io = reader.openFile(instancePath.path);
        if( !enum_io.equals(Enum_IO.OPENED) ){
            return null; // couldn't open the file
        }


        /*  =Init values=  */
        GraphMap graphMap = null;
        int[] dimensionsFromProperties = ( instanceProperties == null ? null : instanceProperties.boardSize);
        int[] dimensions = new int[2]; // todo - works only with 2d as of now



        /*  =Get data from reader=  */

        String nextLine = reader.getNextLine(); // First line


        while ( nextLine != null ){

            if(nextLine.startsWith(this.INDICATOR_HEIGHT)){
                String[] splitedLineHeight = nextLine.split(this.SEPARATOR_DIMENSIONS);
                if( IO_Manager.isPositiveInt(splitedLineHeight[1])){
                    dimensions[0] = Integer.parseInt(splitedLineHeight[1]);
                    if( dimensionsFromProperties != null && dimensions[0] != dimensionsFromProperties[0]){
                        reader.closeFile();
                        return null;
                    }
                }

            }else if ( nextLine.startsWith(this.INDICATOR_WIDTH) ){
                String[] splitedLineWidth = nextLine.split(this.SEPARATOR_DIMENSIONS);
                if( IO_Manager.isPositiveInt(splitedLineWidth[1])){
                    dimensions[1] = Integer.parseInt(splitedLineWidth[1]);
                    if( dimensionsFromProperties != null && dimensions[1] != dimensionsFromProperties[1]){
                        reader.closeFile();
                        return null;
                    }
                }

            }else if( nextLine.startsWith(this.INDICATOR_MAP) ){
                String[] mapAsStrings = I_InstanceBuilder.buildMapAsStringArray(reader, dimensions);

                // If instanceProperties is not null check the obstacle percentage
                Integer obstaclePercentage = ( instanceProperties == null ? -1 : instanceProperties.getObstaclePercentage());
                // build map
                graphMap = I_InstanceBuilder.buildGraphMap(mapAsStrings, dimensions.length, cellTypeHashMap, obstaclePercentage);

                break;
            }

            nextLine = reader.getNextLine();
        }

        reader.closeFile(); // No more data in the file

        return graphMap;

    }

    /***  =Build Agents=  ***/

    private Agent buildSingleAgent(int dimensions, String line){
        //split line of an agent
        String[] agentLine = line.split("\t+");
        if( agentLine == null || agentLine.length < 1){
            return null; // invalid agent line
        }

        //split batch number
        int agentBatch = Integer.parseInt(agentLine[0]);
        //split coordinate
        String[] agentCoordinat =agentLine[2].split("\\s+");

        if(dimensions == 2) {
            /*      source values    */
            int source_xValue = Integer.valueOf(agentCoordinat[4]); // check with Morag if first is goal
            int source_yValue = Integer.valueOf(agentCoordinat[5]);
            Coordinate_2D source = new Coordinate_2D(source_xValue, source_yValue);
            /*      Target values    */
            int target_xValue = Integer.valueOf(agentCoordinat[2]);
            int target_yValue = Integer.valueOf(agentCoordinat[3]);
            Coordinate_2D target = new Coordinate_2D(target_xValue, target_yValue);

            return new Agent(agentBatch, source, target);
        }


        if(dimensions == 3) {
            /*      source values    */
            int source_xValue = Integer.valueOf(agentLine[5]);
            int source_yValue = Integer.valueOf(agentLine[6]);
            int source_zValue = Integer.valueOf(agentLine[7]);
            Coordinate_3D source = new Coordinate_3D(source_xValue, source_yValue, source_zValue);
            /*      Target values    */
            int target_xValue = Integer.valueOf(agentLine[2]);
            int target_yValue = Integer.valueOf(agentLine[3]);
            int target_zValue = Integer.valueOf(agentLine[4]);
            Coordinate_3D target = new Coordinate_3D(target_xValue, target_yValue, target_zValue);

            return new Agent(agentBatch, source, target);
        }

        return null; // Bad dimensions input
    }


    private Agent[] buildAgents(Reader reader, int dimensions,InstanceProperties instanceProperties) {


        String nextLine = reader.getNextLine(); // expected version - iעnore
        if (nextLine == null || !IO_Manager.isPositiveInt(nextLine)) {
            return null; // num of agents should be a positive int
        }
        nextLine = reader.getNextLine();
        if (nextLine == null || !IO_Manager.isPositiveInt(nextLine)) {
            return null; //
        }
        //check how we can return num of agents
            int numOfAgents = (instanceProperties.getNumOfAgents() == null ? this.defaultNumOfAgents : instanceProperties.numOfAgents[0]);
            Agent[] agents = new Agent[numOfAgents];

            for (int i = 0; i < agents.length; i++) {
                nextLine = reader.getNextLine();

                Agent agentToAdd = buildSingleAgent(dimensions, nextLine);
                if (agentToAdd == null) {
                    return null; // Bad agent line
                }
                agents[i] = agentToAdd;
            }

        return agents;
    }


        private Agent[] getAgents (InstanceManager.Moving_AI_Path moving_ai_path,InstanceProperties instanceProperties
        ,int dimensions, int numberOfAgents ){

            String[][] batchesAsStrings = getBatchesLines(moving_ai_path,3);
            // imp - Lidor
            int numOfBatches= batchesAsStrings.length;
            // init values
             //(agentsProperties == null ? 1 : agentsProperties.beginAtBatch);
            Agent[] agents = new Agent[numberOfAgents];
            //int batchInArray = batch - 1;
            int nextAgentPointer=0;
            for(int i=0 ; i<numOfBatches;i++) {
                for (int j = 0; j < 10; j++) {
                    Agent agentToAdd = buildSingleAgent(dimensions, batchesAsStrings[i][j]);
                    if (agentToAdd == null) {
                        return null; // Bad agent line
                    }
                    agents[nextAgentPointer] = agentToAdd;
                    nextAgentPointer++;
                }
            }
            return agents;
        }


    @Override
    public MAPF_Instance getNextExistingInstance(){
        if( ! this.instanceStack.empty() ){
            return this.instanceStack.pop();
        }
        return null;
    }

    @Override
    public InstanceManager.InstancePath[] getInstancesPaths(String directoryPath) {
        InstanceManager.InstancePath[] pathArray = IO_Manager.getFilesFromDirectory(directoryPath);
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




    public static void main(String[] args) {
        InstanceBuilder_MovingAI instanceBuilder_movingAI = new InstanceBuilder_MovingAI();

        InstanceManager.InstancePath[] instancePaths = instanceBuilder_movingAI.getInstancesPaths(IO_Manager.buildPath(new String[]{IO_Manager.testResources_Directory,"Instances\\MovingAI"}));
        InstanceManager.Moving_AI_Path moving_ai_path = (InstanceManager.Moving_AI_Path) instancePaths[0];

        InstanceProperties properties = new InstanceProperties(new int[]{512,512},null, new int[]{5,10},"-");
        instanceBuilder_movingAI.prepareInstances("Default name", new InstanceManager.Moving_AI_Path(moving_ai_path.path,moving_ai_path.scenarioPath),properties);

        MAPF_Instance nextInstance = instanceBuilder_movingAI.getNextExistingInstance();
        while (nextInstance != null){
            System.out.println("Got another instance (testing only)");
            nextInstance = instanceBuilder_movingAI.getNextExistingInstance();
        }
    }




    private int getNumOfBatches(int[] values){
        if( values == null ){
            return 5; // default num of batches
        }
        int curBatch = 0;
        int prevNumOfAgents = 0;

        for (int i = 0; i < values.length; i++) {
            curBatch = curBatch + (prevNumOfAgents / 10) + 1;
            prevNumOfAgents = values[i];
        }

        curBatch = curBatch + (prevNumOfAgents / 10) + 1;
        return curBatch;

    }





}
