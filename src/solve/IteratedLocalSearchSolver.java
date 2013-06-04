package solve;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	private Map<Move, Solution> appliedMoves;
	
	/**
	 * Number of time you can ignore a move before forcing a random move.
	 * If set to 0, makes only random moves.
	 */
	protected int ignoreThreshold = 400;
	
	/**
	 * Number of random moves that will be performed if the ignoreThreshold is crossed (or set to 0).
	 * Default is 500.
	 */
	protected int randomMoveThreshold = 500;
	private double temperature;
	protected static double defaultTemperature = 1;
	private Move lastRandomMove;

	public IteratedLocalSearchSolver(Solution originalSolution) {
		super();
		this.originalSolution = originalSolution;
		HCV = new HardConstraintsValidator();
		appliedMoves = new HashMap<Move, Solution>();
		temperature = defaultTemperature;
		lastRandomMove = null;
	}

	public int getRandomThreshold() {
		return randomMoveThreshold;
	}

	public void setRandomThreshold(int randomThreshold) {
		this.randomMoveThreshold = randomThreshold;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public static double getDefaultTemperature() {
		return defaultTemperature;
	}

	public static void setDefaultTemperature(double defaultTemperature) {
		IteratedLocalSearchSolver.defaultTemperature = defaultTemperature;
	}

	public int getIgnoreThreshold() {
		return ignoreThreshold;
	}

	/**
	 * If set to 0, makes only random moves.
	 * @param ignoreThreshold Number of ignore before starting to make random moves.
	 */
	public void setIgnoreThreshold(int ignoreThreshold) {
		this.ignoreThreshold = ignoreThreshold;
	}

	public Solution getOriginalSolution() {
		return originalSolution;
	}

	public void setOriginalSolution(Solution originalSolution) {
		this.originalSolution = originalSolution;
	}

	public Map<Move, Solution> getAppliedMoves() {
		return appliedMoves;
	}

	public void setAppliedMoves(Map<Move, Solution> appliedMoves) {
		this.appliedMoves = appliedMoves;
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
			//TODO:flush the list, keep local optimum
			
			//try to move each exam
			boolean thresholdCrossed = false;
			int sSize = solutions.size();
			if (ignoreThreshold > 0) {
				thresholdCrossed = doMoves(solutions, currentSolution, currentMoves, EMoveType.SINGLE_MOVE);
				if (!thresholdCrossed)
					thresholdCrossed = doMoves(solutions, currentSolution, currentMoves, EMoveType.SWAP);
			} else {
				thresholdCrossed = true;
			}
			
			/*
			try {
				fred.join();
			} catch (InterruptedException e) {
				throw new SolvingException("Thread interrupted: " + e.getMessage());
			}*/
			
			if (thresholdCrossed) {
				System.out.println("----Crossed the ignore threshold - doing random moves");
				simulatedAnnealing(previousSolution, solutions,
						currentSolution, currentMoves);
			}
			//TODO:new stop condition: stop after added a certain number of solutions has been added
			System.out.println("----Finished looping through the exam list - analyzing");
			if (sSize == solutions.size()) {
				System.out.println("----No valid Solution was added - making random moves");
				simulatedAnnealing(previousSolution, solutions,	currentSolution, currentMoves);
				Collections.sort(solutions);
				
				for (int i = 0; i < solutions.size(); i++) {
					//don't pick the previous solution again
					if (!solutions.get(i).equals(previousSolution)) {
						previousSolution = solutions.get(i);
						break;
					}
				}
				
			} else {
				System.out.println("----Sorting the Solutions");
				Collections.sort(solutions);
				
				if (Debugging.debug == true) {
					verifySolutions(solutions);
				}
				
				previousSolution = solutions.get(0);
				System.out.println("----Found the least costly Solution - moving to next iteration");
			}
		}
		System.out.println("--Exiting main loop");
		verifySolutions(solutions);
		
		Solution cheapestSolution = solutions.get(0);
		
		if (cheapestSolution.equals(originalSolution))
			System.err.println("--Warning: result Solution is identical to original Solution");
		System.out.println("--Original Solution cost=\t" + originalSolution.getCost());
		System.out.println("--Final Solution cost=\t\t" + cheapestSolution.getCost());
		double rate = (double) cheapestSolution.getCost() / (double) originalSolution.getCost();
		rate *= 100.0;
		rate = 100.0 - rate;
		System.out.println("--Reduced cost by " + rate + "%");
		System.out.println("--List of solution costs: (" + solutions.size() + " solutions)\n");
		for (Solution solution : solutions) {
			System.out.print(solution.getCost() + "\t");
		}
		return cheapestSolution;
	}

	/**
	 * Verifies the validity of each solutions in the specified list.
	 * @param solutions
	 * @throws SolvingException If a solution is invalid.
	 */
	public void verifySolutions(List<Solution> solutions)
			throws SolvingException {
		System.out.println("----Verifying the validity of each Solution");
		for (Solution cs : solutions) {
			Feedback f = new Feedback();
			if (!HCV.isSolutionValid(cs, f)) {
				System.err.println(f);//TODO:return a list of valid solutions?
				throw new SolvingException("Found an invalid Solution.");
			}
		}
	}

	/**
	 * Makes random moves & save them using a simulated annealing algorithm.
	 * @param previousSolution
	 * @param solutions
	 * @param currentSolution
	 * @param currentMoves
	 * @throws SolvingException
	 */
	public void simulatedAnnealing(Solution previousSolution,
			List<Solution> solutions, Solution currentSolution,
			List<Move> currentMoves) throws SolvingException {
		for (int i = 0; i < randomMoveThreshold; i++) {
			Solution randomSolution = doRandomMove(solutions, currentSolution, currentMoves, false);
			int delta = randomSolution.getCost() - currentSolution.getCost();
			if (saveOrNot(delta)) {
				System.out.println("------Saving move");
				saveMoveAndSolution(solutions, randomSolution, currentMoves, lastRandomMove, previousSolution, true);
			} else {
				System.out.println("------Ignoring move");
			}
			temperature *= 0.99;
			System.out.println("------Current temperature = " + temperature
					+ "(move " + i + "/" + randomMoveThreshold + ")");
		}
	}

	/**
	 * Ponders whether to save this move according to the current temperature.
	 * @param delta nuCost - originalCost
	 * @return True if thy Move should be saved.
	 */
	public boolean saveOrNot(int delta) {
		if (delta < 0) {
			return true;
		} else if (temperature == 0) {
			return false;
		} else {
			double probability = Math.exp(-delta / temperature);
			if (Math.random() <= probability) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Does a random move operation.
	 * @param solutions
	 * @param currentSolution
	 * @param currentMoves
	 * @param forceSave Always saves the Solution if true. Otherwise it's up to the user.
	 * @return A new Solution, or null.
	 * @throws SolvingException
	 */
	public Solution doRandomMove(List<Solution> solutions,
			Solution currentSolution, List<Move> currentMoves, boolean forceSave) throws SolvingException {
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
			if (forceSave) {
				saveMoveAndSolution(solutions, currentCopy, currentMoves, move, currentSolution, true);
			}
			lastRandomMove = move;
			return currentCopy;
		}
		return null;
	}

	/**
	 * Loop through the exams & do the specified move type.
	 * @param solutions
	 * @param currentSolution
	 * @param currentMoves
	 * @param moveType
	 * @return True if the ignore threshold has been crossed.
	 * @throws SolvingException
	 */
	public boolean doMoves(List<Solution> solutions,
			Solution currentSolution, List<Move> currentMoves,
			EMoveType moveType) throws SolvingException {
		Move move;
		int ignoreCount = 0;
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
			
			boolean saved = saveMoveAndSolution(solutions, currentCopy, currentMoves, move, currentSolution, false);
			if (!saved) {
				ignoreCount++;
			}
			if (ignoreCount >= ignoreThreshold) {
				return true;
			}
		}
		return false;
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
		} else if (forceSave) {
			System.out.println("------Force save enabled - saving");
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
	
	/**
	 * Looks for a random valid swap target that has not been applied yet.
	 * @param examId
	 * @param currentSolution
	 * @param previousMoves
	 * @return A valid exam target id.
	 * @throws MovingException
	 */
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
