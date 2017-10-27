package bit.edu.entity;

public class ConIndexPair {
	int indexOfMap;//�ȣ���
	int indexOfBucket;
	
	public ConIndexPair() {
		
	}
	
	public ConIndexPair(int map, int bucket) {
		this.indexOfMap = map;
		this.indexOfBucket = bucket;
	}
	
	public int getIndexOfMap() {
		return indexOfMap;
	}
	
	public void setIndexOfMap(int indexOfMap) {
		this.indexOfMap = indexOfMap;
	}
	
	public int getIndexOfBucket() {
		return indexOfBucket;
	}
	
	public void setIndexOfBucket(int indexOfBucket) {
		this.indexOfBucket = indexOfBucket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + indexOfBucket;
		result = prime * result + indexOfMap;
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
		ConIndexPair other = (ConIndexPair) obj;
		if (indexOfBucket != other.indexOfBucket)
			return false;
		if (indexOfMap != other.indexOfMap)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "<" + indexOfMap + ","
				+ indexOfBucket + ">";
	}
}