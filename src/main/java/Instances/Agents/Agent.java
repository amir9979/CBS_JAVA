package Instances.Agents;

import Instances.Maps.I_Coordinate;

import java.util.Objects;

public class Agent {

    private final int iD;
    private final I_Coordinate source;
    private final I_Coordinate target;

    //einat commit


    public Agent(int iD, I_Coordinate source, I_Coordinate target) {
        this.iD = iD;
        this.source = source;
        this.target = target;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agent)) return false;
        Agent agent = (Agent) o;
        return iD == agent.iD &&
                Objects.equals(source, agent.source) &&
                Objects.equals(target, agent.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iD, source, target);
    }
}
