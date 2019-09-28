package Instances.Agents;

import Instances.Maps.I_Coordinate;

public class OnlineAgent extends Agent {

    /*
     todo - will have to deal with the fact tha when re-planning an agent at a time different from its start time, start location
     will have to also change:
     option 1 - wrap #super.source with a getter, and override it. add presentLocation field with a setter.
     option 2 - make #super.source protected and not final, and change it as needed. add a originalSource field.
     */


    public static final int DEFAULT_ARRIVAL_TIME = 0;
    public final int arrivalTime;

    public OnlineAgent(int iD, I_Coordinate source, I_Coordinate target, int arrivalTime) {
        super(iD, source, target);
        this.arrivalTime = arrivalTime;
    }

    public OnlineAgent(Agent offlineAgent, int arrivalTime){
        this(offlineAgent.iD, offlineAgent.source, offlineAgent.target, arrivalTime);
    }

    public OnlineAgent(Agent offlineAgent){
        this(offlineAgent, DEFAULT_ARRIVAL_TIME);
    }

    public int getArrivalTime() {
        return arrivalTime;
    }
}
