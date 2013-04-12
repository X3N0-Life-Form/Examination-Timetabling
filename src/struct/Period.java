package struct;

import java.util.Date;

public class Period {
	private Date date_hour;
	private int duration;
	private int cost;
	//TODO: make comparable according to ???

	public Period(Date date_hour, int duration, int cost) {
		this.date_hour = date_hour;
		this.duration = duration;
		this.cost = cost;
	}

	public Date getDate_hour() {
		return date_hour;
	}

	public void setDate_hour(Date date_hour) {
		this.date_hour = date_hour;
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
}
