//package Instances;
//
//import IO_Package.Enum_IO;
//import IO_Package.IO_Manager;
//import IO_Package.Reader;
//import Instances.Agents.Agent;
//import Instances.Maps.Coordinate_2D;
//import Instances.Maps.Coordinate_3D;
//import Instances.Maps.GraphMap;
//
//
//
//public class InstanceBuilder_BGU implements I_InstanceBuilder {
//
//
//    @Override
//    public MAPF_Instance getInstance(String instanceName, InstanceManager.InstancePath instancePath) {
//
//        MAPF_Instance mapf_instance;
//        String fullName=instancePath.path;
//        String nameOfInstance;
//        // todo - check separator
//        String separator = IO_Manager.pathSeparator; // this is a single backslash '\'
//        if(fullName.contains(separator)) {
//
//            String[] fullNameSplitted = fullName.split(separator);
//            nameOfInstance = fullNameSplitted[fullNameSplitted.length - 1];
//        }
//        else {
//            return null;   // if the path is invalid
//        }
//
//        Reader reader=new Reader();
//        Enum_IO enum_io =reader.openFile(fullName);
//        if( !enum_io.equals(Enum_IO.OPENED) ){
//            return null; // couldn't open the file
//        }
//
//        int index=1;
//        String line="";
//        GraphMap graphMap;
//        String numOfRows="0"; //just until it will initial accordingly
//        String[] mapByStrings=new String[0]; //just until it will initial accordingly
//        Agent [] agents=new Agent[0]; //just until it will initial accordingly
//        int dimensions=0;
//
//
//        /* Example: First 3 lines in bgu instance ( before map )
//         0
//        Grid:
//        16,16
//        */
//        while (index<4){
//            line=reader.getNextLine();
//            if(line==null) {
//                reader.closeFile();
//                return null;
//            }
//            if(index==3) {
//                if(line.contains(",")) {
//                    String[] splitedLine=line.split(",");
//                    dimensions=splitedLine.length;
//                    numOfRows = splitedLine[0];
//                    if(IO_Manager.isPositiveInt(numOfRows)){
//                        mapByStrings = new String[Integer.valueOf(numOfRows)];
//                    }
//                }
//            }
//            index++;
//        }
//        int row=0;
//        while (row<Integer.valueOf(numOfRows)){
//            line=reader.getNextLine();
//            if(line==null) {
//                reader.closeFile();
//                return null;
//            }
//            mapByStrings[row]=line;
//            row++;
//        }
//        graphMap = new GraphMap(mapByStrings);
//
//        line=reader.getNextLine();
//        if(line!=null && line.equals("Agents:")){
//            line=reader.getNextLine();
//            if(line!=null && IO_Manager.isPositiveInt(line)){
//                int numOfAgents=Integer.valueOf(line);
//                int indexOfAgent=0;
//                agents=new Agent[numOfAgents];
//                while (indexOfAgent<numOfAgents){
//                    line=reader.getNextLine();
//                    if(line!=null && line.contains(",")){
//                        agents[indexOfAgent]=buildAgent(dimensions,line);
//                    }
//                    else {
//                        reader.closeFile();
//                        return null; //expected the the line will contains comma
//                    }
//                    indexOfAgent++;
//                }
//                mapf_instance=new MAPF_Instance(nameOfInstance,graphMap,agents);
//                return mapf_instance;
//            }
//        }
//        else {
//            reader.closeFile();
//            return null; //expected that the line will be agents"
//        }
//
//        return null;
//    }
//
//    private Agent buildAgent(int dimensions, String line){
//        String[] coordinates=line.split(",");
//        if(dimensions==2) {
//            if (coordinates.length == 5 && IO_Manager.isPositiveInt(coordinates[0]) && IO_Manager.isPositiveInt(coordinates[1]) && IO_Manager.isPositiveInt(coordinates[2]) && IO_Manager.isPositiveInt(coordinates[3]) && IO_Manager.isPositiveInt(coordinates[4])) {
//                Coordinate_2D source = new Coordinate_2D(Integer.valueOf(coordinates[3]), Integer.valueOf(coordinates[4]));
//                Coordinate_2D target = new Coordinate_2D(Integer.valueOf(coordinates[1]), Integer.valueOf(coordinates[2]));
//                return new Agent(Integer.valueOf(coordinates[0]),source,target);
//            }
//            else {
//                return null; //invalid parameters of the line
//            }
//        }
//        if(dimensions==3) {
//            if (coordinates.length == 7 && IO_Manager.isPositiveInt(coordinates[0]) && IO_Manager.isPositiveInt(coordinates[1]) && IO_Manager.isPositiveInt(coordinates[2]) && IO_Manager.isPositiveInt(coordinates[3]) && IO_Manager.isPositiveInt(coordinates[4]) && IO_Manager.isPositiveInt(coordinates[5])&& IO_Manager.isPositiveInt(coordinates[6])) {
//                Coordinate_3D source = new Coordinate_3D(Integer.valueOf(coordinates[4]), Integer.valueOf(coordinates[5]), Integer.valueOf(coordinates[6]));
//                Coordinate_3D target = new Coordinate_3D(Integer.valueOf(coordinates[1]), Integer.valueOf(coordinates[2]), Integer.valueOf(coordinates[3]));
//                return new Agent(Integer.valueOf(coordinates[0]),source,target);
//            }
//            else {
//                return null; //invalid parameters of the line
//            }
//        }
//        else {
//            return null; //todo add more possible dimensions
//        }
//    }
//
//    @Override
//    public InstanceManager.InstancePath[] getInstancesPaths(String directoryPath){
//
//        String[] paths = IO_Manager.getFilesFromDirectory(directoryPath);
//
//        InstanceManager.InstancePath[] instancePaths = new InstanceManager.InstancePath[paths.length];
//
//
//        for (int i = 0; i < paths.length ; i++) {
//            instancePaths[i] = new InstanceManager.InstancePath(paths[i]);
//        }
//
//
//        return instancePaths;
//    }
//
//
//
//
//
//
//}
