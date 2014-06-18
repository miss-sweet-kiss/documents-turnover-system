package logic;

import java.util.ArrayList;

import db.Accessor;

public class Document {
	private Integer idDocument;
	private String name;
	private Double version;
	private String type;
	private String address;
	private Integer idTeacher;
	private Integer idDocumentList;
	private Date date = new Date();
	private Date approvalDate = new Date();
	private static String driverName = "¿Õﬁ“ ¿-œ \\SQLEXPRESS1";
	private static String dataBaseName = "ProjectDB";
	
	public Document () {
		this.idDocument = null;
		this.name = null;
		this.version = 1.0;
		this.type = null;
		this.address = null;
		this.idTeacher = null;
		this.idDocumentList = null;
		this.date = null;
		this.approvalDate = null;
	}
	
	public Document(Integer idDoc, String name, Double version, String type, String ad, Integer idT, Integer idL, Date date, Date apDate) {
		this.idDocument = idDoc;
		this.name = name;
		this.version = version;
		this.type = type;
		this.address = ad;
		this.idTeacher = idT;
		this.idDocumentList = idL;
		this.date = date;	
		this.approvalDate = apDate;
	}
	
	public void setIdDocument(Integer id) {
		this.idDocument = id;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public void setVersion(Double v) {
		this.version = v;
	}	
	public void setType(String type) {
		this.type = type;
	}	
	public void setAddress(String ad) {
		this.address = ad;
	}	
	public void setIdTeacher(Integer id) {
		this.idTeacher = id;
	}	
	public void setIdDocumentList(Integer id) {
		this.idDocumentList = id;
	}	
	public void setDate(Date date) {
		this.date = date;
	}
	public void setApprovalDate(Date date) {
		this.approvalDate = date;
	}
	
	public Integer getIdDocument() {
		return idDocument;
	}	
	public String getName() {
		return name;
	}	
	public Double getVersion() {
		return version;
	}	
	public String getType() {
		return type;
	}	
	public String getAddress() {
		return address;
	}	
	public Integer getIdTeacher() {
		return idTeacher;
	}
	public Integer getIdDocumentList() {
		return idDocumentList;
	}	
	public Date getDate() {
		return date;
	}
	public Date getApprovalDate() {
		return approvalDate;
	}
	
	public static ArrayList<Document> searchDocuments(String name, Date date, Date appDate, int teach, Integer i) {
		Accessor ac;
		ArrayList<Document> docList = null;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				docList = ac.searchDocuments(name, date, appDate, teach, i);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return docList;
	}
	
	public static ArrayList<Document> getDocumentsByCatalog(Integer id) {
		Accessor ac;
		ArrayList<Document> docList = null;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				docList = ac.getDocumentsByCatalog(id);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return docList;
	}
	
	public static ArrayList<Document> getDocumentsWithoutApproval() {
		Accessor ac;
		ArrayList<Document> docList = null;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				docList = ac.getDocumentsWithoutApproval();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return docList;
	}
	
	public static boolean isDocument(String name) {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				boolean i = ac.isDocument(name);
				return i;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}
	
	public static void deleteDocument(String name) {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				ac.deleteDocument(name);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static int addDocument(Document doc) {
		Accessor ac;
		int i = 0;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				i = ac.addDocument(doc);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return i;
	}
	
	public static Document getDocumentByName(String name) {
		Accessor ac;
		Document doc = new Document();
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				doc = ac.getDocumentByName(name);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return doc;
	}
	
	public static String getDocumentsAddress(String name) {
		Accessor ac;
		String address = null;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				address = ac.getDocumentsAddress(name);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return address;
	}
	
	public static int setApprovalDate(String name, Date date) {
		Accessor ac;
		int i = 0;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				i = ac.setApprovalDate(name, date);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return i;
	}
	
	public String toString() {
		return name + " " + version + " " + date + " " + type + " " + address;
	}

}
