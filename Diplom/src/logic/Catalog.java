package logic;

import java.io.File;
import java.util.ArrayList;

import db.Accessor;

public class Catalog {
	private Integer idCatalog;
	private String name;	
	private Integer idParent;
	private Integer level;
	private Integer children;
	private Integer docs;
	private static String driverName = "¿Õﬁ“ ¿-œ \\SQLEXPRESS1";
	private static String dataBaseName = "ProjectDB";
	
	public Catalog() {
		this.idCatalog = null;
		this.name = null;
		this.idParent = 0;
		this.level = 0;
		this.children = 0;
		this.docs = 0;
	}
	
	public Catalog(Integer id, String name, Integer parent, Integer level, Integer child, Integer docs) {
		this.idCatalog = id;
		this.name = name;
		this.idParent = parent;
		this.level = level;
		this.children = child;
		this.docs = docs;
	}
	
	public void setIdCatalog(Integer id) {
		this.idCatalog = id;
	}	
	public void setName(String name) {
		this.name = name;
	}	
	public void setIdParent(Integer id) {
		this.idParent = id;
	}	
	public void setLevel(Integer level) {
		this.level = level;
	}	
	public void addLevel() {
		this.level += 1;
	}	
	public void addChild() {
		this.children += 1;
	}	
	public void addDocument() {
		this.docs += 1;
	}	
	public void subLevel() {
		this.level -= 1;
	}	
	public void subChild() {
		this.children -= 1;
	}
	public void subDocument() {
		this.docs -= 1;
	}
	public Integer getIdCatalog() {
		return idCatalog;
	}	
	public String getName() {
		return name;
	}	
	public Integer getIdParent() {
		return idParent;
	}		
	public Integer getLevel() {
		return level;
	}	
	public Integer getChildren() {
		return children;
	}	
	public Integer getDocs() {
		return docs;
	}
	
	public static int addCatalog(String folderName, String currentCatalog) {
		Accessor ac;
		int i = 5;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				Catalog catalog = new Catalog();
				catalog.setIdParent(ac.getCatalogIdByName(currentCatalog));
				catalog.setName(folderName);
				catalog.setLevel(ac.getCatalogLevelByName(currentCatalog)+1);
				i = ac.addCatalog(catalog);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return i;
	}
	
	public static ArrayList<Catalog> getCatalogsByParent(Integer id) {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				ArrayList<Catalog> cat = ac.getCatalogsByParent(id);
				return cat;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static int getMaxLevel() {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				int level = ac.getMaxLevel();
				return level;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return 0;
	}
	
	public static int getCatalogIdByName(String name) {
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				int id = ac.getCatalogIdByName(name);
				return id;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return 0;
	}
	
	public static int deleteCatalog(String name) {
		int i = 0;
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				i = ac.deleteCatalog(name);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return i;
	}
	
	public static String getPath(String name) {
		String path = "";
		Accessor ac;
		try {
			ac = Accessor.getInstance(driverName, dataBaseName);
			if(ac!=null) {
				Catalog catalog = ac.getCatalogByName(name);
				int level = catalog.getLevel();
				for(int i = 0;i < level;i++) {
					path = ac.getCatalogParent(catalog) + File.separator + path;
					catalog = ac.getCatalogByName(ac.getCatalogParent(catalog));
				}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return path + name + File.separator;
	}
	
	public String toString() {
		return name;
	}
}
