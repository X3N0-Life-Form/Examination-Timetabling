package util;

import java.util.ArrayList;
import java.util.List;

import struct.Exam;
import struct.ResultCouple;

/**
 * More stupid cloning stuff
 * @author Adrien Droguet - Sara Tari
 *
 */
public class OurCollections {

	/**
	 * Manually clones a list and its contents.
	 * @param resIn
	 * @return A list containing clones of resIn's elements.
	 */
	public static ArrayList<ResultCouple> manualClone(List<ResultCouple> resIn) {
		ArrayList<ResultCouple> res = new ArrayList<ResultCouple>();
		for (ResultCouple toClone : resIn) {
			res.add(toClone.clone());
		}//manual cloning - lol
		return res;
	}

	public static ArrayList<Exam> manualCloneExam(List<Exam> resIn) {
		ArrayList<Exam> res = new ArrayList<Exam>();
		for (Exam toClone : resIn) {
			res.add(toClone.clone());
		}//manual cloning - lol
		return res;
	}

}
