package Solvers.AStar;

import Instances.Agents.Agent;
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
public class SingleAgentAStar_Solver extends A_Solver {

    /**
     * Since A* should solve even very large single agent problems very quickly, set default timeout to 3 seconds.
     * A timeout would therefore very likely mean the problem is unsolvable.
     * Without a timeout, unsolvable problems would not resolve, since new states with higher time ({@link Move#timeNow},
     * would continue to be generated ad infinitum. This would eventually result in a heap overflow.
     */
    protected static final long DEFAULT_TIMEOUT = 3 * 1000;
    protected static final int DEFAULT_PROBLEM_START_TIME = 0;
    private static final Comparator<AStarState> stateFComparator = Comparator.comparing(AStarState::getF);
    private static final Comparator<AStarState> stateGComparator = Comparator.comparing(AStarState::getG);

    private ConstraintSet constraints;
    private AStarHeuristic heuristicFunction;
    private I_OpenList<AStarState> openList;
    private Set<AStarState> closed;
    private Agent agent;
    private I_Map map;
    private SingleAgentPlan existingPlan;
    private Solution existingSolution;
    /**
     * Not real-world time. The problem's start time.
     */
    private int problemStartTime;
    private int expandedNodes;
    private int generatedNodes;

    public SingleAgentAStar_Solver() {
        super.DEFAULT_TIMEOUT = SingleAgentAStar_Solver.DEFAULT_TIMEOUT;
    }

    /*  = set up =  */

    protected void init(MAPF_Instance instance, RunParameters runParameters){
        super.init(instance, runParameters);

        this.constraints = runParameters.constraints == null ? new ConstraintSet(): runParameters.constraints;
        this.agent = instance.agents.get(0);
        this.map = instance.map;

        if(runParameters.existingSolution != null){
            this.existingSolution = runParameters.existingSolution;
            if(runParameters.existingSolution.getPlanFor(this.agent) != null){
                this.existingPlan = runParameters.existingSolution.getPlanFor(this.agent);
                this.problemStartTime = this.existingPlan.getEndTime();
            }
            else {
                this.existingPlan = new SingleAgentPlan(this.agent);
                this.existingSolution.putPlan(this.existingPlan);
                this.problemStartTime = DEFAULT_PROBLEM_START_TIME;
            }
        }
        else{
            // make a new, empty solution, with a new, empty, plan
            this.existingSolution = new Solution();
            this.existingPlan = new SingleAgentPlan(this.agent);
            this.existingSolution.putPlan(this.existingPlan);
        }

        if(runParameters instanceof  RunParameters_SAAStar
                && ((RunParameters_SAAStar) runParameters).heuristicFunction != null){
            RunParameters_SAAStar parameters = ((RunParameters_SAAStar) runParameters);
            this.heuristicFunction = parameters.heuristicFunction;
        }
        else{
            this.heuristicFunction = new defaultHeuristic();
        }
        if(runParameters instanceof  RunParameters_SAAStar
                && ((RunParameters_SAAStar) runParameters).problemStartTime >= 0){
            RunParameters_SAAStar parameters = ((RunParameters_SAAStar) runParameters);
            this.problemStartTime = parameters.problemStartTime;
        }
        // else keep the value that it has already been initialised with (above)

        this.openList = new OpenList<>(stateFComparator);
        this.expandedNodes = 0;
        this.closed = new HashSet<>();
        this.generatedNodes = 0;
    }

    /*  = A* algorithm =  */

    @Override
    protected Solution runAlgorithm(MAPF_Instance instance, RunParameters parameters) {
        return solveAStar();
    }

    /**
     * Solves AStar for a single agent.
     * Assumes only 1 goal state - otherwise there may be problems when accounting for constraints at goal that come after reaching goal.
     * @return a solution that contains a plan for the {@link #agent} to its goal.
     */
    protected Solution solveAStar() {
        // if failed to init OPEN then the problem cannot be solved as defined (bad constraints? bad existing plan?)
        if (!initOpen()) return null;

        AStarState currentState;
        int firstRejectionAtGoalTime = -1;

        while ((currentState = openList.poll()) != null){ //dequeu in the if
            if(checkTimeout()) {return null;}
            closed.add(currentState);

            // todo change to early goal test!
            if (isGoalState(currentState)){
                // check to see if a rejecting constraint on the goal exists at some point in the future.

                // smaller means we have passed the current rejection (if one existed) and should check if another exists.
                // shouldn't be equal because such a state would not be generated
                if(firstRejectionAtGoalTime < currentState.move.timeNow) {
                    // do the expensive update/check
                    firstRejectionAtGoalTime = constraints.rejectsEventually(currentState.move);
                }

                if(firstRejectionAtGoalTime == -1){ // no rejections. done!
                    currentState.backTracePlan(); // updates this.existingPlan which is contained in this.existingSolution
                    return this.existingSolution; // the goal is good and we can return the plan.
                }
                else{ // we are rejected from the goal at some point in the future. expand.
                    currentState.expand();
                }
            }
            else{ //expand
                currentState.expand(); //doesn't generate closed or duplicate states
            }
        }
        return null; //no goal state found (problem unsolvable)
    }

