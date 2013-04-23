package solve;

import struct.Solution;

/**
 * A Validator is used to determined if a solution is valid after passing it through a Solver.
 * @author Adrien Droguet - Sara Tari
 * @see Solver
 * @see Solution
 */
public interface Validator {
	public boolean isSolutionValid(Solution s, Feedback feedback);
}
