package Solvers.AStarPackage;

import Solvers.AStarPackage.Problems.I_ProblemState;

public class A_StarSearch extends A_SearchSolver {
	// Define lists here ...
	
	@Override
	public String getSolverName()
	{
		return "AStar";
	}
	
	@Override
	public A_SearchNode createSearchRoot(I_ProblemState problemState){
		A_SearchNode newNode = new HeuristicSearchNode(problemState);
		return newNode;
	}

	@Override
	public void initLists(){
	}

	@Override
	public A_SearchNode getOpen(A_SearchNode node){
		return null;
	}

	@Override
	public boolean isOpen( A_SearchNode node){
		return false;
	}
	
	@Override
	public boolean isClosed( A_SearchNode node){
		return false;
	}

	@Override
	public void addToOpen( A_SearchNode node){

	}


	@Override
	public void addToClosed( A_SearchNode node){
	}

	@Override
	public int openSize() 
	{
		return 0;
	}

	@Override
	public A_SearchNode getBest()
	{
		return null;
	}

}
