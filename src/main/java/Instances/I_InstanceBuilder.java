package Instances;
import IO_Package.Reader;
import Instances.Maps.Enum_MapCellType;
import Instances.Maps.GraphMap;
import Instances.Maps.MapDimensions;
import Instances.Maps.MapFactory;
import java.util.HashMap;

/*  An Interface for parsing instance files   */
public interface I_InstanceBuilder {



    /*  Builds Instances and saves it in a data structure, ready for future use */
    void prepareInstances(String instanceName, InstanceManager.InstancePath instancePath, InstanceProperties instanceProperties);

    /*  Saves all paths in a data structure, ready for iterative use    */
    InstanceManager.InstancePath[] getInstancesPaths(String directoryPath);

    /*  Returns the next existing instance from the prepareInstances structure  */
    MAPF_Instance getNextExistingInstance();


    MapDimensions.MapOrientation getMapOrientation();




    /*  =Static methods=    */

    /*  ==Build maps==  */

    static String[] buildMapAsStringArray(Reader reader, MapDimensions mapDimensions){

        int axis_length = 0;// Indicates num of lines
        if(mapDimensions.mapOrientation.equals(MapDimensions.MapOrientation.Y_HORIZONTAL_X_VERTICAL)){
            axis_length = mapDimensions.xAxis_length;
        }else if( mapDimensions.mapOrientation.equals(MapDimensions.MapOrientation.X_HORIZONTAL_Y_VERTICAL)){
            axis_length = mapDimensions.yAxis_length;
        }

        String[] mapAsStringArray = new String[axis_length];
        for (int index = 0; index < axis_length; index++) {

            String nextLine = reader.getNextLine();
            if ( nextLine != null ){
                mapAsStringArray[index] = nextLine;
            }else {
                return null; // unexpected num of lines
            }
        }
        return mapAsStringArray;
    }


    /***
     * Builds a {@link GraphMap} from String array
     * @param mapAsStrings - Map from file, rows are yAxis
     * @param mapSeparator - Regex value to split map values. by default is "".
     * @param mapDimensions - A {@link MapDimensions}, must be valid.
     * @param cellTypeHashMap - HashMap for converting Character to {@link Enum_MapCellType}
     * @param obstacle - Value is a {@link Instances.InstanceProperties.ObstacleWrapper} indicates obstacle in the map.
     * @return A GraphMap
     */
    static GraphMap buildGraphMap(String[] mapAsStrings, String mapSeparator, MapDimensions mapDimensions, HashMap<Character,Enum_MapCellType> cellTypeHashMap, InstanceProperties.ObstacleWrapper obstacle) {

        switch ( mapDimensions.numOfDimensions ){
            case 2:

                Character[][] mapAsCharacters_2d = build2D_CharacterMap(mapAsStrings,mapDimensions,mapSeparator);
                Enum_MapCellType[][] mapAsCellType_2D = build_2D_cellTypeMap(mapAsCharacters_2d, cellTypeHashMap, mapDimensions.mapOrientation, obstacle);
                if( mapAsCellType_2D == null){
                    return null; // Error while building the map
                }
                return MapFactory.newSimple4Connected2D_GraphMap(mapAsCellType_2D);

            case 3:
                Character[][][] mapAsCharacters_3d = new Character[][][]{};
                Enum_MapCellType[][][] mapAsCellType_3D = build_3D_cellTypeMap(mapAsCharacters_3d, cellTypeHashMap, obstacle);
                return null; // niceToHave - change to newSimple 4Connected 3D_GraphMap if exists in MapFactory
        }


        return null; // If something went wrong ( should return in switch-case )
    }


