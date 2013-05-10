package util;

import struct.Solution;

/**
 * Contains various cost-calculation-related methods.
 * @author Sara Tari - Adrien Droguet
 * @see Solution
 */
public class CostCalculator {
	
	/**
	 * Calculates how much a Solution costs.
	 * @param s
	 * @return The cost of the Solution according to its soft constraints.
	 */
	public static int calculateCost(Solution s) {
		int res = 0;
		
		res += calculateTwoInARow(s);
		res += calculateTwoInADay(s);
		res += calculatePeriodSpread(s);
		res += calculateNonMixedDuration(s);
		res += calculateRoomCost(s);
		res += calculatePeriodCost(s);
		
		return res;
	}

	public static int calculatePeriodCost(Solution s) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int calculateRoomCost(Solution s) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int calculateNonMixedDuration(Solution s) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int calculatePeriodSpread(Solution s) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int calculateTwoInADay(Solution s) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int calculateTwoInARow(Solution s) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
