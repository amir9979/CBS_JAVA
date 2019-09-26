package Solvers.PrioritisedPlanning;

import Solvers.SingleAgentPlan;
import Solvers.Solution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrioritisedPlanning_SolverTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void solve() {
    }

    @Test
    void init() {
    }

    public static boolean isValidSolution(Solution solution){
        for (SingleAgentPlan plan :
                solution) {
            for (SingleAgentPlan otherPlan :
                    solution) {
                if(! (plan == otherPlan)){ //don't compare with self
                    if(plan.conflictsWith(otherPlan)) return false;
                }
            }
        }
        return true;
    }
}