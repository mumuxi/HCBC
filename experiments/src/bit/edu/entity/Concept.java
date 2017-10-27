package bit.edu.entity;

import java.util.Vector;

public class Concept {

	public Concept() {
		Id = new ConIndexPair();
		objSet = new Vector<Integer>();
		attrSet = new Vector<Integer>();
		parentConcept = new Vector<ConIndexPair>();
		childConcept = new Vector<ConIndexPair>();
	}
	
	public Concept(Vector<Integer> attr, Vector<Integer> obj) {
		Id = new ConIndexPair();
		this.objSet = obj;
		this.attrSet = attr;
		parentConcept = new Vector<ConIndexPair>();
		childConcept = new Vector<ConIndexPair>();
	}
	
	@SuppressWarnings("unchecked")
	public Concept clone() {
		Concept con = new Concept();
		con.setId(this.getId());
		con.setAttrSet((Vector<Integer>) this.getAttrSet().clone());
		con.setObjSet((Vector<Integer>) this.getObjSet().clone());
		con.setChildConcept((Vector<ConIndexPair>) this.getChildConcept().clone());
		con.setParentConcept((Vector<ConIndexPair>) this.getParentConcept().clone());
		return con;
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

	public Vector<ConIndexPair> getParentConcept() {
		return parentConcept;
	}

	public void setParentConcept(Vector<ConIndexPair> parentConcept) {
		this.parentConcept = parentConcept;
	}

	public Vector<ConIndexPair> getChildConcept() {
		return childConcept;
	}

	public void setChildConcept(Vector<ConIndexPair> childConcept) {
		this.childConcept = childConcept;
	}

	public ConIndexPair getId() {
		return Id;
	}

	public void setId(ConIndexPair id) {
		Id = id;
	}

//	public String toString() {
//		String obj = "";
//		String attr = "";
//		for(int i = 0;i < this.objSet.size();i++) {
//			if(obj.trim().equals(""))
//				obj += this.objSet.get(i);
//			else
//				obj += "_" + this.objSet.get(i);
//		}
//		for(int i = 0;i < this.attrSet.size();i++) {
//			if(attr.trim().equals(""))
//				attr += this.attrSet.get(i);
//			else
//				attr += "_" + this.attrSet.get(i);
//		}
//		return "objSet=" + obj + ", attrSet="
//		+ attr;
//	}
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Concept other = (Concept) obj;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		return true;
	}

	private ConIndexPair Id;
	private Vector<Integer> objSet;
	private Vector<Integer> attrSet;
	private Vector<ConIndexPair> parentConcept;
	private Vector<ConIndexPair> childConcept;
}
