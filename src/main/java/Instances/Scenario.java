package Instances;

import Instances.Maps.Coordinate_2D;
import jdk.internal.net.http.common.Pair;

public class Scenario {
    private final String name;
    private Pair<Coordinate_2D,Coordinate_2D> startAndGoalPairs;

    public Scenario(String name, Pair<Coordinate_2D, Coordinate_2D> startAndGoalPairs) {
        this.name = name;
        this.startAndGoalPairs = startAndGoalPairs;
    }
}
