package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import logic.Admin;
import logic.Catalog;
import logic.Date;
import logic.Document;
import logic.Log;
import logic.Teacher;

public class Accessor {
	private static Accessor singletonAccessor;

	private Connection con;
	private ResultSet rs;

	// ������� ����������� ��������� ������� � ����� ��
	// ����������� ���������� Exception
	private Accessor(String server, String urlDatabase) throws Exception {

		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		// ������������ ������ �����������
		String connectionString = "jdbc:odbc:Driver={SQL Server};Server="+ server + ";Database=" + urlDatabase + ";";

		// ����������� � ��
		con = DriverManager.getConnection(connectionString);

	}

	// �������� ����� ��������� ������������� ���������� ��������
	// ����������� ���������� Exception
	public static Accessor getInstance(String _driver, String _urlDatabase) throws Exception {
		if (singletonAccessor == null)
			singletonAccessor = new Accessor(_driver, _urlDatabase);
		return singletonAccessor;
	}

	// ��������� ���������� � ��
	public void closeConnection() throws SQLException {
		if (con != null)
			con.close();
	}

	void propertyStatement() throws SQLException {
		// ��������, ��������� �� ������� JDBC ��� ��� ���� ��� �������
		// TYPE_FORWARD_ONLY - ������ ������� ������������ ������ ������
		// TYPE_SCROLL_INSENSITIVE - ������ ������������ � ����� ������������,
		// ������� �� ����������
		// TYPE_SCROLL_SENSITIVE - ������ ������������ � ����� ������������,
		// ������� ���������� ��� ��������� ����� � ��

		boolean ro = con.getMetaData().supportsResultSetType(
				ResultSet.TYPE_FORWARD_ONLY);
		System.out.println("TYPE_FORWARD_ONLY - " + ro);

		ro = con.getMetaData().supportsResultSetType(
				ResultSet.TYPE_SCROLL_INSENSITIVE);
		System.out.println("TYPE_SCROLL_INSENSITIVE - " + ro);

		ro = con.getMetaData().supportsResultSetType(
				ResultSet.TYPE_SCROLL_SENSITIVE);
		System.out.println("TYPE_SCROLL_SENSITIVE - " + ro);

		// ��������, ������������ �� ������� JDBC ��� ��� ���� ����� ���������
		// �������
		// CONCUR_READ_ONLY - ������� ������ ��������
		// CONCUR_UPDATABLE - ������� ����� ��������
		ro = con.getMetaData().supportsResultSetConcurrency(
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		System.out.println("CONCUR_READ_ONLY - " + ro);

		ro = con.getMetaData().supportsResultSetConcurrency(
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		System.out.println("CONCUR_UPDATABLE - " + ro);

	}
	/************************������ � �������� Teacher******************************/
	//������� ������ ��������������
	public ArrayList<Teacher> getTeachers() throws SQLException {
		ArrayList<Teacher> teachers = new ArrayList<Teacher>();
		Teacher teach;
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Teacher");
		while (rs.next()) {
			teach = new Teacher();
			teach.setIdTeacher(rs.getInt("id_teacher"));
			teach.setName(rs.getString("FIO"));
			teachers.add(teach);
		}
		stat.close();
		return teachers;
	}
	//�������� � �� ���������� � �������������
	public int addTeacher(Teacher teacher) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT FIO FROM Teacher WHERE FIO = '" + teacher.getName() + "'");
		if (rs.next()) {
			stat.close();
			System.out.println("���������� � ������������� � ����� ������ ��� ���������� � ��!");
			return 1;
		}
		int n = stat.executeUpdate("INSERT INTO Teacher VALUES ('" + teacher.getName() + "')");
		stat.close();
		if (n > 0) {
			System.out.println("���������� � ������������� ��������� � ��!");
			return 0;
		}
		System.out.println("���������� �������� ���������� � �������������!");	
		return 2;
		
	}
	//������� �� �� ���������� � �������������
	public int deleteTeacher(Teacher teacher) throws SQLException {
		Statement stat = con.createStatement();
		int n;
		rs = stat.executeQuery("SELECT FIO FROM Teacher WHERE FIO = '" + teacher.getName() + "'");
		if (rs.next()) {
			try{
				n = stat.executeUpdate("DELETE FROM Teacher WHERE FIO = '" + teacher.getName() + "'");
			} catch (SQLException e) {
				return 1;
			}
			stat.close();
			if (n > 0) {
				System.out.println("���������� � ������������� ������� �� ��!");
				return 0;
			} else {
				System.out.println("���������� ������� ���������� � ������������� �� ��!");	
				return 1;
			}
		}
		System.out.println("� �� ��� ������ � ����� �������������!");
		stat.close();
		return 2;
	}
	//������� id �������������
	public int getTeacherId(String FIO) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT id_teacher FROM Teacher WHERE FIO = '" + FIO + "'");
		if (rs.next()) {
			int id = rs.getInt("id_teacher");
			stat.close();
			return id;
		}
		stat.close();
		System.out.println("� �� ��� ������ � ����� �������������!");
		return 0;
	}
	//������� ��� �������������
	public String getTeacherFIO(int id) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT FIO FROM Teacher WHERE id_teacher = " + id);
		if (rs.next()) {
			String fio = rs.getString("FIO");
			stat.close();
			return fio;
		}
		stat.close();
		System.out.println("� �� ��� ������ � ����� �������������!");
		return null;
	}
	/************************������ � �������� Admin******************************/
	//����������� ��������������
	public Admin authorization(String login, String pass) throws SQLException {
		Admin admin = new Admin();
		admin.setLogin(login);
		admin.setPassword(pass);
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT name FROM Admin WHERE login ='"+login+"' and password ='"+pass+"'");
		if (rs.next()) {
			admin.setName(rs.getString("name"));
			stat.close();
			//System.out.println("����� ����������!" + admin.getName());
			return admin;
		}
		stat.close();
		//System.out.println("�������� ����� �/��� ������!");
		return null;
	}
	
	public Admin changeLogin(Admin ad, String login) throws SQLException {
		Statement stat = con.createStatement();
		int n = stat.executeUpdate("UPDATE Admin SET login = '" + login + "' WHERE name = '" + ad.getName() + "'");
		if (n > 0) {
			ad.setLogin(login);
			stat.close();
			return ad;
		}
		stat.close();
		return null;
	}
	
	public Admin changePassword(Admin ad, String pass) throws SQLException {
		Statement stat = con.createStatement();
		int n = stat.executeUpdate("UPDATE Admin SET password = '" + pass + "' WHERE name = '" + ad.getName() + "'");
		if (n > 0) {
			ad.setPassword(pass);
			stat.close();
			return ad;
		}
		stat.close();
		return null;
	}
	
	public Admin changeName(Admin ad, String name) throws SQLException {
		Statement stat = con.createStatement();
		int n = stat.executeUpdate("UPDATE Admin SET name = '" + name + "' WHERE name = '" + ad.getName() + "'");
		if (n > 0) {
			ad.setName(name);
			stat.close();
			return ad;
		}
		stat.close();
		return null;
	}
	
	
	/************************������ � �������� Document****************************/
	public Document getDocumentByName(String name) throws SQLException {
		Document doc = new Document();
		Date date;
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Document WHERE name = '" + name + "'");
		while (rs.next()) {
			date = new Date();
			doc.setIdDocument(rs.getInt("id_document"));
			doc.setName(rs.getString("name"));
			doc.setVersion(rs.getDouble("version"));
			doc.setType(rs.getString("type"));
			doc.setAddress(rs.getString("address"));
			doc.setIdTeacher(rs.getInt("ref_teacher"));
			doc.setIdDocumentList(rs.getInt("ref_catalog"));
			date.setDay(rs.getInt("day"));
			date.setMonth(rs.getString("month"));
			date.setYear(rs.getInt("year"));
			doc.setDate(date);
		}
		stat.close();
		//System.out.println(docList);
		return doc;
	}
	public String getDocumentsAddress(String name) throws SQLException {
		String address = null;
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT address FROM Document WHERE name = '" + name + "'");
		while (rs.next()) {
			address = rs.getString("address");
		}
		stat.close();
		return address;
	}
	//�������� �� ���� ����������
	public boolean isDocument(String name) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT name FROM Document WHERE name='" + name + "'");
		if (rs.next()) {
			stat.close();
			return true;
		}
		stat.close();
		return false;
	}
	//�������� � �� ���������� � ���������
	public int addDocument(Document document) throws SQLException {
		Double version = getLastDocumentVersion(document.getName());
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT name FROM Document WHERE name='"+document.getName()+"'");
		if (rs.next()) {
			stat.executeUpdate("UPDATE Document SET version = " + (version+1) + " WHERE name = '"+document.getName()+"'");
			stat.close();
			return 1;
		}
		int n = stat.executeUpdate("INSERT INTO Document VALUES ('"+document.getName()+"',"+document.getVersion()+",'"+document.getType()+"','"+document.getAddress()+"',"+document.getIdTeacher()+","+document.getIdDocumentList()+","+document.getDate().getDay()+",'"+document.getDate().getMonth()+"',"+document.getDate().getYear()+",null,null,null)");
		stat.close();
		if (n > 0) {
			changeDoc(document.getIdDocumentList(), 1);//�������� � ������� �������� �� ������� � ��� ����������
			System.out.println("���������� � ��������� ��������� � ��!");
			return 0;
		}
		System.out.println("���������� �������� ���������� � ���������!");	
		return 2;
		
	}	
	//������� id ���������
	public int getDocumentId(String name) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT id_document FROM Document WHERE name='"+name+"'");
		if (rs.next()) {
			int id = rs.getInt("id_document");
			stat.close();
			return id;
		}
		stat.close();
		System.out.println("� �� ��� ������ � ����� ���������!");
		return 0;
	}
	//������� ��������� ������ ���������
	public double getLastDocumentVersion(String name) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT MAX(version) as max_ver FROM Document WHERE name='"+name+"'");
		if (rs.next()) {
			double max = rs.getDouble("max_ver");
			stat.close();
			return max;
		}
		stat.close();
		System.out.println("� �� ��� ������ � ����� ���������!");
		return 1;
	}
	//������� ������ ���������� �� �������������
	public int setApprovalDate(String name, Date date) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Document WHERE name = '" + name + "'");
		if (rs.next()) {
			stat.executeUpdate("UPDATE Document SET approv_day="+date.getDay()+", approv_month='"+date.getMonth()+"', approv_year = "+date.getYear()+" WHERE name = '"+name+"'");
			stat.close();
			return 1;
		}
		stat.close();
		return 0;
	}
	//������� ������ ���������� �� ��������
	public ArrayList<Document> getDocumentsByCatalog(int id) throws SQLException {
		ArrayList<Document> docList = new ArrayList<Document>();
		Document doc;
		Date date, apprDate;
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Document WHERE ref_catalog = " + id);
		while (rs.next()) {			
			doc = new Document();
			date = new Date();
			apprDate = new Date();
			doc.setIdDocument(rs.getInt("id_document"));
			doc.setName(rs.getString("name"));
			doc.setVersion(rs.getDouble("version"));
			doc.setType(rs.getString("type"));
			doc.setAddress(rs.getString("address"));
			doc.setIdTeacher(rs.getInt("ref_teacher"));
			doc.setIdDocumentList(rs.getInt("ref_catalog"));
			date.setDay(rs.getInt("day"));
			date.setMonth(rs.getString("month"));
			date.setYear(rs.getInt("year"));
			apprDate.setDay(rs.getInt("approv_day"));
			apprDate.setMonth(rs.getString("approv_month"));
			apprDate.setYear(rs.getInt("approv_year"));
			doc.setDate(date);
			doc.setApprovalDate(apprDate);
			docList.add(doc);
		}
		stat.close();
		return docList;
	}
	public ArrayList<Document> getDocumentsWithoutApproval() throws SQLException {
		ArrayList<Document> docList = new ArrayList<Document>();
		Document doc;
		Date date, apprDate;
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Document WHERE approv_day is NULL and approv_month is NULL and approv_year is NULL");
		while (rs.next()) {			
			doc = new Document();
			date = new Date();
			apprDate = new Date();
			doc.setIdDocument(rs.getInt("id_document"));
			doc.setName(rs.getString("name"));
			doc.setVersion(rs.getDouble("version"));
			doc.setType(rs.getString("type"));
			doc.setAddress(rs.getString("address"));
			doc.setIdTeacher(rs.getInt("ref_teacher"));
			doc.setIdDocumentList(rs.getInt("ref_catalog"));
			date.setDay(rs.getInt("day"));
			date.setMonth(rs.getString("month"));
			date.setYear(rs.getInt("year"));
			apprDate.setDay(rs.getInt("approv_day"));
			apprDate.setMonth(rs.getString("approv_month"));
			apprDate.setYear(rs.getInt("approv_year"));
			doc.setDate(date);
			doc.setApprovalDate(apprDate);
			docList.add(doc);
		}
		stat.close();
		return docList;
	}
	//����� ����������
	public ArrayList<Document> searchDocuments(String name, Date docDate, Date appDate, int teach, int i) throws SQLException {
		ArrayList<Document> docList = new ArrayList<Document>();
		Document doc = new Document();
		Date date, apprDate;
		String select = "";
		String select2 = "";
		Statement stat = con.createStatement();
		if(docDate != null && docDate.getMonth() == null && docDate.getDay() == null)
			select = "year = " + docDate.getYear();
		else if(docDate != null && docDate.getMonth() != null && docDate.getDay() == null)
			select = "year = " + docDate.getYear() + "and month = '" + docDate.getMonth() + "'";
		else if(docDate != null && docDate.getMonth() != null && docDate.getDay() != null) 
			select = "day = " + docDate.getDay() + " and month = '" + docDate.getMonth() + "' and year = " + docDate.getYear();
		
		if(appDate != null && appDate.getMonth() == null && appDate.getDay() == null)
			select2 = "approv_year = " + appDate.getYear();
		else if(appDate != null && appDate.getMonth() != null && appDate.getDay() == null)
			select2 = "approv_year = " + appDate.getYear() + "and approv_month = '" + appDate.getMonth() + "'";
		else if(appDate != null && appDate.getMonth() != null && appDate.getDay() != null) 
			select2 = "approv_day = " + appDate.getDay() + " and approv_month = '" + appDate.getMonth() + "' and approv_year = " + appDate.getYear();

		switch(i){
		case 1://���� ����� ������ �� �������� ���������
			rs = stat.executeQuery("SELECT * FROM Document WHERE name LIKE '%" + name + "%'");
			break;
		case 2://���� ����� ������ �� ����
			rs = stat.executeQuery("SELECT * FROM Document WHERE " + select);
			break;
		case 3://���� ����� �� �������������
			rs = stat.executeQuery("SELECT * FROM Document WHERE ref_teacher = " + teach );
			break;
		case 4:
			rs = stat.executeQuery("SELECT * FROM Document WHERE " + select2);
			break;
		case 5://���� ����� �� �������� � ����
			rs = stat.executeQuery("SELECT * FROM Document WHERE name LIKE '%" + name + "%' and " + select);
			break;
		case 6://���� ����� �� �������� � �������������
			rs = stat.executeQuery("SELECT * FROM Document WHERE name LIKE '%" + name + "%' and ref_teacher = " + teach);
			break;
		case 7:
			rs = stat.executeQuery("SELECT * FROM Document WHERE name LIKE '%" + name + "%' and " + select2);
			break;
		case 8://���� ����� �� ������������� � ����
			rs = stat.executeQuery("SELECT * FROM Document WHERE ref_teacher = " + teach + " and " + select);
			break;
		case 9:
			rs = stat.executeQuery("SELECT * FROM Document WHERE " + select + " and " + select2);
			break;
		case 10:
			rs = stat.executeQuery("SELECT * FROM Document WHERE ref_teacher = " + teach + " and " + select2);
			break;
		case 11://���� ����� �� ��������, ���� � �������������
			rs = stat.executeQuery("SELECT * FROM Document WHERE name LIKE '%" + name + "%' and ref_teacher = " + teach + " and " + select);
			break;
		case 12:
			rs = stat.executeQuery("SELECT * FROM Document WHERE name LIKE '%" + name + "%' and " + select + " and " + select2);
			break;
		case 13:
			rs = stat.executeQuery("SELECT * FROM Document WHERE name LIKE '%" + name + "%' and " + select2 + " and ref_teacher = " + teach);
			break;
		case 14:
			rs = stat.executeQuery("SELECT * FROM Document WHERE " + select + " and ref_teacher = " + teach + " and " + select2);
			break;
		case 15:
			rs = stat.executeQuery("SELECT * FROM Document WHERE name LIKE '%" + name + "%' and " + select + " and ref_teacher = " + teach + " and " + select2);
			break;
		}
		while (rs.next()) {			
			doc = new Document();
			date = new Date();
			apprDate = new Date();
			doc.setIdDocument(rs.getInt("id_document"));
			doc.setName(rs.getString("name"));
			doc.setVersion(rs.getDouble("version"));
			doc.setType(rs.getString("type"));
			doc.setAddress(rs.getString("address"));
			doc.setIdTeacher(rs.getInt("ref_teacher"));
			doc.setIdDocumentList(rs.getInt("ref_catalog"));
			date.setDay(rs.getInt("day"));
			date.setMonth(rs.getString("month"));
			date.setYear(rs.getInt("year"));
			apprDate.setDay(rs.getInt("approv_day"));
			apprDate.setMonth(rs.getString("approv_month"));
			apprDate.setYear(rs.getInt("approv_year"));
			doc.setDate(date);
			doc.setApprovalDate(apprDate);
			docList.add(doc);
		}
		stat.close();
		return docList;		
	}
	
	public int deleteDocument(String name) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Document WHERE name = '" + name + "'");
		if (rs.next()) {
			int ref_cat = rs.getInt("ref_catalog");
			int n = stat.executeUpdate("DELETE FROM Document WHERE name = '" + name + "'");
			stat.close();
			if(n > 0) {
				System.out.println("�������� ������!");
				int ch = changeDoc(ref_cat, 2);
				if(ch == 0) {
					return 0;			
				}
			}
			System.out.println("������� �� ������!");
			return 1;
		}
		stat.close();
		System.out.println("�������� �� ����������!");
		return 3;
	}
	/************************������ � �������� Catalog****************************/
	//������� id �������� �� �����
	public int getCatalogIdByName(String name) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT id_catalog FROM Catalog WHERE name = '" + name + "'");
		if (rs.next()) {
			int id_catalog = rs.getInt("id_catalog");
			stat.close();
			return id_catalog;
		}
		stat.close();
		return 0;
	}
	//������� ������� �������� �� �����
	public int getCatalogLevelByName(String name) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT level FROM Catalog WHERE name = '" + name + "'");
		if (rs.next()) {
			int level = rs.getInt("level");
			stat.close();
			return level;
		}
		stat.close();
		return -1;
	}
	//������� ������������ ������� ���������
	public int getMaxLevel() throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT MAX(level) as max_level FROM Catalog");
		if (rs.next()) {
			int max = rs.getInt("max_level");
			stat.close();
			return max;
		}
		stat.close();
		System.out.println("� �� ��� ������ � ����� ���������!");
		return 0;
	}
	//�������� � ������� ��� ������ �������
	public int changeChild(int id, int change) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT children FROM Catalog WHERE id_catalog = " + id);
		if (rs.next()) {
			int children = rs.getInt("children");
			if(change == 1)
				children += 1;
			if(change == 2)
				children -= 1;
			int n = stat.executeUpdate("UPDATE Catalog SET children = " + children + " WHERE id_catalog = " + id);
			stat.close();
			if (n > 0) {
				System.out.println("������� ��������!");
				return 0;
			}
			return 1;
		}
		stat.close();
		return 2;
	}
	//�������� � ������� ��� ���� ��������
	public int changeDoc(int id, int change)  throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT docs FROM Catalog WHERE id_catalog = " + id);
		if (rs.next()) {
			int docs = rs.getInt("docs");
			if(change == 1)
				docs += 1;
			if(change == 2)
				docs -= 1;
			int n = stat.executeUpdate("UPDATE Catalog SET docs = " + docs + " WHERE id_catalog = " + id);
			stat.close();
			if (n > 0) {
				System.out.println("�������� �������!");
				return 0;
			}
			return 1;
		}
		stat.close();
		return 2;
	}
	//�������� � �� ���������� � ��������
	public int addCatalog(Catalog catalog) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT name FROM Catalog WHERE name = '" + catalog.getName() + "'");
		if (rs.next()) {
			stat.close();
			System.out.println("���������� � �������� ��� ���������� � ��!");
			return 1;
		}
		int n = stat.executeUpdate("INSERT INTO Catalog VALUES ('"+catalog.getName()+"',"+catalog.getIdParent()+","+catalog.getLevel()+","+catalog.getChildren()+","+catalog.getDocs()+")");
		stat.close();
		if(n > 0) {
			System.out.println("���������� � �������� ��������� � ��!");
			int ch = changeChild(catalog.getIdParent(), 1);
			if(ch == 0) {
				return 0;			
			}			
		}
		return 2;		
	}
	//�������� ���������� � ��������, ���� �� ������!
	public int deleteCatalog(String name) throws SQLException {
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Catalog WHERE name = '" + name + "'");
		if (rs.next()) {
			int par = rs.getInt("parent");
			int children = rs.getInt("children");
			int docs = rs.getInt("docs");
			if(children == 0 && docs == 0) {
				int n = stat.executeUpdate("DELETE FROM Catalog WHERE name = '" + name + "'");
				stat.close();
				if(n > 0) {
					System.out.println("������� ������!");
					int ch = changeChild(par, 2);
					if(ch == 0) {
						return 0;			
					}
				}
				System.out.println("������� �� ������!");
				return 1;
			}
			System.out.println("������� �� ����!");
			return 2;
		}
		stat.close();
		System.out.println("�������� �� ����������!");
		return 3;
	}
	//getCatalogs
	public ArrayList<Catalog> getCatalogsByParent(int parent) throws SQLException {
		ArrayList<Catalog> catList = new ArrayList<Catalog>();
		Catalog catalog;
		//CatalogTree catTree = new CatalogTree();
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Catalog WHERE parent = " + parent);
		while (rs.next()) {	
			int id_catalog = rs.getInt("id_catalog");
			String name = rs.getString("name");
			int par = rs.getInt("parent");
			int level = rs.getInt("level");
			int children = rs.getInt("children");
			int docs = rs.getInt("docs");
			catalog = new Catalog(id_catalog, name, par, level, children, docs);
			catList.add(catalog);
		}
		stat.close();
		return catList;
	}
	
	public Catalog getCatalogByName(String catName) throws SQLException {
		Catalog catalog = new Catalog();
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Catalog WHERE name = '" + catName + "'");
		while (rs.next()) {	
			int id_catalog = rs.getInt("id_catalog");
			String name = rs.getString("name");
			int par = rs.getInt("parent");
			int level = rs.getInt("level");
			int children = rs.getInt("children");
			int docs = rs.getInt("docs");
			catalog = new Catalog(id_catalog, name, par, level, children, docs);
		}
		stat.close();
		return catalog;
	}
	
	public String getCatalogParent(Catalog catalog) throws SQLException {
		String path = "";
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT name FROM Catalog WHERE id_catalog = " + catalog.getIdParent());
		if(rs.next()) {
			path += rs.getString("name");
		}
		stat.close();
		return path;
	}
	/************************������ � �������� Log****************************/
	//�������� ������ � ������
	public boolean addLog(Log log) throws SQLException {
		Statement stat = con.createStatement();
		int n = stat.executeUpdate("INSERT INTO Log VALUES ('"+log.getAction()+"',"+log.getDate().getDay()+",'"+log.getDate().getMonth()+"',"+log.getDate().getYear()+",'"+log.getDocument()+"','"+log.getDocType()+"')");
		stat.close();
		if(n > 0) {
			return true;						
		}
		return false;		
	}
	//�������� ������
	public ArrayList<Log> getLog() throws SQLException {
		ArrayList<Log> logs = new ArrayList<Log>();
		Log log;
		Date date;
		Statement stat = con.createStatement();
		rs = stat.executeQuery("SELECT * FROM Log");
		while (rs.next()) {	
			log = new Log();
			date = new Date();
			log.setIdLog(rs.getInt("id_log"));
			log.setAction(rs.getString("action"));
			date.setDay(rs.getInt("day"));
			date.setMonth(rs.getString("month"));
			date.setYear(rs.getInt("year"));
			log.setDocument(rs.getString("document"));
			log.setDocType(rs.getString("document_type"));
			log.setDate(date);
			logs.add(log);
		}
		stat.close();
		return logs;	
	}
	//�������� ������
	public boolean clearLog() throws SQLException {
		Statement stat = con.createStatement();
		int n = stat.executeUpdate("DELETE FROM Log");
		if(n > 0)
			return true;
		return false;
	}
	//������� ������
	public boolean deleteLog(Log log) throws SQLException {
		Statement stat = con.createStatement();
		int n = stat.executeUpdate("DELETE FROM Log WHERE action = '"+log.getAction()+"' and day = "+log.getDate().getDay()+" and month = '"+log.getDate().getMonth()+"' and year = "+log.getDate().getYear()+" and document = '"+log.getDocument()+"'");
		if(n > 0)
			return true;
		return false;
	}

}
