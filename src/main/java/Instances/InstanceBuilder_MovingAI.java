package Instances;

import IO_Package.Enum_IO;
import IO_Package.IO_Manager;
import IO_Package.Reader;
import Instances.Agents.Agent;
import Instances.Maps.Enum_MapCellType;
import Instances.Maps.GraphMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class InstanceBuilder_MovingAI implements I_InstanceBuilder {


    private final String INDICATOR_MAP = "map";
    private final String FILE_TYPE_MAP = ".map";
    private final String FILE_TYPE_SCENARIO = ".scen";
    private final String INDICATOR_HEIGHT = "height";
    private final String INDICATOR_WIDTH = "width";

    private final String SEPARATOR_DIMENSIONS = " ";


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
    public MAPF_Instance getInstance(String instanceName, InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties) {

        if (!( instancePath instanceof InstanceManager.Moving_AI_Path)){
            return null;
        }

        InstanceManager.Moving_AI_Path moving_ai_path = (InstanceManager.Moving_AI_Path) instancePath;



        MAPF_Instance mapf_instance = null;
        GraphMap graphMap = getMap(moving_ai_path, instanceProperties);
        Agent[] agents = getAgents(moving_ai_path, instanceProperties);




        if ( instanceName == null || graphMap == null || agents == null){
            return null; // Invalid parameters
        }

        mapf_instance = new MAPF_Instance(instanceName, graphMap, agents);
        return mapf_instance;


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
                Integer obstaclePercentage = ( instanceProperties == null ? null : instanceProperties.getObstaclePercentage());
                // build map
                graphMap = I_InstanceBuilder.buildGraphMap(mapAsStrings, dimensions.length, cellTypeHashMap, obstaclePercentage);

                break;
            }

            nextLine = reader.getNextLine();
        }

        reader.closeFile(); // No more data in the file

        return graphMap;

    }


    private Agent[] getAgents( InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties ){

        // imp

        Reader reader = new Reader();
        String scenarioPath = ((InstanceManager.Moving_AI_Path)instancePath).scenarioPath;
        Enum_IO enum_io = reader.openFile( scenarioPath );
        if( !enum_io.equals(Enum_IO.OPENED) ){
            return null; // couldn't open the file
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

        InstanceManager.InstancePath[] instancePaths = instanceBuilder_movingAI.getInstancesPaths("D:\\CBS_JAVA\\src\\test\\resources\\Instances\\MovingAI");
        InstanceManager.Moving_AI_Path moving_ai_path = (InstanceManager.Moving_AI_Path) instancePaths[0];

        instanceBuilder_movingAI.getInstance(",", new InstanceManager.Moving_AI_Path(moving_ai_path.path,moving_ai_path.scenarioPath),null);
    }


}
