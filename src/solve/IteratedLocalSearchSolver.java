package solve;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import struct.EMoveType;
import struct.Exam;
import struct.Move;
import struct.ResultCouple;
import struct.Solution;
import util.CostCalculator;
import util.Moving;
import util.Solving;

/**
 * Soft Constraint Solver using an iterated local search algorithm
 * @author Adrien Droguet - Sara Tari
 *
 */
public class IteratedLocalSearchSolver extends SoftConstraintSolver {
	
	private Solution originalSolution;
	private int mainLoopCounter = -1;
	private Calendar startTime;
	
	private int stopCounter;
	/**
	 * In milliseconds. Default is two hours
	 */
	private int stopTime = 7200000;//now that's badass

	public IteratedLocalSearchSolver(Solution originalSolution) {
		super();
		this.originalSolution = originalSolution;
	}

	public Solution getOriginalSolution() {
		return originalSolution;
	}

	public void setOriginalSolution(Solution originalSolution) {
		this.originalSolution = originalSolution;
	}

	@SuppressWarnings("null")
	@Override
	public Solution solve() throws SolvingException {
		System.out.println("Beginning iterated local search solving");
		if (areStopConditionsSet())
			throw new SolvingException("Stop conditions were not set");
		Solution s = new Solution(originalSolution);
		List<Solution> solutions = new LinkedList<Solution>();
		startTime = Calendar.getInstance();
		
		System.out.println("--Beginning main loop: startTime=" + startTime);
		System.out.println("--Stop conditions:");
		if (stopCounter > 0)
			System.out.println("----stopCounter=" + stopCounter);;
		if (stopTime > 0)
			System.out.println("----stopTime=" + stopTime);;
		while (updateMainLoopStatus()) {
			Solution currentSolution = new Solution(s);
			int currentCost = CostCalculator.calculateCost(currentSolution);
			List<Move> currentMoves = new ArrayList<Move>();
			Move move = null;
			System.out.println("--Current cost=" + currentCost);
			//try to move each exam
			for (Integer examId : currentSolution.getExamSession().getExams().navigableKeySet()) {
				System.out.println("----Processing exam " + examId);
				int swapId = -1;
				
				ResultCouple target = lookForMoveTarget(examId, s, currentMoves);
				if (target != null) {
					System.out.println("------Found a move target: periodId=" + target.getPeriod().getId()
							+ "; roomId=" + target.getRoom().getId());
					move = Moving.movingSingleExam(examId, currentSolution, target.getPeriod().getId(), target.getRoom().getId());
				} else if ((swapId  = lookForSwapTarget(examId, target, s, currentMoves)) != -1) {
					System.out.println("------Found a swap target: " +
							"examId=" + swapId
							+ "; periodId=" + target.getPeriod().getId()
							+ "; roomId=" + target.getRoom().getId());
					move = Moving.swapExams(examId, swapId, currentSolution);
				} else {
					System.out.println("------No target found - moving on");
					continue;
				}
				System.out.println("------Saving tested move");
				currentMoves.add(move);
				//exam moved --> save solution if lower cost
				if (CostCalculator.calculateCost(currentSolution) < currentCost) {
					System.out.println("------Cost inferior to previous Solution - saving");
					solutions.add(currentSolution);
				} else {
					System.out.println("------Cost superior to previous Solution - ignoring");
				}
			}
			System.out.println("----Finished looping through the exam list - analyzing");
			if (solutions.isEmpty()) {
				System.out.println("----Solution list is empty - exiting loop");
				break;
				//TODO: look for another path?
			} else {
				Collections.sort(solutions);
				s = solutions.get(0);
				System.out.println("----Found the least costly Solution - moving to next iteration");
			}
		}
		System.out.println("--Exiting main loop");
		
		if (s.equals(originalSolution))
			System.err.println("--Warning: result Solution is identical to original Solution");
		return s;
	}

	/**
	 * 
	 * @return True if at least one of the stop conditions is set.
	 */
	public boolean areStopConditionsSet() {
		return (stopCounter > 0
				|| stopTime > 0);
	}

	/**
	 * Sets the stop conditions.
	 * @param stopCounter negative value to disable
	 * @param stopTime negative value to disable
	 */
	public void setStopConditions(int stopCounter, int stopTime) {
		this.stopCounter = stopCounter;
		this.stopTime = stopTime;
	}
	
	/**
	 * Called to update the main loop's status, and determine if the loop may continue or not.
	 * @return True if the loop may continue.
	 */
	protected boolean updateMainLoopStatus() {
		mainLoopCounter ++;
		if (stopCounter > 0 && mainLoopCounter >= stopCounter)
			return false;
		if (stopTime > 0 && startTime.compareTo(Calendar.getInstance()) >= stopTime)
			return false;
		return true;
	}

	/**
	 * Determines whether a Move is valid or not within a Solution.
	 * @param move
	 * @param s
	 * @return True if it is.
	 */
	public boolean isMoveValid(Move move, Solution s) {
		ResultCouple target = null;
		ResultCouple origin = null;
		int examId = -1;
		int examTargetId = -1;
		switch (move.getType()) {
		case SINGLE_MOVE:
			target = move.getTargets().get(0);
			examId = move.getExamIds().get(0);
			if (!Solving.canHost(s, examId, target.getPeriod().getId(), s.getResult())) {
				//period can't host
				return false;
			} else if (!Solving.getAvailablePeriod(s, examId, s.getResult())
					.contains(target.getPeriod().getId())) {
				//target period is not an available period
				return false;
			} else if (!Solving.findSuitable(s, examId, target.getPeriod().getId(), s.getResult())
					.contains(target.getRoom().getId())) { 
				//target room isn't suitable
				return false;
			} else {
				//everything OK
				return true;
			}
		case SWAP:
			examId = move.getExamIds().get(0);
			examTargetId = move.getExamIds().get(1);
			return Moving.canSwap(examId, examTargetId, s);
		case MULTIPLE_MOVES:
			System.err.println("Not yet implemented");
			break;
		}
		return false;
	}
	
	@Override
	public ResultCouple lookForMoveTarget(int examId, Solution s, List<Move> previousMoves) {
		ResultCouple target = null;
		for (ResultCouple rc : s.getResult()) {
			Move simulatedMove = new Move(EMoveType.SINGLE_MOVE, examId, s.getResultForExam(examId), rc);
			if (previousMoves.contains(simulatedMove)) {
				continue;
			}
			if (isMoveValid(simulatedMove, s)) {
				target = rc;
			}
		}
		return target;
	}
	
	@Override
	public List<ResultCouple> lookForMoveTargets(List<Integer> examIds, Solution s, List<Move> previousMoves) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lookForSwapTarget(int examId, ResultCouple targetLocation, Solution s, List<Move> previousMoves) {
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		ResultCouple origin = s.getResultForExam(examId);
		for (ResultCouple rc : s.getResult()) {
			for (Exam currentExam : rc.getExamList()) {
				examIds.add(currentExam.getId());
				Move simulatedMove = new Move(EMoveType.SWAP, examIds, origin, rc);
				if (isMoveValid(simulatedMove, s)) {
					targetLocation = rc;
					return currentExam.getId();
				} else {
					examIds.remove(1);
				}
			}
		}
		return -1;
	}

}
