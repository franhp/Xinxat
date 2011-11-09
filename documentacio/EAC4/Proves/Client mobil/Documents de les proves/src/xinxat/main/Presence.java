/**
 * Presence.java
 *
 * Created on 28-oct-2011, 15:42:36
 * @author Hector Costa Guzman
 */

package xinxat.main;

public class Presence {
	
	//variables del objeto
	public String from;
	public String show;
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String status;
	
	//constructor
	public Presence(String from, String show, String status){
		this.from = from;
		this.show = show;
		this.status = status;
	}
	
}
