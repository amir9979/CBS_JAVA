package Instances;

import IO_Package.IO_Manager;
import Instances.Agents.Agent;
import Instances.Agents.OnlineAgent;
import Instances.Maps.Coordinate_2D;
import Instances.Maps.Coordinate_3D;

public class OnlineInstanceBuilder_BGU extends InstanceBuilder_BGU {

    // override agent building to add arrival times


    @Override
    protected Agent buildSingleAgent(int dimensions, String line){

        String[] agentLine = line.split(super.SEPARATOR_AGENTS);

        if( agentLine == null || agentLine.length < 1){
            return null; // invalid agent line
        }

        for (int i = 0; i < agentLine.length; i++) {
            if( ! IO_Manager.isPositiveInt(agentLine[i])){
                return null; // dimensions should be a positive int
            }
        }

        int agentID = Integer.parseInt(agentLine[0]);

        if(dimensions == 2) {
            /*      source values    */
            int source_xValue = Integer.valueOf(agentLine[3]);
            int source_yValue = Integer.valueOf(agentLine[4]);
            Coordinate_2D source = new Coordinate_2D(source_xValue, source_yValue);
            /*      Target values    */
            int target_xValue = Integer.valueOf(agentLine[1]);
            int target_yValue = Integer.valueOf(agentLine[2]);
            Coordinate_2D target = new Coordinate_2D(target_xValue, target_yValue);
            // add arrival time for online agents
            int arrivalTime = (agentLine.length >= 6) ? Integer.valueOf(agentLine[5]) : 0;

            return new OnlineAgent(agentID, source, target, arrivalTime);
        }


        if(dimensions == 3) {
            /*      source values    */
            int source_xValue = Integer.valueOf(agentLine[4]);
            int source_yValue = Integer.valueOf(agentLine[5]);
            int source_zValue = Integer.valueOf(agentLine[6]);
            Coordinate_3D source = new Coordinate_3D(source_xValue, source_yValue, source_zValue);
            /*      Target values    */
            int target_xValue = Integer.valueOf(agentLine[1]);
            int target_yValue = Integer.valueOf(agentLine[2]);
            int target_zValue = Integer.valueOf(agentLine[3]);
            Coordinate_3D target = new Coordinate_3D(target_xValue, target_yValue, target_zValue);
            // add arrival time for online agents
            int arrivalTime =  (agentLine.length >= 8) ? Integer.valueOf(agentLine[7]) : 0;

            return new OnlineAgent(agentID, source, target, arrivalTime);
        }

        return null; // Bad dimensions input
    }

}
