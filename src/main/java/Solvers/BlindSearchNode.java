package Solvers;

import Problems.I_ProblemState;

public class BlindSearchNode extends A_SearchNode {

	protected double	_g;

	public BlindSearchNode( I_ProblemState currentProblemState ) {
		// Fields in A_Search Node
		_prev					= null;
		_currentProblemState 	= currentProblemState;
		_g 						= 0;
	}
	
	public BlindSearchNode( A_SearchNode	prev,
							I_ProblemState currentProblemState,
							double 			g ){
		// Fields in A_Search Node
		_prev					= prev;
		_currentProblemState 	= currentProblemState;
		_g 						= g;
	}
	
	@Override
	public double getH()
	{
		return 0;
	}
	
	@Override
	public double getG()
	{
		return _g;
	}
	
	@Override
	public double getF() 
	{
		return _g;
	}

	@Override
	public A_SearchNode createSearchNode( I_ProblemState 	currentProblemState	){
		double 		g		= _g + currentProblemState.getStateLastMoveCost();
		A_SearchNode newNode = new BlindSearchNode(this, currentProblemState, g);
		return newNode;
	}
	
	@Override
	public boolean equals(Object obj){

		if (obj instanceof BlindSearchNode){
			BlindSearchNode otherNode = (BlindSearchNode)obj;
			if (_currentProblemState.equals(otherNode._currentProblemState)) {
				return true;
			}
		}
		return false;
	}

}
