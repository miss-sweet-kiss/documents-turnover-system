package logic;

import db.Accessor;

public class Admin {
	private Integer idAdmin;
	private String name;
	private String login;
	private String pass;
	private static String driverName = "¿Õﬁ“ ¿-œ \\SQLEXPRESS1";
	private static String dataBaseName = "ProjectDB";
	
	public Admin() {
		this.idAdmin = null;
		this.name = null;
		this.login = null;
		this.pass = null;
	}
	
	public Admin(Integer id, String name, String login, String pass) {
		this.idAdmin = id;
		this.name = name;
		this.login = login;
		this.pass = pass;
	}
	
	public void setIdAdmin(Integer id) {
		this.idAdmin = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	public void setPassword(String pass) {
		this.pass = pass;
	}
	
	public Integer getIdAdmin() {
		return idAdmin;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return pass;
	}
	
	public static Admin authorization(String login, String pass) {
		Accessor ac;
		Admin admin = null;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				admin = ac.authorization(login, pass);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return admin;
	}
	
	public static Admin changeLogin(Admin ad, String login) {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				ad = ac.changeLogin(ad, login);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ad;
	}
	
	public static Admin changeName(Admin ad, String name) {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				ad = ac.changeName(ad, name);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ad;
	}
	
	
	public static Admin changePassword(Admin ad, String login) {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				ad = ac.changePassword(ad, login);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ad;
	}
	
	public String toString() {
		return name + " " + login;
	}
}
