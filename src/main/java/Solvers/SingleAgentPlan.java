package Solvers;

import Instances.Agents.Agent;
import Solvers.ConstraintsAndConflicts.A_Conflict;
import Solvers.ConstraintsAndConflicts.SwappingConflict;
import Solvers.ConstraintsAndConflicts.VertexConflict;

import java.util.*;
import java.util.function.Consumer;

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
        if (newMove == null){return false;}
        return agent.equals(newMove.agent) &&
                ( currentMoves.size() == 0 ||
                        newMove.timeNow - currentMoves.get(currentMoves.size()-1).timeNow == 1 );
    }

    private static boolean isValidMoveSequenceForAgent(List<Move> moves, Agent agent) {
        if(moves.isEmpty()){return true;}
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
    public void addMove(Move newMove){
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
    public void addMoves(List<Move> newMoves){
        if(newMoves == null){throw new IllegalArgumentException();}
        List<Move> tmpMoves = new ArrayList<>(this.moves);
        tmpMoves.addAll(newMoves);
        if(isValidMoveSequenceForAgent(tmpMoves, agent)){
            this.moves = tmpMoves;
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    public void clearMoves(){this.moves.clear();}

    /**
     * Replaces the current plan with a copy of the given sequence of moves.
     * Can be empty. All {@link Move}s must be moves for the same {@link Agent}, and the contained {@link Move}'s
     * {@link Move#timeNow} must form an ascending series with d=1. Must start at {@link #agent}s source.
     * @param newMoves a sequence of moves for the agent.
     */
    public void setMoves(List<Move> newMoves){
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
     * Return the move in the plan where {@link Move#timeNow} equals the given time.
     * O(1).
     * @param time the time of the move in the plan.
     * @return the move in the plan where {@link Move#timeNow} equals the given time, or null if there is no move for
     * that time.
     */
    public Move moveAt(int time){
        if(moves.isEmpty()) return null;
        int requestedIndex = time - getFirstMoveTime();
        return requestedIndex >= moves.size() || requestedIndex < 0 ? null : moves.get(requestedIndex);
    }

    /**
     * @return the start time of the plan, which is 1 less than the time of the first move. returns -1 if plan is empty.
     */
    public int getPlanStartTime(){
        return moves.isEmpty() ? -1 : moves.get(0).timeNow - 1;
        // since the first move represents one timestep after the start, the start time is timeNow of the first move -1
    }

    /**
     * @return the {@link Move#timeNow time} of the first move in the plan.
     */
    public int getFirstMoveTime(){
        return moves.isEmpty() ? -1 : moves.get(0).timeNow;
    }

    /**
     * @return the end time of the plan, which is the time of the last move. returns -1 if plan is empty.
     */
    public int getEndTime(){
        return moves.isEmpty() ? -1 : moves.get(moves.size()-1).timeNow;
    }

    /**
     * Returns the total time of the plan, which is the difference between end and start times. It is the same as the number of moves in the plan.
     * @return the total time of the plan, which is the difference between end and start times. Return 0 if plan is empty.
     */
    public int size(){return moves.isEmpty() ? 0 : this.getEndTime()-this.getPlanStartTime();}

    /**
     * Compares with another {@link SingleAgentPlan}, looking for vertex conflicts ({@link VertexConflict}) or
     * swapping conflicts ({@link SwappingConflict}). Runtime is O(the number of moves in this plan).
     * @param other
     * @return true if a conflict exists between the plans.
     */
    public boolean conflictsWith(SingleAgentPlan other){
        // todo improve by finding lower and upper bound for time, and checking only in that range
        // todo use the static functions in the Conflict classes instead
        for (Move localMove :
                this.moves) {
            Move otherMoveAtTime = other.moveAt(localMove.timeNow);
            if(otherMoveAtTime != null){
                boolean vertexConflict = otherMoveAtTime.currLocation.equals(localMove.currLocation);
                boolean swappingConflict = otherMoveAtTime.prevLocation.equals(localMove.currLocation)
                        && localMove.prevLocation.equals(otherMoveAtTime.currLocation);
                if(A_Conflict.haveConflicts(localMove, otherMoveAtTime)){return true;}
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "SingleAgentPlan{" +
                "agent=" + agent +
                ", moves=" + moves +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleAgentPlan moves1 = (SingleAgentPlan) o;
        return moves.equals(moves1.moves) &&
                agent.equals(moves1.agent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moves, agent);
    }

    /*  = Iterable Interface =  */

    @Override
    public Iterator<Move> iterator() {
        return this.moves.iterator();
    }

    @Override
    public void forEach(Consumer<? super Move> action) {
        this.moves.forEach(action);
    }

    @Override
    public Spliterator<Move> spliterator() {
        return this.moves.spliterator();
    }
}
