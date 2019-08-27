package Instances.Agents;

import Instances.Maps.Coordinate_2D;

public class Agent {
    private int iD;
    private final Coordinate_2D source;
    private final Coordinate_2D target;

    public Agent(int iD, Coordinate_2D source, Coordinate_2D target) {
        this.iD = iD;
        this.source = source;
        this.target = target;
    }
}
