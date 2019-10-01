package Solvers.AStar;

import Instances.Agents.Agent;
import Instances.Agents.OnlineAgent;
import Instances.MAPF_Instance;
import Instances.Maps.I_Map;
import Instances.Maps.I_MapCell;
import Metrics.InstanceReport;
import Solvers.*;
import Solvers.ConstraintsAndConflicts.Constraint;
import Solvers.ConstraintsAndConflicts.ConstraintSet;

import java.util.*;

public class SingleAgentAStar_Solver implements I_Solver {

    private static final int MAX_STATES_THRESHOLD = 2000000;

    private long maximumRuntime;
    private ConstraintSet constraints;
    private InstanceReport instanceReport;
    private long startTime;
    private long endTime;
    private boolean abortedOnTimeout;
    private Queue<AStarState> openList;
    private Set<AStarState> closed;
    private Agent agent;
    private I_Map map;

    @Override
    public Solution solve(MAPF_Instance instance, RunParameters parameters) {
        init(instance, parameters);
        Solution solution = solveAStar();
        writeMetricsToReport(solution);
        releaseMemory();
        return solution;
    }


    protected void init(MAPF_Instance instance, RunParameters runParameters){
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.abortedOnTimeout = false;
        this.constraints = runParameters.constraints == null ? new ConstraintSet()
                : new ConstraintSet(runParameters.constraints);
        this.agent = instance.agents.get(0);
        this.map = instance.map;

        this.openList = new PriorityQueue<AStarState>(Comparator.comparing(AStarState::getF));
        this.closed = new HashSet<AStarState>();
    }

    protected Solution solveAStar() {
        this.openList.add(generateRootState());
        while (!openList.isEmpty() && openList.size() < MAX_STATES_THRESHOLD){
            //dequeu
            AStarState currentState = openList.remove();
            if (isGoalState(currentState)){
                Map<Agent, SingleAgentPlan> plan =  new HashMap<>();
                plan.put(this.agent, currentState.backTracePlan());
                return new Solution(plan);
            }
            else{
                closed.add(currentState);
                openList.addAll(currentState.generateChildStates());
            }
        }
        return null; //no goal state found (problem unsolvable)
    }

    private boolean isGoalState(AStarState state) {
        return state.move.currLocation.getCoordinate().equals(agent.target);
    }

    private AStarState generateRootState() {
        //first time is the time of the agent, or 1.
        int moveTime = this.agent instanceof OnlineAgent ? ((OnlineAgent)this.agent).arrivalTime + 1 : 1;
        // first move is always to stay at current location (thus the minimal solution length is 1).
        Move firstMove = new Move(this.agent, moveTime,this.map.getMapCell(this.agent.source), this.map.getMapCell(this.agent.source));

        return new AStarState(firstMove,null, 1);
    }

    protected void writeMetricsToReport(Solution solution) {
        //imp - write some metrics about the run
    }

    protected void releaseMemory() {
        this.constraints = null;
        this.instanceReport = null;
        this.openList = null;
        this.closed = null;
        this.agent = null;
        this.map = null;
    }

    private class AStarState{
        public Move move;
        private AStarState prev;
        private int g;
        private float h;

        public AStarState(Move move, AStarState prevState, int g) {
            this.move = move;
            this.prev = prevState;
            this.g = g;
            this.h = move.currLocation.getCoordinate().euclideanDistance(move.agent.target);
        }

        public Collection<? extends AStarState> generateChildStates() {
            // can move to neighboring cells or stay put
            List<I_MapCell> neighborCellsIncludingCurrent = new ArrayList<>(move.currLocation.getNeighbors());
            neighborCellsIncludingCurrent.add(this.move.currLocation);

            List<AStarState> children = new ArrayList<>(neighborCellsIncludingCurrent.size());

            for (I_MapCell destination: neighborCellsIncludingCurrent){
                Move possibleMove = new Move(this.move.agent, this.move.timeNow+1, this.move.currLocation, destination);
                if(constraints.accepts(possibleMove)){ //move not prohibited by existing constraint
                    AStarState child = new AStarState(possibleMove, this, this.g + 1);
                    if(!closed.contains(child)){ // state not visited already
                        children.add(child);
                    }

                }
            }
            return children;
        }

        public SingleAgentPlan backTracePlan() {
            List<Move> moves = new LinkedList<>();
            AStarState currentState = this;
            while (currentState != null){
                moves.add(currentState.move);
                currentState = currentState.prev;
            }
            Collections.reverse(moves); //reorder moves because they were reversed
            return new SingleAgentPlan(this.move.agent, moves);
        }

        // todo - currently doing equals by move.cuurLocation. is this good?

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AStarState that = (AStarState) o;
            return move.currLocation.equals(that.move.currLocation);
        }

        @Override
        public int hashCode() {
            return move.currLocation.hashCode();
        }

        public float getF(){
            return g + h;
        }

    }
}
