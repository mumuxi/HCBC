package bit.edu.entity;

import java.util.Vector;

public class ConceptInt {

	public ConceptInt() {
		Id = 0;
		objSet = new Vector<Integer>();
		attrSet = new Vector<Integer>();
		parentConcept = new Vector<Integer>();
		childConcept = new Vector<Integer>();
	}
	
	public ConceptInt(Vector<Integer> attr, Vector<Integer> obj) {
		Id = 0;
		this.objSet = obj;
		this.attrSet = attr;
		parentConcept = new Vector<Integer>();
		childConcept = new Vector<Integer>();
	}
	
	public Vector<Integer> getObjSet() {
		return objSet;
	}
	
	public void setObjSet(Vector<Integer> objSet) {
		this.objSet = objSet;
	}

	public Vector<Integer> getAttrSet() {
		return attrSet;
	}

	public void setAttrSet(Vector<Integer> attrSet) {
		this.attrSet = attrSet;
	}

	public Vector<Integer> getParentConcept() {
		return parentConcept;
	}

	public void setParentConcept(Vector<Integer> parentConcept) {
		this.parentConcept = parentConcept;
	}

	public Vector<Integer> getChildConcept() {
		return childConcept;
	}

	public void setChildConcept(Vector<Integer> childConcept) {
		this.childConcept = childConcept;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	@Override
	public String toString() {
		String obj = "";
		String attr = "";
		String parent = "";
		String child = "";
		for(int i = 0;i < this.objSet.size();i++) {
			if(obj.trim().equals(""))
				obj += this.objSet.get(i);
			else
				obj += "_" + this.objSet.get(i);
		}
		for(int i = 0;i < this.attrSet.size();i++) {
			if(attr.trim().equals(""))
				attr += this.attrSet.get(i);
			else
				attr += "_" + this.attrSet.get(i);
		}
		for(int i = 0;i < this.parentConcept.size();i++) {
			if(parent.trim().equals(""))
				parent += this.parentConcept.get(i);
			else
				parent += "_" + this.parentConcept.get(i);
		}
		for(int i = 0;i < this.childConcept.size();i++) {
			if(child.trim().equals(""))
				child += this.childConcept.get(i);
			else
				child += "_" + this.childConcept.get(i);
		}
		return "Concept [Id=" + Id + ", objSet=" + obj + ", attrSet="
				+ attr + ", parentConcept=" + parent
				+ ", childConcept=" + child + "]";
	}
	
	private int Id;
	private Vector<Integer> objSet;
	private Vector<Integer> attrSet;
	private Vector<Integer> parentConcept;
	private Vector<Integer> childConcept;
}
