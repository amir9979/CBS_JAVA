package Instances;

import IO_Package.Enum_IO;
import IO_Package.IO_Manager;
import IO_Package.Reader;
import Instances.Agents.Agent;
import Instances.Maps.*;

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
    private final String SEPARATOR_MAP = "";


    /*  =Default Values=    */
    private final int defaultNumOfDimensions = 2;
    private final Integer defaultObstaclePercentage = -1;
    private final int defaultNumOfAgents = 10;
    private final int defaultNumOfBatches = 5;

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
        if( graphMap == null ){
            return;
        }


        // create agent properties
        int[] numOfAgentsFromProperties = (instanceProperties == null ? new int[]{this.defaultNumOfAgents} : instanceProperties.numOfAgents);


        // Blocking - add implementation of 'getAgents'

        for (int i = 0; i < numOfAgentsFromProperties.length; i++) {

            Agent[] agents = null;

            if (instanceName == null || graphMap == null || agents == null) {
                continue; // Invalid parameters
            }

            mapf_instance = new MAPF_Instance(instanceName, graphMap, agents);

            this.instanceStack.push(mapf_instance);

        }

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
        MapDimensions dimensionsFromFile = new MapDimensions(); // todo - works only with 2d as of now



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
                Integer obstaclePercentage = ( instanceProperties == null ? this.defaultObstaclePercentage : instanceProperties.getObstaclePercentage());
                // build map
                graphMap = I_InstanceBuilder.buildGraphMap(mapAsStrings, this.SEPARATOR_MAP, dimensionsFromFile, this.cellTypeHashMap, obstaclePercentage);

                break;
            }

            nextLine = reader.getNextLine();
        }

        reader.closeFile(); // No more data in the file

        return graphMap;

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
