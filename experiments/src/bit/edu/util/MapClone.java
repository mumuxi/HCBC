package bit.edu.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bit.edu.entity.Concept;

public class MapClone {
	public static Map<Integer, List<Concept>> clone(Map<Integer, List<Concept>> map) {
		Map<Integer, List<Concept>> des = new HashMap<Integer, List<Concept>>();
		for (Iterator<Integer> it = map.keySet().iterator(); it.hasNext();) {
			Integer key = it.next();
			List<Concept> value = map.get(key);
			List<Concept> list = new ArrayList<Concept>();
			int size = value.size();
			for(int i = 0;i < size;i++) {
				list.add(value.get(i).clone());
			}
			des.put(key, list);
		}
		return des;
	}
}
