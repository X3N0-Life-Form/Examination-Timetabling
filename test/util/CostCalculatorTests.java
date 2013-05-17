package util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import struct.Solution;

public class CostCalculatorTests {
	
	private static Solution s_set1;
	private static Solution s_set2;
	private static Solution s_set3;
	private static Solution s_set4;
	
	/**
	 * Loads up the serialized Solution objects. 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws FileNotFoundException, ClassNotFoundException, IOException {
		s_set1 = Serialization.loadSolution(Serialization.set1SerializedName);
		s_set2 = Serialization.loadSolution(Serialization.set2SerializedName);
		s_set3 = Serialization.loadSolution(Serialization.set3SerializedName);
		s_set4 = Serialization.loadSolution(Serialization.set4SerializedName);
	}

	/**
	 * Simply runs the various methods, to see if they crash. Using set4.
	 */
	@Test
	public void test_checkForRuntimeErrors() {
		Solution s = s_set3;
		int frontLoad = CostCalculator.calculateFrontLoad(s);
		int NMD = CostCalculator.calculateNonMixedDuration(s);
		int periodCost = CostCalculator.calculatePeriodCost(s);
		int periodSpread = CostCalculator.calculatePeriodSpread(s);
		int roomCost = CostCalculator.calculateRoomCost(s);
		int TID = CostCalculator.calculateTwoInADay(s);
		int TIR = CostCalculator.calculateTwoInARow(s);
		System.out.println("frontLoad=\t" + frontLoad);
		System.out.println("NMD=\t\t" + NMD);
		System.out.println("periodCost=\t" + periodCost);
		System.out.println("periodSpread=\t" + periodSpread);
		System.out.println("roomCost=\t" + roomCost);
		System.out.println("TID=\t\t" + TID);
		System.out.println("TIR=\t\t" + TIR);
	}
	

}
