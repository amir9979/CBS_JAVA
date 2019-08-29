package Solvers;

import Problems.I_ProblemMove;
import Problems.I_ProblemState;

import java.util.ArrayList;
import java.util.List;

public abstract class A_SearchNode {

    A_SearchNode		_prev;
    I_ProblemState      _currentProblemState;

    public List<A_SearchNode> getNeighbors() {
        List<A_SearchNode> 	    neighbors 		    = new ArrayList<A_SearchNode>();
        List<I_ProblemState>    neighborStates 	    = _currentProblemState.getNeighborStates();

        for (I_ProblemState state : neighborStates) {
            A_SearchNode newNode = createSearchNode(state);
            neighbors.add(newNode);
        }
        return neighbors;
    }

    public boolean isGoal()
    {
        return _currentProblemState.isGoalState();
    }

    public I_ProblemMove getLastMove()
    {
        return _currentProblemState.getStateLastMove();
    }

    abstract public double 		getH();

    abstract public double 		getG();

    abstract public double 		getF();

    abstract public A_SearchNode createSearchNode(I_ProblemState currentProblemState);


}
