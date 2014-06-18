package logic;

import java.util.ArrayList;

import db.Accessor;

public class Log {
	private Integer idLog;
	private String document;
	private String docType;
	private String action;
	private Date date;
	private static String driverName = "¿Õﬁ“ ¿-œ \\SQLEXPRESS1";
	private static String dataBaseName = "ProjectDB";
	
	public Log() {
		this.idLog = null;
		this.document = null;
		this.docType = null;
		this.action = null;
		this.date = null;
	}

	public Log(Integer id, String doc, String type, String action, Date date) {
		this.idLog = id;
		this.document = doc;
		this.docType = type;
		this.action = action;
		this.date = date;
	}
	
	public void setIdLog(Integer id) {
		this.idLog = id;
	}
	public void setDocument(String doc) {
		this.document = doc;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setDocType(String type) {
		this.docType = type;
	}
	
	public Integer getIdLog() {
		return idLog;
	}
	public String getDocument() {
		return document;
	}
	public String getAction() {
		return action;
	}
	public Date getDate() {
		return date;
	}
	public String getDocType() {
		return docType;
	}
	
	public static boolean addLog(Log log) {
		Accessor ac;
		Boolean b = false;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				b = ac.addLog(log);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return b;
	}
	
	public static ArrayList<Log> getLog() {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				ArrayList<Log> logs = ac.getLog();
				return logs;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static boolean clearLog() {
		Accessor ac;
		Boolean b = false;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				b = ac.clearLog();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return b;
	}
	
	public static boolean deleteLog(Log log) {
		Accessor ac;
		Boolean b = false;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				b = ac.deleteLog(log);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return b;
	}
	
	public String toString() {
		return document + " " + action + " " + date;
	}

}
