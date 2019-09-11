package Solvers.AStarPackage.Problems;

import java.util.List;

public interface I_ProblemState{

	List<I_ProblemState> getNeighborStates();

	I_Problem getProblem();

	boolean	isGoalState();

	I_ProblemMove getStateLastMove();

	double	getStateLastMoveCost();

	I_ProblemState performMove(I_ProblemMove move);

}
