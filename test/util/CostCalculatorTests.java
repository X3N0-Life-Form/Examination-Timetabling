package util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import solve.HCS_serializeSolutions;
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
		s_set1 = Serialization.loadSolution(HCS_serializeSolutions.set1SerializedName);
		s_set2 = Serialization.loadSolution(HCS_serializeSolutions.set2SerializedName);
		s_set3 = Serialization.loadSolution(HCS_serializeSolutions.set3SerializedName);
		s_set4 = Serialization.loadSolution(HCS_serializeSolutions.set4SerializedName);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
