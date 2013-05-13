package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import struct.Exam;
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
		res += calculateFrontLoad(s);
		
		return res;
	}

	public static int calculateFrontLoad(Solution s) {
		int cost = 0;
		int periodLimit = s.getExamSession().getPeriods().size()-1 - s.getExamSession().getInstitutionalWeightings().getFrontLoad_2();
		
		for (int i = 0 ; i < s.getBiggerExams().size();i++ ){
			boolean found = false;
			int currentBig = s.getBiggerExams().get(i);
			for (int j = 0 ; j < s.getResult().size(); j++){
				for (int k = 0 ; k < s.getResult().get(j).getExamList().size() ; k++){
					Exam current = s.getResult().get(j).getExamList().get(k);
					if ( current.getId() == currentBig){
						found = true;
						if (s.getResult().get(j).getPeriod().getId() >= periodLimit){
							cost += s.getExamSession().getInstitutionalWeightings().getFrontLoad_3();
						}
						break;
					}
					if (found == true)
						break;
				}
				if (found == true)
					break;
			}
		}
		
		return cost;
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
		int cost = 0;		
		for (int i = 0 ; i < s.getResult().size();i++){
			
			TreeMap<Integer, Integer> durations = new TreeMap<Integer, Integer>(); 
			int size = s.getResult().get(i).getExamList().size();
			for (int j = 0 ; j < size; j++){
				if(durations.containsKey(s.getResult().get(i).getExamList().get(j).getDuration())){
					int count = durations.get(s.getResult().get(i).getExamList().get(j).getDuration());
					count++;
					durations.put(s.getResult().get(i).getExamList().get(j).getDuration(), count);
				}
				else
					durations.put(s.getResult().get(i).getExamList().get(j).getDuration(), 1);
			}
			int biggest = -1;
			for (int key : durations.navigableKeySet()){
				if (durations.get(key) > biggest){
					biggest = durations.get(key);
				}
			}
			cost += (size - biggest) * s.getExamSession().getInstitutionalWeightings().getNonMixedDurations();
		}		
		return cost;
	}
	
	public static int calculatePeriodSpread(Solution s) {
		//TODO
		int cost = 0;
		
		return cost;
	}

	public static int calculateTwoInADay(Solution s) {
		int cost = 0;
		for (int i = 0 ; i < s.getStudentList().size(); i++){
			for (int j = 0; j < s.getStudentList().get(i).getExamRes().size(); j++){
				Date currentDate = s.getStudentList().get(i).getExamRes().get(j).getPeriod().getDate_hour();
				for (int k = 0; k < s.getStudentList().get(i).getExamRes().size() ;k++){
					Date secondDate =  s.getStudentList().get(i).getExamRes().get(k).getPeriod().getDate_hour();
					if (j != k){
						if (currentDate.compareTo(secondDate) == 0){
							cost += s.getExamSession().getInstitutionalWeightings().getTwoInADay();
						}
						// TODO find how to compare dates 
						/*
						else if (){
							
						}*/
					}
					
				}
			}
		}
		return cost;
	}

	public static int calculateTwoInARow(Solution s) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
