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


    MAPF_Instance(String name, I_Map map, Agent[] agents) {
        this.name = name;
        this.map = map;
        this.agents = List.of(agents); //unmodifiable list
    }

    MAPF_Instance(String name, I_Map map, List<Agent> agents) {
        this.name = name;
        this.map = map;
        this.agents = List.copyOf(agents); //unmodifiable list
    }

}
