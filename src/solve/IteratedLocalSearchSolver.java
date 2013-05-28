package solve;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import struct.EMoveType;
import struct.EPeriodHardConstraint;
import struct.Exam;
import struct.Move;
import struct.PeriodHardConstraint;
import struct.ResultCouple;
import struct.Solution;
import util.CostCalculator;
import util.Debugging;
import util.Moving;
import util.Solving;

/**
 * Soft Constraint Solver using an iterated local search algorithm
 * @author Adrien Droguet - Sara Tari
 *
 */
public class IteratedLocalSearchSolver extends SoftConstraintSolver implements Runnable {
	
	private Solution originalSolution;
	private int mainLoopCounter = -1;
	private Calendar startTime;
	
	private int stopCounter;
	/**
	 * In milliseconds. Default is two hours
	 */
	private int stopTime = 7200000;//now that's badass
	
	private HardConstraintsValidator HCV;
	private int lowestCost;
	/**
	 * Contains applied Moves & the Solution they've been applied to.
	 */
	private TreeMap<Move, Solution> appliedMoves;

	public IteratedLocalSearchSolver(Solution originalSolution) {
		super();
		this.originalSolution = originalSolution;
		HCV = new HardConstraintsValidator();
		appliedMoves = new TreeMap<Move, Solution>();
	}

	public Solution getOriginalSolution() {
		return originalSolution;
	}

	public void setOriginalSolution(Solution originalSolution) {
		this.originalSolution = originalSolution;
	}

	static class OurThreadInfo {
		static List<Solution> solutions;
		static Solution currentSolution;
		static int currentCost;
		static List<Move> currentMoves;
		static EMoveType moveType;
	}

