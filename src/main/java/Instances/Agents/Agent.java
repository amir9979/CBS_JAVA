package Instances.Agents;

import Instances.Maps.I_Coordinate;

public class Agent {

    private final int iD;
    private final I_Coordinate source;
    private final I_Coordinate target;




    public Agent(int iD, I_Coordinate source, I_Coordinate target) {
        this.iD = iD;
        this.source = source;
        this.target = target;
    }
}
