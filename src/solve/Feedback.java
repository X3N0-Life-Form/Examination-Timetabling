package solve;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import struct.Exam;
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

	public static final String EXCLUSION_VIOLATION = "EXCLUSION violation: two mutually " +
			"exclusive exams are in the same period";

	public static final String EXAM_COINCIDENCE_VIOLATION = "EXAM_COINCIDENCE violation: " +
			"two coinciding exams are in different periods";

	public static final String AFTER_VIOLATION = "AFTER violation: one exam should be " +
			"after another, but isn't";
	
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

	@Override
	public String toString() {
		String s = "Feedback [items=";
		Set<ResultCouple> keys = items.keySet();
		for (ResultCouple item : keys) {
			s += "\n\t" + items.get(item);
			if (items.get(item).equals(ROOM_SIZE_VIOLATION)) {
				int totalSize = 0;
				for (Exam exam : item.getExamList()) {
					totalSize += exam.getSize();
				}
				s += "; total size=" + totalSize;
			}
			s += " ==> " + item;
		}
		s += "\n]";
		return s;
		/**/
		//return "Feedback [items=" + items + "]";
	}
}
