package Instances;

import IO_Package.Enum_IO;
import IO_Package.IO_Manager;
import IO_Package.Reader;
import Instances.Agents.Agent;
import Instances.I_InstanceBuilder;
import Instances.InstanceManager;
import Instances.MAPF_Instance;
import Instances.Maps.Coordinate_2D;
import Instances.Maps.Coordinate_3D;
import Instances.Maps.GraphMap;

public class InstanceBuilder_BGU_New implements I_InstanceBuilder {


    private final String INDICATOR_AGENTS = "Agents:";
    private final String SEPARATOR_AGENTS = ",";
    private final String INDICATOR_MAP = "Grid:";
    private final String SEPARATOR_DIMENSIONS = ",";


    @Override
    public MAPF_Instance getInstance(String instanceName, InstanceManager.InstancePath instancePath) {


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
        int[] dimensions = null;
        GraphMap graphMap = null;
        Agent[] agents = null;
        int instance_id = Integer.parseInt(nextLine); // get instance id


        /*  =Get data from reader=  */

        nextLine = reader.getNextLine(); // Second line

        while ( nextLine != null ){

            switch (nextLine){

                case INDICATOR_MAP:
                    String dimensionsAsString = reader.getNextLine();
                    dimensions = getDimensions(dimensionsAsString, SEPARATOR_DIMENSIONS);
                    if (dimensions == null){
                        reader.closeFile();
                        return null; // unexpected dimensions line
                    }
                    int numOfLines = dimensions[0];
                    String[] mapAsStrings = this.buildMap(reader, numOfLines);

                    graphMap = new GraphMap(mapAsStrings);

                    break;

                case INDICATOR_AGENTS:

                    if ( dimensions == null ){
                        reader.closeFile();
                        return null; // Missing dimensions
                    }
                    agents = buildAgents(reader, dimensions.length, SEPARATOR_AGENTS);

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

    private Agent buildSingleAgent(int dimensions, String line, String separator){
        String[] agentLine = line.split(separator);

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
            Coordinate_2D source = new Coordinate_2D(   Integer.valueOf(agentLine[3]),
                                                        Integer.valueOf(agentLine[4]));
            Coordinate_2D target = new Coordinate_2D(   Integer.valueOf(agentLine[1]),
                                                        Integer.valueOf(agentLine[2]));
            return new Agent(agentID, source, target);
        }


        if(dimensions == 3) {
            Coordinate_3D source = new Coordinate_3D(   Integer.valueOf(agentLine[4]),
                                                        Integer.valueOf(agentLine[5]),
                                                        Integer.valueOf(agentLine[6]));
            Coordinate_3D target = new Coordinate_3D(   Integer.valueOf(agentLine[1]),
                                                        Integer.valueOf(agentLine[2]),
                                                        Integer.valueOf(agentLine[3]));
            return new Agent(agentID, source, target);
        }

        return null; // Bad dimensions input
    }


    private Agent[] buildAgents(Reader reader,int dimensions, String agents_separator) {

        String nextLine = reader.getNextLine(); // expected num of agents
        if( nextLine == null || ! IO_Manager.isPositiveInt(nextLine)) {
            return null; // num of agents should be a positive int
        }

        int numOfAgents = Integer.parseInt(nextLine);
        Agent[] agents = new Agent[numOfAgents];

        for (int i = 0; i < agents.length; i++) {
            nextLine = reader.getNextLine();

            Agent agentToAdd = buildSingleAgent(dimensions, nextLine, agents_separator);
            if ( agentToAdd == null ){
                return null; // Bad agent line
            }
            agents[i] = agentToAdd;
        }

        return agents;
    }



    /***  =Build Map and Dimensions=  ***/

    private int[] getDimensions(String dimensionsAsString, String separator) {

        int[] dimensions = null;
        if(dimensionsAsString.contains(separator)) {
            String[] spllitedLine = dimensionsAsString.split(SEPARATOR_DIMENSIONS);
            dimensions = new int[spllitedLine.length];

            for (int i = 0; i < dimensions.length; i++) {
                if ( IO_Manager.isPositiveInt( spllitedLine[i] )){
                    dimensions[i] = Integer.parseInt(spllitedLine[i]);
                }else{
                    return null; // dimensions should be positive integers
                }
            }

        }else {
            return null; // Missing expected separator
        }

        return dimensions;
    }




    private String[] buildMap(Reader reader, int numOfLines){

        String[] result = new String[numOfLines];
        for (int i = 0; i < numOfLines; i++) {

            String nextLine = reader.getNextLine();
            if ( nextLine != null ){
                result[i] = nextLine;
            }else {
                return null; // unexpected num of lines
            }
        }
        return result;
    }


    @Override
    public InstanceManager.InstancePath[] getInstancesPaths(String directoryPath) {
        return new InstanceManager.InstancePath[0];
    }
}
