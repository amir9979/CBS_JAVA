package Solvers.AStar;

import Instances.Agents.Agent;
import Instances.Agents.OnlineAgent;
import Instances.MAPF_Instance;
import Instances.Maps.I_Coordinate;
import Instances.Maps.I_Map;
import Instances.Maps.I_MapCell;
import Metrics.InstanceReport;
import Solvers.*;
import Solvers.ConstraintsAndConflicts.ConstraintSet;

import java.util.*;

/**
 * An A* solver that only solves single agent problems. It assumes the first {@link Agent} from {@link MAPF_Instance instances}
 * that it is given is the agent to solve for.
 * By default, it uses {@link Instances.Maps.I_Coordinate#distance(I_Coordinate)} as a heuristic.
 */
public class SingleAgentAStar_Solver implements I_Solver {

    /**
     * Since A* should solve even very large single agent problems very quickly, set default timeout to 3 seconds.
     * A timeout would therefore very likely mean the problem is unsolvable.
     * Without a timeout, unsolvable problems would not resolve, since new states with higher time ({@link Move#timeNow},
     * would continue to be generated ad infinitum. This would eventually result in a heap overflow.
     */
    protected static final long DEFAULT_TIMEOUT = 3 * 1000;

    private long maximumRuntime;
    private ConstraintSet constraints;
    private InstanceReport instanceReport;
    private AStarHeuristic heuristicFunction;
    private long startTime;
    private long endTime;
    private boolean abortedOnTimeout;
    private Queue<AStarState> openList;
    private Map<AStarState, AStarState> openListStates;
    private Set<AStarState> closed;
    private Agent agent;
    private I_Map map;
    private SingleAgentPlan existingPlan;
    private Solution existingSolution;
    private int expandedNodes;
    private int generatedNodes;

    /*  = interface implementation =  */

    @Override
    public Solution solve(MAPF_Instance instance, RunParameters parameters) {
        init(instance, parameters);
        Solution solution = solveAStar();
        writeMetricsToReport(solution);
        releaseMemory();
        return solution;
    }

    /*  = set up =  */

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
        if(runParameters instanceof  RunParameters_SAAStar){
            RunParameters_SAAStar parameters = ((RunParameters_SAAStar) runParameters);
            this.heuristicFunction = parameters.heristicFunction;
        }
        else{
            this.heuristicFunction = new defaultHeuristic();
        }

        this.maximumRuntime = (runParameters.timeout >= 0) ? runParameters.timeout : DEFAULT_TIMEOUT;
        this.abortedOnTimeout = false;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.openList = new PriorityQueue<>(Comparator.comparing(AStarState::getF));
        this.openListStates = new HashMap<>();
        this.expandedNodes = 0;
        this.closed = new HashSet<>();
        this.generatedNodes = 0;
    }

    private Move getFirstMove(Agent agent) {
        //first time is the time of the agent, or 1.
        int moveTime = this.agent instanceof OnlineAgent ? ((OnlineAgent)this.agent).arrivalTime + 1 : 1;
        // first move is always to stay at current location (thus the minimal solution length is 1).
        return new Move(this.agent, moveTime, this.map.getMapCell(this.agent.source), this.map.getMapCell(this.agent.source));
    }

    /*  = A* algorithm =  */

    protected Solution solveAStar() {
        addToOpen(generateRootState());
        while (!openList.isEmpty() ){
            if(checkTimeout()) {return null;}
            //dequeu
            AStarState currentState = dequeueFromOpen();

            if (isGoalState(currentState)){
                currentState.backTracePlan(); // updates this.existingPlan which is contained in this.existingSolution
                return this.existingSolution;
            }
            else{ //expand
                closed.add(currentState);
                this.expandedNodes++;
                currentState.expand(); //doesn't generate closed or duplicate states
            }
        }
        return null; //no goal state found (problem unsolvable)
    }

    /*  = auxiliary methods =  */

    private boolean addToOpen(AStarState child) {
        openListStates.put(child, child);
        return openList.add(child);
    }

    private AStarState dequeueFromOpen() {
        AStarState state = openList.remove();
        openListStates.remove(state);
        return state;
    }

    protected boolean checkTimeout() {
        if(System.currentTimeMillis()-startTime > maximumRuntime){
            this.abortedOnTimeout = true;
            return true;
        }
        return false;
    }

    private boolean isGoalState(AStarState state) {
        return state.move.currLocation.getCoordinate().equals(agent.target);
    }

    private AStarState generateRootState() {
        return new AStarState(existingPlan.moveAt(existingPlan.getEndTime()),null, /*g=number of moves*/existingPlan.size());
    }

    /*  = wind down =  */

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

    /*  = inner classes =  */

    public class AStarState{
        private Move move;
        private AStarState prev;
        private int g;
        private float h;

        public AStarState(Move move, AStarState prevState, int g) {
            this.move = move;
            this.prev = prevState;
            this.g = g;

            // must call this last, since it needs the other fields to be initialized already.
            this.h = calcH();
        }

        /*  = getters =  */

        public Move getMove() {
            return move;
        }

        public AStarState getPrev() {
            return prev;
        }

        public int getG() {
            return g;
        }

        public float getF(){
            return g + h;
        }

        private float calcH() {
            return SingleAgentAStar_Solver.this.heuristicFunction.getH(this);
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
                    else if(null != (existingState = getFromOpen(child)) ){ //an equal state is waiting in open
                        //keep the one with min G
                        keepTheStateWithMinG(child, existingState); //O(LOGn)
                    }
                    else{ // it's a new state
                        addToOpen(child);
                    }
                }
            }
        }

        private void keepTheStateWithMinG(AStarState newState, AStarState existingState) {
            boolean shouldSwap = newState.g < existingState.g;
            if(shouldSwap){
                openList.remove(existingState);
                // no need to remove from openListStates because going to put a new value there anyway.
                addToOpen(newState);
            }
        }


        /**
         * looks for a {@link AStarState} equal to state, in {@link #openList}. If found, returns the found state, else returns null.
         */
        private AStarState getFromOpen(AStarState state) {
            return openListStates.get(state);
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

    }

    private class defaultHeuristic implements AStarHeuristic{

        @Override
        public float getH(AStarState state) {
            return state.move.currLocation.getCoordinate().distance(state.move.agent.target);
        }
    }
}
