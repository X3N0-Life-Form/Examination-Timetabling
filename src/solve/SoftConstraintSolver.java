package solve;

import java.util.List;

import struct.ResultCouple;

/**
 * 
 * @author Adrien Droguet - Sara Tari
 *
 */
public abstract class SoftConstraintSolver implements Solver {
	
	/**
	 * Looks for an available room/period for the specified exam.
	 * @param examId
	 * @return An acceptable period/room.
	 */
	public abstract ResultCouple lookForMoveTarget(int examId);
	
	/**
	 * Look for one or more room/period to move the specified exams in.
	 * @param examIds
	 * @return A list of acceptable ResultCouples.
	 */
	public abstract List<ResultCouple> lookForMoveTargets(List<Integer> examIds);
	
	/**
	 * Look for an exam the can safely be swapped with the specified exam 
	 * @param examId
	 * @param targetLocation The location of the target exam, or null.
	 * @return The target exam id, or -1.
	 */
	public abstract int lookForSwapTarget(int examId, ResultCouple targetLocation);
	
}