	@Override
	public void run() {
		try {
			doMoves(OurThreadInfo.solutions,
					OurThreadInfo.currentSolution,
					OurThreadInfo.currentMoves,
					OurThreadInfo.moveType);
		} catch (SolvingException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public Solution solve() throws SolvingException {
		System.out.println("Beginning iterated local search solving");
		if (!areStopConditionsSet())
			throw new SolvingException("Stop conditions were not set");
		Solution previousSolution = new Solution(originalSolution);
		lowestCost = CostCalculator.calculateCost(originalSolution);
		List<Solution> solutions = new LinkedList<Solution>();
		startTime = Calendar.getInstance();
		Feedback feedback = new Feedback();
		if (!HCV.isSolutionValid(previousSolution, feedback)) {
			System.out.println(feedback);
			throw new SolvingException("The provided Solution is invalid.");
		}
		
		System.out.println("--Beginning main loop: startTime=" + startTime);
		System.out.println("--Stop conditions:");
		if (stopCounter > 0)
			System.out.println("----stopCounter=" + stopCounter);
		if (stopTime > 0)
			System.out.println("----stopTime=" + stopTime);
		
		//Thread fred = new Thread(this);
		
		while (updateMainLoopStatus()) {
			Solution currentSolution = new Solution(previousSolution);
			CostCalculator.calculateCost(currentSolution);
			List<Move> currentMoves = new ArrayList<Move>();
			System.out.println("--Lowest cost=" + lowestCost);
			
			//prepare for thread execution
			OurThreadInfo.solutions = solutions;
			OurThreadInfo.currentCost = lowestCost;
			OurThreadInfo.currentSolution = currentSolution;
			OurThreadInfo.currentMoves = currentMoves;
			OurThreadInfo.moveType = EMoveType.SWAP;
			//fred.start();//TODO:thread stuff
			
			//TODO: some randomness, skip exams or hjvo
			//try to move each exam
			int sSize = solutions.size();
			doMoves(solutions, currentSolution, currentMoves, EMoveType.SINGLE_MOVE);
			doMoves(solutions, currentSolution, currentMoves, EMoveType.SWAP);
			
			/*
			try {
				fred.join();
			} catch (InterruptedException e) {
				throw new SolvingException("Thread interrupted: " + e.getMessage());
			}*/
			
			System.out.println("----Finished looping through the exam list - analyzing");
			
			if (sSize == solutions.size()) {
				System.out.println("----No valid Solution was added - making a random move");
				previousSolution = doRandomMove(solutions, currentSolution, currentMoves);
				//TODO: failsafe if no other move can be done
			} else {
				System.out.println("----Sorting the Solutions");
				Collections.sort(solutions);
				
				System.out.println("----Verifying the validity of each Solution");
				for (Solution cs : solutions) {
					Feedback f = new Feedback();
					if (!HCV.isSolutionValid(cs, f)) {
						System.err.println(f);
						throw new SolvingException("Found an invalid Solution.");
					}
				}
				
				previousSolution = solutions.get(0);
				System.out.println("----Found the least costly Solution - moving to next iteration");
			}
		}
		System.out.println("--Exiting main loop");
		
		Solution cheapestSolution = solutions.get(0);
		
		if (cheapestSolution.equals(originalSolution))
			System.err.println("--Warning: result Solution is identical to original Solution");
		System.out.println("--Original Solution cost=\t" + originalSolution.getCost());
		System.out.println("--Final Solution cost=\t\t" + cheapestSolution.getCost());
		//TODO:print improvement rate
		System.out.println("--List of solution costs:");
		for (Solution solution : solutions) {
			System.out.println(solution.getCost());
		}
		return cheapestSolution;
	}

	/**
	 * Does a random move operation.
	 * @param solutions
	 * @param currentSolution
	 * @param currentMoves
	 * @return A new Solution, or null.
	 * @throws SolvingException
	 */
	public Solution doRandomMove(List<Solution> solutions,
			Solution currentSolution, List<Move> currentMoves) throws SolvingException {
		Move move;
		for (int i = 0; i < currentSolution.getExamSession().getExams().size(); i++) {
			Random rand = new Random();
			int examId = rand.nextInt(currentSolution.getExamSession().getExams().size());
			System.out.println("----Processing exam " + examId
					+ " (loop " + (mainLoopCounter + 1) + "/" + stopCounter + " - random move)");
			
			move = null;
			Solution currentCopy = new Solution(currentSolution);

			if (rand.nextBoolean()) {
				move = doSingleMove(examId, currentCopy, currentMoves, true);
			} else {
				move = doSwap(examId, currentCopy, currentMoves, true);
			}
			
			if (move == null) {
				System.out.println("------No valid move found - moving on");
				continue;
			}
			
			//force save the move
			saveMoveAndSolution(solutions, currentCopy, currentMoves, move, currentSolution, true);
			return currentCopy;
		}
		return null;
	}

	/**
	 * Loop through the exams & do the specified move type.
	 * @param solutions
	 * @param currentSolution
	 * @param currentCost
	 * @param currentMoves
	 * @param moveType
	 * @throws SolvingException
	 */
	public void doMoves(List<Solution> solutions,
			Solution currentSolution, List<Move> currentMoves,
			EMoveType moveType) throws SolvingException {
		Move move;
		for (Integer examId : currentSolution.getExamSession().getExams().navigableKeySet()) {
			System.out.println("----Processing exam " + examId
					+ " (loop " + (mainLoopCounter + 1) + "/" + stopCounter + " - " + moveType + ")");
			
			move = null;
			Solution currentCopy = new Solution(currentSolution);
			/////////////////
			// single move //
			/////////////////
			if (moveType == EMoveType.SINGLE_MOVE) {
				move = doSingleMove(examId, currentCopy, currentMoves, false);
			}
			
			//////////
			// swap //
			//////////
			if (moveType == EMoveType.SWAP) {
				move = doSwap(examId, currentCopy, currentMoves, false);
			}
			
			//////////////////////////////////
			// still null --> found nothing //
			//////////////////////////////////
			if (move == null) {
				System.out.println("------No valid move found - moving on");
				continue;
			}
			
			saveMoveAndSolution(solutions, currentCopy, currentMoves, move, currentSolution, false);
		}
	}

	/**
	 * Does a swap movement operation.
	 * @param examId
	 * @param currentSolution
	 * @param currentMoves
	 * @param randomSwap If set to true, makes a random valid swap.
	 * @return A valid Move, or null.
	 * @throws SolvingException 
	 */
	public Move doSwap(Integer examId, Solution currentSolution,
			List<Move> currentMoves, boolean randomSwap) throws SolvingException {
		ResultCouple swapTarget = null;
		Move move = null;
		int swapId = -1;
		try {
			if (!randomSwap) {
				swapId = lookForSwapTarget(examId, currentSolution, currentMoves);
			} else {
				swapId = lookForRandomSwapTarget(examId, currentSolution, currentMoves);
			}
			swapTarget = currentSolution.getResultForExam(swapId);
		} catch (MovingException e) {
			throw new SolvingException("Invalid examId provided to IteratedLocalSearchSolver.lookForSwapTarget:" + examId);
		}
		if (swapId != -1) {
			System.out.println("------Found a swap target: " +
					"examId=" + swapId
					+ "; periodId=" + swapTarget.getPeriod().getId()
					+ "; roomId=" + swapTarget.getRoom().getId());
			
			move = Moving.swapExams(examId, swapId, currentSolution);
			// DEBUG //
			Feedback f = new Feedback();
			if (Debugging.debug && !HCV.isSolutionValid(currentSolution, f)) {
				System.err.println("------" + f);
				throw new SolvingException("Swap error.");
			}
			// END DEBUG //
		}
		return move;
	}

	/**
	 * Does a single move operation.
	 * @param examId
	 * @param currentSolution
	 * @param currentMoves
	 * @param randomMove If true, makes a random valid Move.
	 * @return A valid Move, or null.
	 * @throws SolvingException
	 */
	public Move doSingleMove(int examId, Solution currentSolution, List<Move> currentMoves, boolean randomMove) throws SolvingException {
		ResultCouple moveTarget = null;
		Move move = null;
		try {
			if (!randomMove) {
				moveTarget = lookForMoveTarget(examId, currentSolution, currentMoves);
			} else {
				moveTarget = lookForRandomMoveTarget(examId, currentSolution, currentMoves);
			}
		} catch (MovingException e) {
			throw new SolvingException("Invalid examId provided to IteratedLocalSearchSolver.lookForMoveTarget:" + examId);
		}
		if (moveTarget != null) {
			System.out.println("------Found a move target: periodId="
					+ moveTarget.getPeriod().getId()
					+ "; roomId=" + moveTarget.getRoom().getId());
			move = Moving.movingSingleExam(examId, currentSolution, moveTarget.getPeriod().getId(), moveTarget.getRoom().getId());
			// DEBUG //
			Feedback f = new Feedback();
			if (Debugging.debug && !HCV.isSolutionValid(currentSolution, f)) {
				System.err.println("------" + f);
				throw new SolvingException("Move error.");
			}
			// END DEBUG //
		}
		return move;
	}

	/**
	 * Saves the provided move, and potentially the resulting Solution.
	 * @param solutions List to add the Solution into.
	 * @param newSolution
	 * @param lowestCost
	 * @param currentMoves List to add the move into.
	 * @param move
	 * @param previousSolution
	 * @param forceSave Forcibly save the move, even if it's inefficient.
	 * @return True if the Solution was saved.
	 */
	public synchronized boolean saveMoveAndSolution(List<Solution> solutions,
			Solution newSolution, List<Move> currentMoves,
			Move move, Solution previousSolution, boolean forceSave) {
		boolean added = false;
		Feedback feedback;
		System.out.println("------Saving tested move");
		//if the move has never been applied, no chance of a duplicate in move lists
		if (!appliedMoves.containsKey(move)) {
			currentMoves.add(move);
		}
		//exam moved --> save solution if lower cost
		if (CostCalculator.calculateCost(newSolution) < lowestCost) {
			System.out.println("------Cost inferior to lowest Solution ("
					+ newSolution.getCost() + " - " + lowestCost + ")");
			feedback = new Feedback();
			if (Debugging.debug && HCV.isSolutionValid(newSolution, feedback)) {
				System.out.println("------Solution is valid - saving");
				added = true;
			} else if (!Debugging.debug) {
				System.out.println("------Saving");
				added = true;
			} else {
				System.out.println("------Solution is invalid - ignoring");
				System.err.println("------" + feedback);
			}
		} else {
			System.out.println("------Cost superior to previous Solution - ignoring");
		}
		
		if (added || forceSave) {
			solutions.add(newSolution);
			appliedMoves.put(move, previousSolution);
			lowestCost = newSolution.getCost();
		}
		
		return added || forceSave;
	}

	/**
	 * Test whether the stop conditions are set or not.
	 * @return True if at least one of the stop conditions is set.
	 */
	public boolean areStopConditionsSet() {
		return (stopCounter > 0
				|| stopTime > 0);
	}

	/**
	 * Sets the stop conditions.
	 * @param stopCounter Number of loop. Negative value to disable.
	 * @param stopTime Run time, in milliseconds. Negative value to disable.
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
		mainLoopCounter++;
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
		//ResultCouple origin = null;
		int examId = -1;
		int examTargetId = -1;
		switch (move.getType()) {
		case SINGLE_MOVE:
			target = move.getTargets().get(0);
			examId = move.getExamIds().get(0);
			Exam exam = s.getExamSession().getExams().get(examId);
			
			if (/*target.getPeriod().getId() != origin.getPeriod().getId()//TODO:this
					&& */exam.hasPeriodHardConstraint(EPeriodHardConstraint.EXAM_COINCIDENCE)) {
				return false;
			} else if (exam.hasPeriodHardConstraint(EPeriodHardConstraint.AFTER) && !checkAfter(examId, target, s)) {
				return false;
			} else if (!Solving.canHost(s, examId, target.getPeriod().getId(), s.getResult())) {
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
			} else if ((target.getTotalSize() + s.getExamSession().getExams().get(examId).getSize()) > target.getRoom().getSize()) {
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
	
	/**
	 * Check an exam's AFTER constraints regarding a specified target ResultCouple.
	 * @param examId
	 * @param target
	 * @param s
	 * @return True if the exam can be put in the RC.
	 */
	public boolean checkAfter(int examId, ResultCouple target, Solution s) {
		Exam exam = s.getExamSession().getExams().get(examId);
		for (PeriodHardConstraint phc : exam.getConstraints()) {
			if (phc.getConstraint() == EPeriodHardConstraint.AFTER) {
				int beforeId = phc.getE2Id();
				int afterId = phc.getE1Id();
				for (Exam et : target.getExamList()) {
					if (et.getId() == beforeId || et.getId() == afterId) {
						return false;
					}
				}
				if (beforeId != examId
						&& s.getResultForExam(beforeId).getPeriod().getId() >= target.getPeriod().getId()) {
					return false;
				} else if (afterId != examId
						&& s.getResultForExam(afterId).getPeriod().getId() <= target.getPeriod().getId()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public ResultCouple lookForMoveTarget(int examId, Solution s, List<Move> previousMoves) throws MovingException {
		ResultCouple target = null;
		ResultCouple origin = s.getResultForExam(examId);
		if (previousMoves == null) {
			previousMoves = new ArrayList<Move>();
		} else if (origin == null) {
			throw new MovingException("Invalid examId:" + examId);
		}
		for (ResultCouple rc : s.getResult()) {
			Move simulatedMove = new Move(EMoveType.SINGLE_MOVE, examId, origin, rc);
			if (previousMoves.contains(simulatedMove)) {
				continue;
			}
			if (isMoveValid(simulatedMove, s)) {
				target = rc;
				break;
			}
		}
		return target;
	}
	
	public ResultCouple lookForRandomMoveTarget(int examId,
			Solution currentSolution, List<Move> previousMoves) throws MovingException {
		ResultCouple target = null;
		ResultCouple origin = currentSolution.getResultForExam(examId);
		
		if (previousMoves == null) {
			previousMoves = new ArrayList<Move>();
		} else if (origin == null) {
			throw new MovingException("Invalid examId:" + examId);
		}
		
		Random rand = new Random();
		//don't loop forever
		for (int i = 0; i < currentSolution.getResult().size(); i++) {
			int periodId = rand.nextInt(currentSolution.getExamSession().getPeriods().size());
			int roomId = rand.nextInt(currentSolution.getExamSession().getRooms().size());
			ResultCouple rc = currentSolution.getSpecificResult(periodId, roomId);
			//copied from regular move method
			Move simulatedMove = new Move(EMoveType.SINGLE_MOVE, examId, origin, rc);
			if (appliedMoves.containsKey(simulatedMove)) {
				continue;
			}
			if (isMoveValid(simulatedMove, currentSolution)) {
				target = rc;
				break;
			}
		}
		
		return target;
	}
	
	@SuppressWarnings("unused")
	@Override
	public List<ResultCouple> lookForMoveTargets(List<Integer> examIds, Solution s, List<Move> previousMoves) throws MovingException {
		if (true)
			throw new MovingException("Not yet implemented.");
		return null;
	}

	@Override
	public int lookForSwapTarget(int examId, Solution s, List<Move> previousMoves) throws MovingException {
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		if (previousMoves == null) {
			previousMoves = new ArrayList<Move>();
		}
		ResultCouple origin = s.getResultForExam(examId);
		if (origin == null) {
			throw new MovingException("Invalid examId:" + examId);
		}
		
		for (ResultCouple rc : s.getResult()) {
			for (Exam currentExam : rc.getExamList()) {
				int targetId = currentExam.getId();
				if (targetId != examId) {
					examIds.add(targetId);
				} else {
					continue;
				}
				
				Move simulatedMove = new Move(EMoveType.SWAP, examIds, origin, rc);
				if (isMoveValid(simulatedMove, s)) {
					return targetId;
				} else {
					examIds.remove(1);
				}
			}
		}
		return -1;
	}
	
	public int lookForRandomSwapTarget(Integer examId,
			Solution currentSolution, List<Move> previousMoves) throws MovingException {
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		if (previousMoves == null) {
			previousMoves = new ArrayList<Move>();
		}
		ResultCouple origin = currentSolution.getResultForExam(examId);
		if (origin == null) {
			throw new MovingException("Invalid examId:" + examId);
		}
		
		
		Random rand = new Random();
		//don't loop forever
		for (int i = 0; i < currentSolution.getResult().size(); i++) {
			int targetId = rand.nextInt(currentSolution.getExamSession().getExams().size());
			if (targetId != examId) {
				examIds.add(targetId);
			} else {
				continue;
			}
			ResultCouple rc = currentSolution.getResultForExam(targetId);
			
			Move simulatedMove = new Move(EMoveType.SWAP, examIds, origin, rc);
			
			if (appliedMoves.containsKey(simulatedMove)) {
				continue;
			}
			if (isMoveValid(simulatedMove, currentSolution)) {
				return targetId;
			} else {
				examIds.remove(1);
			}
		}
		
		return -1;
		
	}
	


}
