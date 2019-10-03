package Solvers.AStar;

import Instances.Agents.Agent;
import Instances.Agents.OnlineAgent;
import Instances.MAPF_Instance;
import Instances.Maps.I_Map;
import Instances.Maps.I_MapCell;
import Metrics.InstanceReport;
import Solvers.*;
import Solvers.ConstraintsAndConflicts.ConstraintSet;

import java.util.*;

/**
 * An A*
 */
public class SingleAgentAStar_Solver implements I_Solver {
    //testme

    private static final int MAX_STATES_THRESHOLD = Integer.MAX_VALUE;

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
    private SingleAgentPlan existingPlan;
    private Solution existingSolution;
    private int expandedNodes;
    private int generatedNodes;

    @Override
    public Solution solve(MAPF_Instance instance, RunParameters parameters) {
        init(instance, parameters);
        Solution solution = solveAStar();
        writeMetricsToReport(solution);
        releaseMemory();
        return solution;
    }


    protected void init(MAPF_Instance instance, RunParameters runParameters){
        this.instanceReport = runParameters.instanceReport;
        this.constraints = runParameters.constraints == null ? new ConstraintSet()
                : new ConstraintSet(runParameters.constraints);
        this.agent = instance.agents.get(0);
        this.map = instance.map;
        if(runParameters.existingSolution != null && runParameters.existingSolution.getPlanFor(this.agent) != null){
            this.existingPlan = runParameters.existingSolution.getPlanFor(this.agent);
            this.existingSolution = runParameters.existingSolution;
        }
        else{
            //make a new solution and plan and initialize it with a default first move
            this.existingSolution = new Solution();
            this.existingPlan = new SingleAgentPlan(this.agent);
            this.existingPlan.addMove(getFirstMove(this.agent));
            this.existingSolution.putPlan(this.existingPlan);
        }

        this.abortedOnTimeout = false;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.openList = new PriorityQueue<AStarState>(Comparator.comparing(AStarState::getF));
        this.expandedNodes = 0;
        this.closed = new HashSet<AStarState>();
        this.generatedNodes = 0;
    }

    private Move getFirstMove(Agent agent) {
        //first time is the time of the agent, or 1.
        int moveTime = this.agent instanceof OnlineAgent ? ((OnlineAgent)this.agent).arrivalTime + 1 : 1;
        // first move is always to stay at current location (thus the minimal solution length is 1).
        Move firstMove = new Move(this.agent, moveTime, this.map.getMapCell(this.agent.source), this.map.getMapCell(this.agent.source));
        return firstMove;
    }

    protected Solution solveAStar() {
        this.openList.add(generateRootState());
        while (!openList.isEmpty() && openList.size() < MAX_STATES_THRESHOLD){
            //dequeu
            AStarState currentState = openList.remove();
            if(!closed.contains(currentState)){ // if you implement having no duplicates in open, then this is unnecessary
                if (isGoalState(currentState)){
                    currentState.backTracePlan(); // updates this.existingPlan which is contained in this.existingSolution
                    return this.existingSolution;
                }
                else{ //expand
                    closed.add(currentState);
                    this.expandedNodes++;
                    currentState.expand(); //doesn't generate closed or duplicate states
//                openList.addAll(currentState.generateChildStates());
                }

            }
        }
        return null; //no goal state found (problem unsolvable)
    }

    private boolean isGoalState(AStarState state) {
        return state.move.currLocation.getCoordinate().equals(agent.target);
    }

    private AStarState generateRootState() {
        return new AStarState(existingPlan.moveAt(existingPlan.getEndTime()),null, /*g=number of moves*/existingPlan.size());
    }

    protected void writeMetricsToReport(Solution solution) {
        if(instanceReport != null){
            this.instanceReport.putIntegerValue(InstanceReport.StandardFields.expandedNodes, this.expandedNodes);
            this.instanceReport.putIntegerValue(InstanceReport.StandardFields.generatedNodes, this.generatedNodes);
        }
    }

    protected void releaseMemory() {
        this.constraints = null;
        this.instanceReport = null;
        this.openList = null;
        this.closed = null;
        this.agent = null;
        this.map = null;
        this.existingSolution = null;
        this.existingPlan = null;
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
            this.h = getH();
        }

        // todo - extract heuristic to a separate (provided at runtime) class to support different heuristics.
        private float getH() {
            return move.currLocation.getCoordinate().distance(move.agent.target);
        }

        public void expand() {
            // can move to neighboring cells or stay put
            List<I_MapCell> neighborCellsIncludingCurrent = new ArrayList<>(this.move.currLocation.getNeighbors());
            neighborCellsIncludingCurrent.add(this.move.currLocation);

            for (I_MapCell destination: neighborCellsIncludingCurrent){
                Move possibleMove = new Move(this.move.agent, this.move.timeNow+1, this.move.currLocation, destination);
                if(constraints.accepts(possibleMove)){ //move not prohibited by existing constraint
                    AStarState child = new AStarState(possibleMove, this, this.g + 1);
                    generatedNodes++; //field in containing class

                    AStarState existingState;
                    if(closed.contains(child)){ // state visited already
                        // for non consistent heuristics - if the new one has a lower f, remove the old one from closed
                        // and add the new one to open
                    }
                    // todo - commented for now because too expensive. need to reduce runtime of getFromOpen(). if implemented, remove check of !closed.contains(currentState) in solveAStar()
//                    else if(null != (existingState = getFromOpen(child)) ){ //an equal state is waiting in open
//                        //keep the one with min G
//                        removeMaxGAndAddMinG(child, existingState); //O(LOG(n))
//                    }
                    else{ // it's a new state
                        openList.add(child);
                    }
                }
            }
        }

        private void removeMaxGAndAddMinG(AStarState newState, AStarState existingState) {
            boolean shouldSwap = newState.g < existingState.g;
            if(shouldSwap){
                openList.remove(existingState);
                openList.add(newState);
            }
        }


        /**
         * looks for a {@link AStarState} equal to state, in {@link #openList}. If found, returns the found state, else returns null.
         */
        private AStarState getFromOpen(AStarState state) {
            for(AStarState existingState : openList){
                if(state.equals(existingState)) return existingState;
            }
            return null;
        }

        public SingleAgentPlan backTracePlan() {
            List<Move> moves = new LinkedList<>();
            AStarState currentState = this;
            while (currentState != null){
                moves.add(currentState.move);
                currentState = currentState.prev;
            }
            Collections.reverse(moves); //reorder moves because they were reversed

            //if there was an existing plan before solving, then we started from its last move, and don't want to duplicate it.
            if(existingPlan.size() > 0) {moves.remove(0);}
            /*containing class.*/ existingPlan.addMoves(moves);
            return existingPlan;
        }

        /**
         * equality is determined by location (current), and time.
         * @param o
         * @return
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AStarState that = (AStarState) o;
            return move.currLocation.equals(that.move.currLocation) && move.timeNow == that.move.timeNow;
        }

        @Override
        public int hashCode() {
            return Objects.hash(move.currLocation.hashCode(), move.timeNow);
        }

        public float getF(){
            return g + h;
        }

    }
}
