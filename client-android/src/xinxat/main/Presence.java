package xinxat.main;
/*
 * Presence.java
 */
/**
 * @author Procastinadors
 */
public class Presence {
	
	//Variables de l'objete
	public String from;
	public String show;
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return
	 */
	public String getShow() {
		return show;
	}

	/**
	 * @param show
	 */
	public void setShow(String show) {
		this.show = show;
	}

	/**
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	public String status;
	
	//constructor
	/**
	 * @param from
	 * @param show
	 * @param status
	 */
	public Presence(String from, String show, String status){
		this.from = from;
		this.show = show;
		this.status = status;
	}
	
}
