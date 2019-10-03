package Instances.Maps;

import Instances.Agents.Agent;

import java.util.List;

/**
 * Represents a private location where an {@link Instances.Agents.OnlineAgent} can wait before entering the {@link I_Map map}.
 * This is required, because without having such a location for each agent, the problem becomes incomplete, as agents
 * can appear right on top of other agents (in the same {@link I_MapCell cell}).
 * If the map is to be seen as a graph, then these garages are all vertices with an indegree of 0, and an outdegree of 1.
 */
public class PrivateGarage implements I_MapCell {

    /**
     * The only {@link I_MapCell cell} in the {@link I_Map map} that the {@link Instances.Agents.Agent agent} can enter
     * the {@link I_Map map} through.
     */
    public final I_MapCell mapEntryPoint;
    private final I_Coordinate coordinate;
    private final Agent owner;

    public PrivateGarage(Agent owner, I_MapCell mapEntryPoint) {
        if(mapEntryPoint == null) {throw new IllegalArgumentException("PrivateGarage: mapEntryPoint can't be null.");}
        if(owner == null) {throw new IllegalArgumentException("PrivateGarage: owner can't be null.");}
        this.owner = owner;
        this.mapEntryPoint = mapEntryPoint;
        this.coordinate = new GarageCoordinate();
    }

    @Override
    public Enum_MapCellType getType() {
        return Enum_MapCellType.PRIVATE_GARAGE;
    }

    @Override
    public List<I_MapCell> getNeighbors() {
        return List.of(mapEntryPoint);
    }

    /**
     * The agent waits outside of the graph, yet the only coordinate that can represent the garage would be that of its
     * {@link #mapEntryPoint}.
     * @return the coordinate of the garage's {@link #mapEntryPoint}.
     */
    @Override
    public I_Coordinate getCoordinate() {
        return this.coordinate;
    }

    @Override
    public boolean isNeighbor(I_MapCell other) {
        return mapEntryPoint.equals(other);
    }

    // does not override equals() and hashCode(). since every agent has a unique and private garage, address equality is good.

    private class GarageCoordinate implements I_Coordinate{

        @Override
        public float distance(I_Coordinate other) {
            return other == PrivateGarage.this.mapEntryPoint.getCoordinate() ? 0 : -1;
        }

        @Override
        public String toString() {
//            return "(agent" + PrivateGarage.this.owner.iD + "'s garage)";
            return "(garage" + PrivateGarage.this.owner.iD + ")";
        }

        // address equality is good enough, since these are supposed to be unique

    }
}
