package struct;

import java.util.Date;

public class Period  implements Comparable<Period> {
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
		return "Period [date_hour=" + date_hour + ", duration="
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
