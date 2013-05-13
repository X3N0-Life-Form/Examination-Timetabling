package struct;

import java.io.Serializable;
import java.util.Date;

public class Period  implements Comparable<Period>, Serializable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cost;
		result = prime * result
				+ ((date_hour == null) ? 0 : date_hour.hashCode());
		result = prime * result + duration;
		result = prime * result + id;
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
		Period other = (Period) obj;
		if (cost != other.cost)
			return false;
		if (date_hour == null) {
			if (other.date_hour != null)
				return false;
		} else if (!date_hour.equals(other.date_hour))
			return false;
		if (duration != other.duration)
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	private Date date_hour;
	public Date getDate_hour() {
		return date_hour;
	}
	public void setDate_hour(Date date_hour) {
		this.date_hour = date_hour;
	}
	
	private int duration;
	private int cost;

	public Period(int id, Date date_hour, int duration, int cost) {
		this.id = id;
		this.date_hour = date_hour;
		this.duration = duration;
		this.cost = cost;
	}

	

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Period [id=" + id + ", date_hour=" + date_hour + ", duration="
				+ duration + ", cost=" + cost + "]";
	}

	/**
	 * Compares the date.
	 */
	@Override
	public int compareTo(Period period) {
		return this.date_hour.compareTo(period.date_hour);
	}
}
