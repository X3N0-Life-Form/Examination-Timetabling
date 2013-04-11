package struct;

/**
 * 
 * @author Adrien Droguet - Sara Tari
 *
 */
public class Solution {
	/**
	 * List of coinciding exams [id1][id2].
	 */
	private int[][] examCoincidence;
	/**
	 * [id_exam][id_period]
	 */
	private int[][] examPeriod;
	/**
	 * [id_exam][id_room]
	 */
	private int[][] examRoom;
	/**
	 * [id_exam] True if the exam has been placed on the timetable.
	 */
	private boolean[] examPlaced;
	
	private int[][] result;
	private ExamSession examSession;
	
	/**
	 * Must at least be passed through a HardConstraintSolver
	 * after being created.
	 * @param examSession
	 */
	public Solution(ExamSession examSession) {
		this.examSession = examSession;
		/*for () {
			
		}*/
	}
	
}
