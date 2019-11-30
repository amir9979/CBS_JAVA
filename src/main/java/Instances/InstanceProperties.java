package Instances;

import Instances.Maps.MapDimensions;

public class InstanceProperties {

    public final MapDimensions mapSize;
    public final ObstacleWrapper obstacles;
    public final int[] numOfAgents;


    public InstanceProperties() {
        this.mapSize = new MapDimensions();
        this.obstacles = new ObstacleWrapper();
        this.numOfAgents = new int[0];
    }


    public InstanceProperties(MapDimensions.Enum_mapOrientation enummapOrientation){
        this();
        this.mapSize.setMapOrientation(enummapOrientation);
    }



    /***
     * Properties constructor
     * @param mapSize - {@link MapDimensions} indicates the Axis lengths , zero for unknown
     * @param obstacles - For unknown obstacles enter (float)-1
     * @param numOfAgents - An array of different num of agents.
     */
    public InstanceProperties(MapDimensions mapSize, double obstacles, int[] numOfAgents) {
        this.mapSize = (mapSize == null ? new MapDimensions() :  mapSize);
        this.obstacles = (obstacles == -1 ? new ObstacleWrapper() : new ObstacleWrapper(obstacles));
        this.numOfAgents = (numOfAgents == null ? new int[0] : numOfAgents);
    }




    public class ObstacleWrapper {

        public static final double defaultObstacleRate = -1;
        private double obstacleRate = defaultObstacleRate;
        private double minRate = 0;
        private double maxRate = 1;

        public ObstacleWrapper(){}

        public ObstacleWrapper(double rate){
            this.setWithRate(rate);
        }

        public ObstacleWrapper(int percentage){
            this.setWithPercentage(percentage);
        }

        public void setWithRate(double rate){
            this.obstacleRate = rate;
        }

        public void setWithPercentage(int percentage){
            this.obstacleRate = (double) percentage / (double)100;
        }


        /***
         * Get Obstacle as a ratio, Like: 0.15
         * @return A double The obstacle rate in the map
         */
        public double getAsRate() {
            return this.obstacleRate;
        }

        /***
         * Get Obstacle as a percentage, Like: 15%
         * @return An int. The obstacle percentage in the map
         */
        public int getAsPercentage(){
            if (this.obstacleRate == -1) {
                return -1;
            }
            return (int)Math.round(this.obstacleRate * 100); // Returns 15
        }


        /*  Min Max getters, setters */

        public boolean isValidNumOfObstacle(int boardSize, int actualNumOfObstacle){

            // Formula: floor( obstaclesRate * BoardSize) = actual numOfObstacles

            int minNumOfObstacles = (int) Math.floor((this.minRate * boardSize));
            int maxNumOfObstacles = (int) Math.floor((this.maxRate * boardSize));

            if ( actualNumOfObstacle <= maxNumOfObstacles && actualNumOfObstacle >= minNumOfObstacles){
                if( this.obstacleRate == defaultObstacleRate || this.obstacleRate == obstacleRate){
                    return true;
                }
            }
            return false;
        }

        public void setMinRate(double minRate) {
            this.minRate = minRate;
        }

        public void setMaxRate(double maxRate) {
            this.maxRate = maxRate;
        }
    }


}
