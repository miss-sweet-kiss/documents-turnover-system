package logic;

import java.util.List;
import java.util.ArrayList;

public class CatalogTree {
	private Catalog catalog;
	private List<CatalogTree> children;
	
	public CatalogTree(Catalog catalog) {
        this(catalog, new ArrayList<CatalogTree>());
    }
	
    public CatalogTree(Catalog catalog, List<CatalogTree> children) {
        setCatalog(catalog);
        setChildren(children);
    }
    
    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }
    
    public void setChildren(List<CatalogTree> children) {
        this.children = children;
    }
    
    public List<CatalogTree> getChildren() {
        return children;
    }
    
    public Catalog getCatalog() {
        return catalog;
    }
    
    public void addChild(CatalogTree tree) {
        children.add(tree);
    }
    
    public String toString() {
		return catalog.getName();
	}
}
