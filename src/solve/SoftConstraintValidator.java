package solve;

import struct.Solution;
import util.CostCalculator;

/**
 * Generic Soft Constraint Validator.
 * @author Adrien Droguet - Sara Tari
 *
 */
public class SoftConstraintValidator implements Validator {

	private Solution originalSolution;
	
	/**
	 * 
	 * @param originalSolution
	 */
	public SoftConstraintValidator(Solution originalSolution) {
		super();
		this.originalSolution = originalSolution;
	}

	/**
	 * A Solution is valid if it costs less than the original Solution.
	 */
	@Override
	public boolean isSolutionValid(Solution s, Feedback feedback) {
		return CostCalculator.calculateCost(s) < CostCalculator.calculateCost(originalSolution);
	}

	public Solution getOriginalSolution() {
		return originalSolution;
	}

	public void setOriginalSolution(Solution originalSolution) {
		this.originalSolution = originalSolution;
	}

}
