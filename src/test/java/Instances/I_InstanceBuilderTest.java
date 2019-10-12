package Instances;

import Instances.Maps.MapDimensions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class I_InstanceBuilderTest {



    @Test
    public void build2D_CharacterMap_Instance_8_15_5(){

        /*  =Expected value=    */
        Character[][] expectedMap = new Character[][]   {
                                    {'.','.','.','.','.','.','.','.'},
                                    {'.','.','.','.','.','.','.','.'},
                                    {'.','.','.','@','.','.','.','.'},
                                    {'@','.','.','.','.','.','.','.'},
                                    {'.','.','.','.','.','.','.','.'},
                                    {'.','.','.','.','.','.','.','@'},
                                    {'@','.','@','.','.','.','.','@'},
                                    {'@','.','.','@','@','.','.','.'}
                                                        };



        String[] mapAsString = new String[] {
                                                "........",
                                                "........",
                                                "...@....",
                                                "@.......",
                                                "........",
                                                ".......@",
                                                "@.@....@",
                                                "@..@@..."

                                            };
        MapDimensions mapDimensions = new MapDimensions(8,8);

        Character[][] actualMap = I_InstanceBuilder.build2D_CharacterMap(mapAsString,mapDimensions,"");

        //  Check every Character in the array
        for (int i = 0; i < expectedMap.length; i++) {
            for (int j = 0; j < expectedMap[i].length; j++) {
                Assert.assertEquals(expectedMap[i][j],actualMap[i][j]);
            }
        }



    }
}
