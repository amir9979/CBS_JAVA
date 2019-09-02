package Instances;

import Instances.Agents.Agent;
import Instances.Maps.I_Map;

public class MAPF_Instance {

    public final String name;
    public final I_Map map;
    private final Agent[] agents;


    public MAPF_Instance(String name, I_Map map, Agent[] agents) {
        this.name = name;
        this.map = map;
        this.agents = agents;
    }


    public Agent[] getAgents() {

        // fixme - deep copy
        return agents;
    }


}
