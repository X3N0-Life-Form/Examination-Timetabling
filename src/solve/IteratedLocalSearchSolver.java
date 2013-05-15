package solve;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import struct.ResultCouple;
import struct.Solution;
import util.CostCalculator;
import util.Moving;

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

	@Override
	public Solution solve() throws SolvingException {
		System.out.println("Beginning iterated local search solving");
		if (areStopConditionsSet())
			throw new SolvingException("Stop conditions were not set");
		Solution s = new Solution(originalSolution);
		List<Solution> solutions = new LinkedList<Solution>();
		startTime = Calendar.getInstance();
		//TODO: underp
		Moving derp = new Moving();
		
		System.out.println("--Beginning main loop: startTime=" + startTime);
		System.out.println("--Stop conditions:");
		if (stopCounter > 0)
			System.out.println("----stopCounter=" + stopCounter);;
		if (stopTime > 0)
			System.out.println("----stopTime=" + stopTime);;
		while (updateMainLoopStatus()) {
			Solution currentSolution = new Solution(s);
			int currentCost = CostCalculator.calculateCost(currentSolution);
			System.out.println("--Current cost=" + currentCost);
			//try to move each exam
			for (Integer examId : currentSolution.getExamSession().getExams().navigableKeySet()) {
				System.out.println("----Processing exam " + examId);
				int swapId = -1;
				
				ResultCouple target = lookForMoveTarget(examId);
				if (target != null) {
					System.out.println("------Found a move target: periodId=" + target.getPeriod().getId()
							+ "; roomId=" + target.getRoom().getId());
					//TODO:move
					derp.movingSingleExam(examId, currentSolution, target.getPeriod().getId(), target.getRoom().getId());
				} else if ((swapId  = lookForSwapTarget(examId, target)) != -1) {
					System.out.println("------Found a swap target: " +
							"examId=" + swapId
							+ "; periodId=" + target.getPeriod().getId()
							+ "; roomId=" + target.getRoom().getId());
					//TODO: move
				} else {
					System.out.println("------No target found - moving on");
					continue;
				}
				//exam moved --> save solution if lower cost
				if (CostCalculator.calculateCost(currentSolution) < currentCost) {
					System.out.println("Cost inferior to previous Solution - saving");
					solutions.add(currentSolution);
				} else {
					System.out.println("Cost superior to previous Solution - ignoring");
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
		if (stopTime > 0 && startTime.compareTo(Calendar.getInstance()) >= stopTime)//time
			return false;
		return true;
	}

	@Override
	public ResultCouple lookForMoveTarget(int examId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResultCouple> lookForMoveTargets(List<Integer> examIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lookForSwapTarget(int examId, ResultCouple targetLocation) {
		// TODO Auto-generated method stub
		return 0;
	}

}
