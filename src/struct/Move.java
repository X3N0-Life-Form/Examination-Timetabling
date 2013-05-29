package struct;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains data regarding a move (single move, swap, multiple exam moves, ...)
 * @author Adrien Droguet - Sara Tari
 * @see Moving
 * @see EMoveType
 */
public class Move {

	/**
	 * List of the exams involved.
	 */
	private List<Integer> examIds;
	/**
	 * Where the exams came from.
	 */
	private List<ResultCouple> origins;
	/**
	 * Where they are going.
	 */
	private List<ResultCouple> targets;
	private EMoveType type;
	
	/**
	 * Generic all-purpose builder.
	 * @param type
	 * @param examIds
	 * @param origins
	 * @param targets
	 */
	public Move(EMoveType type, List<Integer> examIds, List<ResultCouple> origins, List<ResultCouple> targets) {
		this.type = type;
		this.examIds = examIds;
		this.origins = origins;
		this.targets = targets;
	}
	
	/**
	 * Builder dedicated to moves involving a single exam.
	 * @param type
	 * @param examId
	 * @param origin
	 * @param target
	 */
	public Move(EMoveType type, int examId, ResultCouple origin, ResultCouple target) {
		this.type = type;
		examIds = new ArrayList<Integer>();
		examIds.add(examId);
		origins = new ArrayList<ResultCouple>();
		origins.add(origin);
		targets = new ArrayList<ResultCouple>();
		targets.add(target);
	}
	
	/**
	 * Builder dedicated to moves involving multiple exams, but a single point of origin
	 * and a single destination (such as a swap move). 
	 * @param type
	 * @param examIds examIds[0] = origin id; examIds[1] = target id
	 * @param origin
	 * @param target
	 */
	public Move(EMoveType type, List<Integer> examIds, ResultCouple origin, ResultCouple target) {
		this.type = type;
		this.examIds = examIds;
		origins = new ArrayList<ResultCouple>();
		origins.add(origin);
		targets = new ArrayList<ResultCouple>();
		targets.add(target);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((examIds == null) ? 0 : examIds.hashCode());
		result = prime * result + ((origins == null) ? 0 : origins.hashCode());
		result = prime * result + ((targets == null) ? 0 : targets.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		if (examIds == null) {
			if (other.examIds != null)
				return false;
		} else if (!examIds.equals(other.examIds))
			return false;
		if (origins == null) {
			if (other.origins != null)
				return false;
		} else if (!origins.equals(other.origins))
			return false;
		if (targets == null) {
			if (other.targets != null)
				return false;
		} else if (!targets.equals(other.targets))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public List<Integer> getExamIds() {
		return examIds;
	}

	public void setExamIds(List<Integer> examIds) {
		this.examIds = examIds;
	}

	public List<ResultCouple> getOrigins() {
		return origins;
	}

	public void setOrigins(List<ResultCouple> origins) {
		this.origins = origins;
	}

	public List<ResultCouple> getTargets() {
		return targets;
	}

	public void setTargets(List<ResultCouple> targets) {
		this.targets = targets;
	}

	public EMoveType getType() {
		return type;
	}

	public void setType(EMoveType type) {
		this.type = type;
	}
}
