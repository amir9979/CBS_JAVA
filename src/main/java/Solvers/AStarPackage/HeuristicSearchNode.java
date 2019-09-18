package Solvers.AStarPackage;

import Solvers.AStarPackage.Problems.I_Heuristic;
import Solvers.AStarPackage.Problems.I_ProblemState;

public class HeuristicSearchNode extends BlindSearchNode
{
	double		_h;
	I_Heuristic _heuristic;
	
	public HeuristicSearchNode(	I_ProblemState currentProblemState ){
		super(currentProblemState);
		_heuristic 	= currentProblemState.getProblem().getProblemHeuristic();
		_h			= _heuristic.getHeuristic(currentProblemState);
	}
	
	
	public HeuristicSearchNode(
								A_SearchNode prev,
								I_ProblemState 	currentProblemState,
								double 			g,
								I_Heuristic 		heuristic){

		super(prev, currentProblemState, g);
		_heuristic 	= heuristic;
		_h			= _heuristic.getHeuristic(currentProblemState);
	}
	
	@Override
	public double getH()
	{
		return _h;
	}
	
	
	@Override
	public double getF()
	{
		return _g + _h;
	}
	
	
	@Override
	public A_SearchNode createSearchNode(I_ProblemState 	currentProblemState	){

		double 		g		= _g + currentProblemState.getStateLastMoveCost();
		A_SearchNode newNode = new HeuristicSearchNode(this, currentProblemState, g, _heuristic);
		return newNode;
	}

}
