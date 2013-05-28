package solve;

import java.util.List;

import struct.Move;
import struct.ResultCouple;
import struct.Solution;

/**
 * 
 * @author Adrien Droguet - Sara Tari
 *
 */
public abstract class SoftConstraintSolver implements Solver {
	
	/**
	 * Looks for an available room/period for the specified exam.
	 * @param examId
	 * @param s
	 * @param previousMoves
	 * @return An acceptable period/room.
	 * @throws MovingException 
	 */
	public abstract ResultCouple lookForMoveTarget(int examId, Solution s, List<Move> previousMoves) throws MovingException;
	
	/**
	 * Look for one or more room/period to move the specified exams in.
	 * @param examIds
	 * @param s
	 * @param previousMoves
	 * @return A list of acceptable ResultCouples.
	 * @throws MovingException 
	 */
	public abstract List<ResultCouple> lookForMoveTargets(List<Integer> examIds, Solution s, List<Move> previousMoves) throws MovingException;
	
	/**
	 * Look for an exam the can safely be swapped with the specified exam 
	 * @param examId
	 * @param s
	 * @param previousMoves
	 * @return The target exam id, or -1.
	 * @throws MovingException 
	 */
	public abstract int lookForSwapTarget(int examId, Solution s, List<Move> previousMoves) throws MovingException;
	
}
