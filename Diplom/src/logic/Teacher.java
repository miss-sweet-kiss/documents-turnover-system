package logic;

import java.util.ArrayList;

import db.Accessor;

public class Teacher {
	private String name;
	private Integer idTeacher;
	private static String driverName = "¿Õﬁ“ ¿-œ \\SQLEXPRESS1";
	private static String dataBaseName = "ProjectDB";
	
	public Teacher() {
		this.idTeacher = null;
		this.name = null;
	}
	
	public Teacher(Integer id, String name) {
		this.idTeacher = id;
		this.name = name;
	}
	
	public void setIdTeacher(Integer id) {
		this.idTeacher = id;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getIdTeacher() {
		return idTeacher;
	}	
	public String getName() {
		return name;
	}
	
	public static String getTeacherFIO(Integer id) {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				String fio = ac.getTeacherFIO(id);
				return fio;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static Integer getTeacherId(String fio) {
		Accessor ac;
		Integer id = 0; 
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				id = ac.getTeacherId(fio);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return id;
	}
	
	public static ArrayList<Teacher> getTeachers() {
		ArrayList<Teacher> teachers = new ArrayList<Teacher>();
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				teachers = ac.getTeachers();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return teachers;
	}
	
	public static int addTeacher(String name) {
		Teacher teacher = new Teacher();
		teacher.setName(name);
		int i = 0;
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				i = ac.addTeacher(teacher);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return i;	
	}
	
	public static int deleteTeacher(Teacher t) {
		int i = 0;
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				i = ac.deleteTeacher(t);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return i;	
	}
	
	public String toString() {
		return name;
	}
}
