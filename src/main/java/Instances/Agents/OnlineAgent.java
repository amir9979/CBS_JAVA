package Instances.Agents;

import Instances.Maps.I_Coordinate;

public class OnlineAgent extends Agent {

    public final int arrivalTime;

    public OnlineAgent(int iD, I_Coordinate source, I_Coordinate target, int arrivalTime) {
        super(iD, source, target);
        this.arrivalTime = arrivalTime;
    }
}
