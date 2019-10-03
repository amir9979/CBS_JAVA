package Instances;

import IO_Package.Reader;
import Instances.Maps.Enum_MapCellType;
import Instances.Maps.GraphMap;
import Instances.Maps.MapFactory;

import java.util.HashMap;

public interface I_InstanceBuilder {

    MAPF_Instance getInstance(String instanceName, InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties);

    InstanceManager.InstancePath[] getInstancesPaths(String directoryPath);


    static String[] buildMapAsStringArray(Reader reader, int[] dimensions){

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


    static GraphMap buildGraphMap(String[] mapAsStrings, int numOfDimensions,HashMap<Character,Enum_MapCellType> cellTypeHashMap, Integer obstaclePercentage) {

        switch ( numOfDimensions ){
            case 2:
                Enum_MapCellType[][] mapAsCellType_2D = build_2D_cellTypeMap(mapAsStrings, cellTypeHashMap, obstaclePercentage);
                return MapFactory.newSimple4Connected2D_GraphMap(mapAsCellType_2D);

            case 3:
                Enum_MapCellType[][][] mapAsCellType_3D = build_3D_cellTypeMap(mapAsStrings, cellTypeHashMap,obstaclePercentage);
                return null; // niceToHave - change to newSimple 4Connected 3D_GraphMap if exists in MapFactory
        }


        return null; // If something went wrong ( should return in switch-case )
    }


    static Enum_MapCellType[][] build_2D_cellTypeMap(String[] mapAsStrings , HashMap<Character,Enum_MapCellType> cellTypeHashMap, Integer obstaclePercentage) {
        // done - convert String[] to Enum_MapCellType[][] using this.cellTypeHashMap

        int xAxis_length = mapAsStrings.length;
        int yAxis_length = mapAsStrings[0].length();

        // used to check obstacle percentage
        int numOfObstacles = 0;
        int numOfNonObstacles = 0;


        Enum_MapCellType[][] cellTypeMap = new Enum_MapCellType[xAxis_length][yAxis_length];

        for (int xIndex = 0; xIndex < xAxis_length; xIndex++) {
            for (int yIndex = 0; yIndex < yAxis_length; yIndex++) {

                // done - convert using this.cellTypeHashMap
                Enum_MapCellType cellType = cellTypeHashMap.get(mapAsStrings[xIndex].charAt(yIndex));

                if ( cellType.equals(Enum_MapCellType.WALL)){
                    numOfObstacles++; // add one wall to counter
                }else{
                    numOfNonObstacles++; // add one to counter
                }
                cellTypeMap[xIndex][yIndex] = cellType;
            }
        }

        // If obstacle percentage is not null,
        // check that it matches the value from properties
        // Formulation: floor( obstaclesPercentage * BoardSize) = numOfObstacles
        if ( obstaclePercentage != null && numOfObstacles != (obstaclePercentage *(numOfNonObstacles + numOfObstacles))){
            // done - check with Dor that this is correct
            return null; // Invalid obstacle rate
        }

        return cellTypeMap;
    }

    static Enum_MapCellType[][][] build_3D_cellTypeMap(String[] mapAsStrings, HashMap<Character,Enum_MapCellType> cellTypeHashMap, Integer obstaclePercentage) {
        // niceToHave - no need to implement for now
        return null;
    }


}


