package cl.citiaps.dashboard.eda;

public class User {
	private String id;
	private String firstName;
	private String lastName;
	private String userName;
	
	public User(){
		setId("");
		setFirstName("");
		setLastName("");
		setUserName("");
	}
	
	public User(String id, String firstName, String lastName, String userName){
		setId(id);
		setFirstName(firstName);
		setLastName(lastName);
		setUserName(userName);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