    /**
     * Initialises {@link #openList OPEN}.
     *
     * OPEN is not initialised with a single root state as is common. This is because states in this solver represent
     * {@link Move moves} (classically - operators) rather than {@link I_MapCell map cells} (classically - states).
     * Instead, OPEN is initialised with all possible moves from the starting position.
     * @return true if OPEN was successfully initialised, else false.
     */
    protected boolean initOpen() {
        // if the existing plan isn't empty, we start from the last move of the existing plan.
        if(existingPlan.size() > 0){
            Move lastExistingMove = existingPlan.moveAt(existingPlan.getEndTime());
            // We assume that we cannot change the existing plan, so if it is rejected by constraints, we can't initialise OPEN.
            if(constraints.rejects(lastExistingMove)) {return false;}

            openList.add(new AStarState(existingPlan.moveAt(existingPlan.getEndTime()),null, /*g=number of moves*/existingPlan.size()));
        }
        else { // the existing plan is empty (no existing plan)

            I_MapCell sourceCell = map.getMapCell(agent.source);
            // can move to neighboring cells or stay put
            List<I_MapCell> neighborCellsIncludingCurrent = new ArrayList<>(sourceCell.getNeighbors());
            neighborCellsIncludingCurrent.add(sourceCell);

            for (I_MapCell destination: neighborCellsIncludingCurrent) {
                Move possibleMove = new Move(agent, problemStartTime + 1, sourceCell, destination);
                if (constraints.accepts(possibleMove)) { //move not prohibited by existing constraint
                    AStarState rootState = new AStarState(possibleMove, null, 1);
                    openList.add(rootState);
                    generatedNodes++;
                }
            }

        }

        // if none of the root nodes was valid, OPEN will be empty, and thus uninitialised.
        return !openList.isEmpty();
    }

    private boolean isGoalState(AStarState state) {
        return state.move.currLocation.getCoordinate().equals(agent.target);
    }

//    private boolean handleGoalFound(AStarState goalState) {
//        /* also check that we can stay at goal forever. This check is somewhat expensive. */
//        int firstRejectionTime = constraints.rejectsEventually(goalState.move);
//        if(firstRejectionTime == -1){ // no rejections. done!
//            goalState.backTracePlan(); // updates this.existingPlan which is contained in this.existingSolution
//            return true; // the goal is good and we can return the plan.
//        }
//        else{
//            /*
//                We form a sequence of "stay" moves from the goal we found, until just before being rejected. We
//                then solve a smaller problem around that problematic time. This continues recursively until all
//                rejecting constraints at the goal are handled.
//             */
//
//            // clear OPEN in preparation
//            openList.clear();
//
//            // stay at goal until before we are rejected
//            AStarState currentState = goalState;
//            int time = goalState.move.timeNow + 1;
//            while (time < firstRejectionTime){
//                Move stayMove = new Move(this.agent, time, goalState.move.currLocation, goalState.move.currLocation);
//                AStarState nextState = new AStarState(stayMove, currentState, currentState.g + 1);
//
//                currentState = nextState;
//                time++;
//            }
//
//            // expand the latest state that is not rejected
//            currentState.expand();
//
//            // there is a future problem and we must continue the search to overcome that problem.
//            return false;
//        }
//    }

    /*  = wind down =  */

    protected void writeMetricsToReport(Solution solution) {
        // skips super's writeMetricsToReport(Solution solution).
        super.endTime = System.currentTimeMillis();
        if(instanceReport != null){
            super.instanceReport.putIntegerValue(InstanceReport.StandardFields.expandedNodesLowLevel, this.expandedNodes);
            super.instanceReport.putIntegerValue(InstanceReport.StandardFields.generatedNodesLowLevel, this.generatedNodes);
            super.instanceReport.putIntegerValue(InstanceReport.StandardFields.elapsedTimeMS, (int)(super.endTime-super.startTime));
        }
    }

    protected void releaseMemory() {
        super.releaseMemory();
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

    public class AStarState implements Comparable<AStarState>{

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

        /*  = other methods =  */

        public float getF(){
            return g + h;
        }

        private float calcH() {
            return SingleAgentAStar_Solver.this.heuristicFunction.getH(this);
        }

        public void expand() {
            expandedNodes++;
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
                    else if(null != (existingState = openList.get(child)) ){ //an equal state is waiting in open
                        //keep the one with min G
                        keepTheStateWithMinG(child, existingState); //O(LOGn)
                    }
                    else{ // it's a new state
                        openList.add(child);
                    }
                }
            }
        }

        private void keepTheStateWithMinG(AStarState newState, AStarState existingState) {
            openList.keepOne(existingState, newState, stateGComparator);
//            boolean shouldSwap = newState.g < existingState.g;
//            if(shouldSwap){
//                openList.replace(existingState, newState);
//            }
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
         * @param o {@inheritDoc}
         * @return {@inheritDoc}
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

        @Override
        public int compareTo(AStarState o) {
            return stateFComparator.compare(this, o);
        }
    }

    private class defaultHeuristic implements AStarHeuristic{

        @Override
        public float getH(AStarState state) {
            return state.move.currLocation.getCoordinate().distance(state.move.agent.target);
        }
    }
}
