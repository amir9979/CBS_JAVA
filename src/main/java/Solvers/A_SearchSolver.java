package Solvers;

import Problems.I_Problem;
import Problems.I_ProblemMove;
import Problems.I_ProblemState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class A_SearchSolver {

    public List<I_ProblemMove> solve(I_Problem problem) {
        I_ProblemState          problemState	= problem.getProblemState();
        A_SearchNode			goal			= abstractSearch(problemState);
        List<I_ProblemMove>     solution		= goalNodeToSolutionPath(goal);

        return solution;
    }

    private	A_SearchNode	abstractSearch(I_ProblemState problemState) {

        initLists();
        A_SearchNode Vs 		= createSearchRoot(problemState);
        A_SearchNode current	= null;
        addToOpen(Vs);

        while (openSize() > 0){

            current = getBest();
            if (current.isGoal()) {
                return current;
            }

            List<A_SearchNode> neighbors = current.getNeighbors();
            for (A_SearchNode Vn : neighbors){

                if (isClosed(Vn)) {
                    continue;
                }

                if (!isOpen(Vn) || getOpen(Vn).getG() > Vn.getG()) {
                    addToOpen(Vn);
                }
            }
            addToClosed(current);
        }
        return null;
    }

    private List<I_ProblemMove> goalNodeToSolutionPath(A_SearchNode goal) {
        if (goal == null) {
            return null;
        }
        A_SearchNode 		    currentNode		= goal;
        List<I_ProblemMove> 	solutionPath 	= new ArrayList<I_ProblemMove>();

        while (currentNode._prev != null){
            solutionPath.add(currentNode.getLastMove());
            currentNode = currentNode._prev;
        }

        Collections.reverse(solutionPath);
        return solutionPath;
    }

    abstract public String				getSolverName();

    abstract public	void 				initLists();

    abstract public	A_SearchNode			getOpen(A_SearchNode node);

    abstract public	boolean				isOpen(A_SearchNode node);

    abstract public	boolean				isClosed(A_SearchNode node);

    abstract public	A_SearchNode 		createSearchRoot(I_ProblemState problemState);

    abstract public	void 				addToOpen(A_SearchNode node);

    abstract public	void			addToClosed(A_SearchNode node);

    abstract public	int 			openSize();

    abstract public	A_SearchNode		getBest();

}
