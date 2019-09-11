package Solvers;

import Instances.MAPF_Instance;

public interface I_Solver {
    Solution solve(MAPF_Instance instance, RunParameters parameters);
}
