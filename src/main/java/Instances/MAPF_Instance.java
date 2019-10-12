package Instances;

import Instances.Agents.Agent;
import Instances.Maps.I_Map;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Multi Agent Path Finding instance/problem.
 * An immutable datatype.
 */
public class MAPF_Instance {

    /**
     * A name representing the instance/problem. There is no guarantee about the contents of this field, including
     * uniqueness.
     */
    public final String name;
    /**
     * The map on which the MAPF instance/problem is solved. For example, a 2-dimensional, 4-connected grid.
     */
    public final I_Map map;
    /**
     * An unmodifiable list of agents. Since the {@link Agent}s should themselves be immutable, the contents of this
     * list cannot be changed in any way.
     */
    public final List<Agent> agents;


    public MAPF_Instance(String name, I_Map map, Agent[] agents) {
        if(name == null || map == null || agents == null){throw new IllegalArgumentException();}
        this.name = name;
        this.map = map;
        this.agents = List.of(agents); //unmodifiable list
    }

    MAPF_Instance(String name, I_Map map, List<Agent> agents) {
        this(name, map, agents.toArray(Agent[]::new));
    }

    /**
     * Creates a new {@link MAPF_Instance} from this {@link MAPF_Instance}, which only contains one of the {@link #agents}.
     * @param agent the agent to create a sub-instance for.
     * @return a new {@link MAPF_Instance} created from this {@link MAPF_Instance}, which only contains one of the {@link #agents}.
     * @throws IllegalArgumentException if the agent is not contained in {@link #agents}.
     */
    public MAPF_Instance getSubproblemFor(Agent agent){
        if(agent == null || !this.agents.contains(agent)){throw new IllegalArgumentException("Agent not present in instance.");}
        return new MAPF_Instance(name+"-agent" + agent.iD, map, new Agent[]{agent});
    }

}
