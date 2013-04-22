package solve;

import struct.Solution;

/**
 * 
 * @author Adrien Droguet - Sara Tari
 * @see Validator
 */
public interface Solver {
	public Solution solve() throws SolvingException;
}
