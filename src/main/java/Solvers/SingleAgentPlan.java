package Solvers;

import Instances.Agents.Agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A plan for a single agent, which is a sequence of {@link Move}s.
 * An instance of this class is unmodifiable outside of this class's package.
 */
public class SingleAgentPlan implements Iterable<Move> {
    private List<Move> moves;
    public final Agent agent;

    /**
     * @param moves a sequence of moves for the agent. Can be empty. All {@link Move}s must be moves for the same {@link Agent},
     *             and the contained {@link Move}'s {@link Move#timeNow} must form an ascending series with d=1.
     * @param agent the plan's agent.
     */
    public SingleAgentPlan(Agent agent, List<Move> moves) {
        if(moves == null || agent == null) throw new IllegalArgumentException();
        if(!isValidMoveSequenceForAgent(moves, agent)) throw new IllegalArgumentException();
        this.agent = agent;
        this.moves = new ArrayList<>(moves);
    }

    /**
     * Copy Constructor.
     * @param planToCopy  a {@link SingleAgentPlan}. @NotNull
     * @throws NullPointerException if #planToCopy is null.
     */
    public SingleAgentPlan(SingleAgentPlan planToCopy){
        this(planToCopy.agent, planToCopy.moves);
    }

    public SingleAgentPlan(Agent agent) {
        this(agent, new ArrayList<>());
    }

    private static boolean isValidNextMoveForAgent(List<Move> currentMoves, Move newMove, Agent agent){
        return agent.equals(newMove.agent) &&
                ( currentMoves.size() == 0 ||
                        newMove.timeNow - currentMoves.get(currentMoves.size()-1).timeNow == 1 );
    }

    private static boolean isValidMoveSequenceForAgent(List<Move> moves, Agent agent) {
        if(moves.isEmpty()){return true;}
        else if(!moves.get(0).prevLocation.getCoordinate().equals(agent.source)){
            // the plan starts at a different coordinate than the agent.
            return false;
        }
        else{
            boolean result = true;
            Move prevMove = moves.get(0);
            for (Move move:
                    moves) {
                result &= (move==prevMove //for first iteration
                        || (move.timeNow-prevMove.timeNow == 1)); //ascending series with d=1
                prevMove = move;

                result &= move.agent.equals(agent); //all same agent
            }
            return result;
        }
    }

    /**
     * Add a single {@link Move} to the plan. The new move's {@link Move#timeNow} must be exactly 1 more than the
     * current latest move.
     * @param newMove a {@link Move} to add to the plan. The new move's {@link Move#timeNow} must be exactly 1 more than
     *               the current latest move.
     */
    void addMove(Move newMove){
        if(isValidNextMoveForAgent(this.moves, newMove, this.agent)){
            this.moves.add(newMove);
        }
        else {throw new IllegalArgumentException();}
    }

    /**
     * Appends a new sequence of moves to the current plan. The joint sequence must meet the same conditions as in
     * {@link #setMoves(List)}.
     * @param newMoves a sequence of moves to append to the current plan.
     */
    void addMoves(List<Move> newMoves){
        List<Move> tmpMoves = new ArrayList<>(this.moves);
        tmpMoves.addAll(newMoves);
        if(isValidMoveSequenceForAgent(tmpMoves, agent)){
            this.moves = tmpMoves;
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    void clearMoves(){this.moves.clear();}

    /**
     * Replaces the current plan with a copy of the given sequence of moves.
     * Can be empty. All {@link Move}s must be moves for the same {@link Agent}, and the contained {@link Move}'s
     * {@link Move#timeNow} must form an ascending series with d=1. Must start at {@link #agent}s source.
     * @param newMoves a sequence of moves for the agent.
     */
    void setMoves(List<Move> newMoves){
        if(newMoves == null) throw new IllegalArgumentException();
        if(isValidMoveSequenceForAgent(newMoves, agent)){
            this.moves = new ArrayList<>(newMoves);
        }
        else{
            throw new IllegalArgumentException();
        }
    }

//    /**
//     * Returns a list of the moves in the plan. The returned list is a copy, and changes made in it will not effect the
//     * plan.
//     * @return a list of the moves in the plan. The returned list is a copy, and changes made in it will not effect the
//     *      plan.
//     */
//    public List<Move> getMoves(){return new ArrayList<>(this.moves);}

    /**
     * return the move in the plan where {@link Move#timeNow} equals the given time.
     * @param time the time of the move in the plan.
     * @return the move in the plan where {@link Move#timeNow} equals the given time.
     */
    public Move moveAt(int time){
        int startTime = getStartTime();
        if(time < startTime || time > getEndTime()){ return null;}
        else{
            return moves.get(time - startTime); // return the move at the specified time
        }
    }

    /**
     * @return the start time of the plan, which is 1 less than the time of the first move. returns -1 if plan is empty.
     */
    public int getStartTime(){
        return moves.isEmpty() ? -1 : moves.get(0).timeNow-1;
        // since the first move represents one timestep after the start, the start time is timeNow of the first move -1
    }

    /**
     * @return the start time of the plan, which is the time of the last move. returns -1 if plan is empty.
     */
    public int getEndTime(){
        return moves.isEmpty() ? -1 : moves.get(moves.size()-1).timeNow;
    }

    /**
     * Returns the total time of the plan, which is the difference between end and start times.
     * @return the total time of the plan, which is the difference between end and start times. Return -1 if plan is empty.
     */
    public int getTotalTime(){return moves.isEmpty() ? -1 : this.getEndTime()-this.getStartTime();}

    public boolean conflictsWith(SingleAgentPlan other){
        //imp
        return true;
    }

    /*  = Iterable Interface =  */

    @Override
    public Iterator<Move> iterator() {
        return this.moves.iterator();
    }

    @Override
    public String toString() {
        return "SingleAgentPlan{" +
                "agent=" + agent +
                ", moves=" + moves +
                '}';
    }
}
