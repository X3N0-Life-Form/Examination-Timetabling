package solve;

import java.util.HashMap;
import java.util.Map;

import struct.ResultCouple;

/**
 * This class stores any anomaly found during constraint checking.
 * @author Adrien Droguet
 * @see HardConstraintsSolver
 */
public class Feedback {
	public static final String ROOM_EXLUSIVE_VIOLATION =
			"ROOM_EXCLUSIVE violation: cannot have several exams in a room " +
			"if one of them has a ROOM_EXCLUSIVE constraint";
	
	public static final String DURATION_VIOLATION =
			"Duration violation: cannot have an exam with a duration longer" +
			" than the period's";

	public static final String ROOM_SIZE_VIOLATION =
			"Room size violation: the sum of exam sizes cannot exceed the " +
			"room's size";
	
	Map<ResultCouple, String> items;
	
	public Feedback() {
		items = new HashMap<ResultCouple, String>();
	}
	
	public void addItem(ResultCouple resultCouple, String explanation) {
		items.put(resultCouple, explanation);
	}
	
	public Map<ResultCouple, String> getItems() {
		return items;
	}
}