    static Character[][] build2D_CharacterMap(String[] mapAsStrings, MapDimensions mapDimensions, String mapSeparator){

        int xAxis_length = mapDimensions.xAxis_length;
        int yAxis_length = mapDimensions.yAxis_length;


        Character[][] mapAsCharacters_2d = new Character[xAxis_length][yAxis_length];
        for (int yAxis_oldValue = 0; yAxis_oldValue < mapAsStrings.length; yAxis_oldValue++) {
            String[] yAxisLine = mapAsStrings[yAxis_oldValue].split(mapSeparator);

            for (int yAxis_newValue = 0; yAxis_newValue < yAxisLine.length ; yAxis_newValue++) {
                mapAsCharacters_2d[yAxis_oldValue][yAxis_newValue] = yAxisLine[yAxis_newValue].charAt(0);
            }

        }

        return mapAsCharacters_2d;
    }


    static Enum_MapCellType[][] build_2D_cellTypeMap(Character[][] mapAsCharacters , HashMap<Character,Enum_MapCellType> cellTypeHashMap, MapDimensions.MapOrientation mapOrientation, InstanceProperties.ObstacleWrapper obstacle) {
        // done - convert String[] to Enum_MapCellType[][] using this.cellTypeHashMap

        if(mapAsCharacters == null){
            return null;
        }

        int xAxis_length = mapAsCharacters.length;
        int yAxis_length = mapAsCharacters[0].length;

        // used to check obstacle percentage
        int numOfObstacles = 0;
        int numOfNonObstacles = 0;


        Enum_MapCellType[][] cellTypeMap = new Enum_MapCellType[xAxis_length][yAxis_length];

        for (int xIndex = 0; xIndex < xAxis_length; xIndex++) {
            for (int yIndex = 0; yIndex < yAxis_length; yIndex++) {

                // done - convert using this.cellTypeHashMap

                Character character = null;
                if( mapOrientation.equals(MapDimensions.MapOrientation.X_HORIZONTAL_Y_VERTICAL)){
                    character = mapAsCharacters[yIndex][xIndex];
                }else if( mapOrientation.equals(MapDimensions.MapOrientation.Y_HORIZONTAL_X_VERTICAL)){
                    character = mapAsCharacters[xIndex][yIndex];
                }

                Enum_MapCellType cellType = cellTypeHashMap.get(character);

                if ( cellType.equals(Enum_MapCellType.WALL)){
                    numOfObstacles++; // add one wall to counter
                }else{
                    numOfNonObstacles++; // add one to non obstacle counter
                }
                cellTypeMap[xIndex][yIndex] = cellType;
            }
        }

        // If obstacle rate is not -1,
        // check that it matches the value from properties
        // Formula: floor( obstaclesRate * BoardSize) = numOfObstacles
        int boardSize = (numOfNonObstacles + numOfObstacles);
        int computedNumOfObstacles = (int) Math.floor((obstacle.getAsRate() * boardSize));
        if ( obstacle.getAsRate() != -1 && computedNumOfObstacles != numOfObstacles ){
            // done - check with Dor that this is correct
            return null; // Invalid obstacle rate
        }

        // Set Obstacle for future use in MAPF_Instance
        int obstaclePercentage = (int) Math.ceil( ((double) numOfObstacles / (double) boardSize) * 100 );
        obstacle.setWithPercentage(obstaclePercentage);

        return cellTypeMap;
    }

    static Enum_MapCellType[][][] build_3D_cellTypeMap(Character[][][] mapAsCharacters, HashMap<Character,Enum_MapCellType> cellTypeHashMap, InstanceProperties.ObstacleWrapper obstacle) {
        // niceToHave - no need to implement for now
        return null;
    }




    /*  =Utils= */


    static boolean equalsAll(int[] arr1, int[] arr2){
        if( arr1 == null || arr2 == null){
            return false;
        }

        if( arr1.length != arr2.length ){
            return false;
        }


        for (int i = 0; i < arr1.length; i++) {
            if( arr1[i] != arr2[i] ){
                return false;
            }
        }

        return true;
    }



    static int equalsAny(int lookFor, int[] values){

        if( values == null){
            return -1;
        }

        for (int i = 0; i < values.length ; i++) {
            if( lookFor == values[i] ){
                return i;
            }
        }

        return -1;

    }

}


