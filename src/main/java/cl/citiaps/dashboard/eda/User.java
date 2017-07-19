package cl.citiaps.dashboard.eda;

public class User {
	private String username;
	private Long misiones;

	public User(String username, Long misiones) {
		this.setUsername(username);
		this.setMisiones(misiones);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getMisiones() {
		return misiones;
	}

	public void setMisiones(Long misiones) {
		this.misiones = misiones;
	}

}
