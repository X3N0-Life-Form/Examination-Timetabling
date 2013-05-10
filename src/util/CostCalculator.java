package util;

import struct.Period;
import struct.Room;
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
		int cost = 0;
		for (int i = 0; i<s.getResult().size();i++){
			Period currentPeriod = s.getResult().get(i).getPeriod();
			if (currentPeriod.getCost()> 0){
				int examListSize = s.getResult().get(i).getExamList().size();
				cost += currentPeriod.getCost() * examListSize;
			}
		}
		return cost;
	}

	public static int calculateRoomCost(Solution s) {
		int cost = 0;
		for (int i = 0 ; i< s.getResult().size();i++){
			Room currentRoom = s.getResult().get(i).getRoom();
			if (currentRoom.getCost() > 0){
				int examListSize = s.getResult().get(i).getExamList().size();
				cost += currentRoom.getCost() * examListSize;
			}
		}
		return cost;
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
