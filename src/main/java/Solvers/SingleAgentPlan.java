package Solvers;

import Instances.Agents.Agent;

import java.util.ArrayList;
import java.util.List;

/**
 * A plan for a single agent, which is a sequence of {@link Move}s.
 * An instance of this class is unmodifiable outside of this class's package.
 */
public class SingleAgentPlan {
    private List<Move> moves;
    private final Agent agent;

    /**
     * @param moves a sequence of moves for the agent. Can be empty. All {@link Move}s must be moves for the same {@link Agent},
     *             and the contained {@link Move}'s {@link Move#timeNow} must form an ascending series with d=1.
     * @param agent the plan's agent.
     */
    public SingleAgentPlan(List<Move> moves, Agent agent) {
        if(moves == null || agent == null) throw new IllegalArgumentException();
        if(!isValidMoveSequenceForAgent(moves, agent)) throw new IllegalArgumentException();
        this.agent = agent;
        this.moves = moves;
    }

    public SingleAgentPlan(Agent agent) {
        this(new ArrayList<>(), agent);
    }

    private boolean isValidMoveSequenceForAgent(List<Move> moves, Agent agent) {
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
                result &= (move==prevMove) //for first iteration
                        || (move.timeNow-prevMove.timeNow == 1); //ascending series with d=1
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
        if(this.moves.size() == 0 ||
                (newMove.timeNow - this.moves.get(0).timeNow == 1 && agent.equals(newMove.agent)) ){
            this.moves.add(newMove);
        }
        else {throw new IllegalArgumentException();}
    }

    /**
     * Appends a new sequence of moves to the current plan. The joint sequence must meet the same conditions as in
     * {@link #setMoves(List)}.
     * @param newMoves a sequence of moves to append to the current plan.
     */
    void addMoves(Move newMoves){
        List<Move> tmpMoves = new ArrayList<>(this.moves);
        tmpMoves.add(newMoves);
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
     * @param newMoves a sequence of moves for the agent. Can be empty. All {@link Move}s must be moves for the same {@link Agent},
     *      and the contained {@link Move}'s {@link Move#timeNow} must form an ascending series with d=1.
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

    /**
     * Returns a list of the moves in the plan. The returned list is a copy, and changes made in it will not effect the
     * plan.
     * @return a list of the moves in the plan. The returned list is a copy, and changes made in it will not effect the
     *      plan.
     */
    public List<Move> getMoves(){return new ArrayList<>(this.moves);}

    public int getStartTime(){
        return moves.isEmpty() ? 0 : moves.get(0).timeNow-1;
        // since the first move represents one timestep after the start, the start time is timeNow of the first move -1
    }

    public int getEndTime(){
        return moves.isEmpty() ? 0 : moves.get(moves.size()-1).timeNow;
    }

    public int getElapsedTime(){return this.getEndTime()-this.getStartTime();}
}
