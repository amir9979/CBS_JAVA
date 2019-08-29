package Problems;

public interface I_Problem{
	I_ProblemState 	getProblemState();
	I_Heuristic 	getProblemHeuristic();
	boolean 		performMove(I_ProblemMove move);

}

