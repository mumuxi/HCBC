package bit.edu.LOOCV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;


import bit.edu.LOOCV.latticeConstuction.GIC_AddIntent_LOOCV;

//import org.junit.Test;

import bit.edu.entity.ConIndexPair;
import bit.edu.entity.Concept;

public class MySimilarity_LOOCV {
	
	/*
	 * Vector<Integer> rowData,
			Map<Integer, List<Concept>> concepts, int conceptCount, int[] attrsCount
	 * @param rowData ƥ������ݣ�description��
	 * @param concepts  ���и�������
	 * @param objCount ��ǰ�������������ж������
	 * @param attrsCount ÿ�����Զ�Ӧ���еĶ���֧�ֶȣ����������
	 */
	public static Concept computeSimilarity(Vector<Integer> rowData, Map<Integer, List<Concept>> concepts, int objCount, int[] attrsCount) {
		Vector<Integer> vectData = rowData;
		int vectDataSize = vectData.size();
		Concept maxConcept = new Concept();
		double maxSim = 0.0;
//		for (int j = 0; j <= attrsCount.length; j++) {
//		List<Concept> list = this.concepts.get(j);
		for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter.hasNext();) {
			List<Concept> list = iter.next();
			if (list != null) {
				int listSize = list.size();
				for(int index = 0 ;index < listSize;index++) {
					Concept concept = list.get(index);
					Vector<Integer> attrVect = concept.getAttrSet();
					//����attrsWeight
					int attrVectSize = attrVect.size();
					if(attrVectSize == 0) {
						continue;
					}
//					/*
//					if(attrVectSize < 1 || attrVect.get(attrVectSize - 1) < NoClass_LOOCV.point || concept.getObjSet().size() == 0)
//						continue;
//					*/
					double[] attrsWeight = new double[attrVectSize + 1];
					for(int i = 0;i < attrVectSize + 1;i++) {
						attrsWeight[i] = 1.0;
					}
					Vector<ConIndexPair> pVect = concept.getParentConcept();
					int pSize = pVect.size();
					List<Concept> pList = new LinkedList<Concept>();
					int count = 1;
					for(int i = 0;i < pSize;i++) {
						ConIndexPair pair = pVect.get(i);
						Concept temp = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
						pList.add(temp);
						count++;
					}
					while(!pList.isEmpty()) {
						Concept temp = pList.remove(0);
						Vector<Integer> tempAttr = temp.getAttrSet();
						for(int i = 0;i < attrVectSize;i++) {
							if(tempAttr.contains(attrVect.get(i)))
								attrsWeight[i] += 1;
						}
						Vector<ConIndexPair> tempPVect = temp.getParentConcept();
						int tempPVectSize = tempPVect.size();
						for(int i = 0;i < tempPVectSize;i++) {
							ConIndexPair pair = tempPVect.get(i);
							Concept tempPConcept = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
							if(!pList.contains(tempPConcept)) {
								pList.add(tempPConcept);
								count++;
							}
						}
					}
					/*
					double[] attrsWeight = new double[attrVectSize + 1];
					for(int i = 0;i < attrVectSize + 1;i++) {
						attrsWeight[i] = 0.0;
					}
					Vector<ConIndexPair> childrenVect = concept.getChildConcept();
					int childrenSize = childrenVect.size();
					List<Concept> childrenList = new LinkedList<Concept>();
					int count = 1;
					for(int i = 0;i < childrenSize;i++) {
						ConIndexPair pair = childrenVect.get(i);
						Concept temp = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
						childrenList.add(temp);
						count++;
					}
					while(!childrenList.isEmpty()) {
						Concept temp = childrenList.remove(0);
						Vector<Integer> tempAttr = temp.getAttrSet();
						for(int i = 0;i < attrVectSize;i++) {
							if(tempAttr.contains(attrVect.get(i)))
								attrsWeight[i] += 1;
						}
						Vector<ConIndexPair> tempChildVect = temp.getChildConcept();
						int tempChildVectSize = tempChildVect.size();
						for(int i = 0;i < tempChildVectSize;i++) {
							ConIndexPair pair = tempChildVect.get(i);
							Concept tempChildConcept = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
							if(!childrenList.contains(tempChildConcept)) {
								childrenList.add(tempChildConcept);
								count++;
							}
						}
					}
					*/
					double sum = 0.0;
					for(int i = 0;i < attrVectSize;i++) {
						if(count == attrsWeight[i])
							attrsWeight[i] = 1.0 * (count + 1) / attrsWeight[i];
						else 
							attrsWeight[i] = 1.0 * count / attrsWeight[i];
						sum += attrsWeight[i];
					}
					attrsWeight[attrVectSize] = sum / attrVectSize;
					//��ʼ�������ƶ�
					double tempSim = 0.0;
					double dividend = 0.0;
					double divider1 = 0.0;
					double divider2 = 0.0;
					for(int i = 0;i < attrVectSize;i++) {
						int tempAttr = attrVect.get(i);
//						double tempD = Math.log10(attrsWeight[i] * attrsCount[tempAttr] / objCount);
						double tempD = Math.log10(attrsWeight[i] * objCount / attrsCount[tempAttr]);
						divider1 += tempD * tempD;
						if(vectData.contains(tempAttr)) {
							dividend += tempD * tempD;
						}
					}
					for(int i = 0;i < vectDataSize;i++) {
						int tempAttr = vectData.get(i);
						if(!attrVect.contains(tempAttr)) {
//							double tempD = Math.log10(attrsWeight[attrVectSize] * attrsCount[tempAttr] / objCount);
							double tempD = 0.0;
//							System.out.println(attrsWeight[attrVectSize] + " " + objCount + " " +  attrsCount[tempAttr]);
							if(attrsCount[tempAttr] == 0)
								tempD = Math.log10(attrsWeight[attrVectSize] * objCount / (attrsCount[tempAttr] + 1));
							else
								tempD = Math.log10(attrsWeight[attrVectSize] * objCount / attrsCount[tempAttr]);
							divider2 += tempD * tempD;
						}
					}
					divider2 += dividend;
//					System.out.println(divider2);
//					System.out.println(divider1);
					tempSim = dividend / Math.sqrt(divider1 * divider2);
//					System.out.println("temp" + tempSim);
					if(tempSim > maxSim) {
						maxSim = tempSim;
//						System.out.println("max" + maxSim);
						maxConcept = concept;
					}
				}
			}
		}
		return maxConcept;
	}
	
		
	
	/*
	 * Vector<Integer> rowData,
			Map<Integer, List<Concept>> concepts, int conceptCount, int[] attrsCount
	 * @param rowData ƥ������ݣ�description��
	 * @param concepts  ���и�������
	 * @param objCount ��ǰ���������ĸ���ڵ���
	 * @param attrsCount ÿ�����Զ�Ӧ���еĸ���ڵ�֧�ֶȣ����������Եĸ���ڵ������
	 */
	public static Concept computeSimilarityMod(Vector<Integer> rowData, Map<Integer, List<Concept>> concepts, int conceptCount, int[] attrsCount) {
		Vector<Integer> vectData = rowData;
		int vectDataSize = vectData.size();
		Concept maxConcept = new Concept();
		double maxSim = 0.0;
//		for (int j = 0; j <= attrsCount.length; j++) {
//		List<Concept> list = this.concepts.get(j);
		for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter.hasNext();) {
			List<Concept> list = iter.next();
			if (list != null) {
				int listSize = list.size();
				for(int index = 0 ;index < listSize;index++) {
					Concept concept = list.get(index);
					Vector<Integer> attrVect = concept.getAttrSet();
					//����attrsWeight
					int attrVectSize = attrVect.size();
//					/*
					if(attrVectSize < 1 || attrVect.get(attrVectSize - 1) < NoClass_LOOCV.point)
						continue;
//					*/
					double[] attrsWeight = new double[attrVectSize + 1];
					for(int i = 0;i < attrVectSize + 1;i++) {
						attrsWeight[i] = 0.0;
					}
					Vector<ConIndexPair> pVect = concept.getParentConcept();
					int pSize = pVect.size();
					List<Concept> pList = new LinkedList<Concept>();
					int count = 1;
					for(int i = 0;i < pSize;i++) {
						ConIndexPair pair = pVect.get(i);
						Concept temp = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
						pList.add(temp);
						count++;
					}
					while(!pList.isEmpty()) {
						Concept temp = pList.remove(0);
						Vector<Integer> tempAttr = temp.getAttrSet();
						for(int i = 0;i < attrVectSize;i++) {
							if(tempAttr.contains(attrVect.get(i)))
								attrsWeight[i] += 1;
						}
						Vector<ConIndexPair> tempPVect = temp.getParentConcept();
						int tempPVectSize = tempPVect.size();
						for(int i = 0;i < tempPVectSize;i++) {
							ConIndexPair pair = tempPVect.get(i);
							Concept tempPConcept = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
							if(!pList.contains(tempPConcept)) {
								pList.add(tempPConcept);
								count++;
							}
						}
					}
					double sum = 0.0;
					for(int i = 0;i < attrVectSize;i++) {
						attrsWeight[i] = 1.0 * count / (attrsWeight[i] + 1);
						sum += attrsWeight[i];
					}
					attrsWeight[attrVectSize] = sum / attrVectSize;
					//��ʼ�������ƶ�
					double tempSim = 0.0;
					double dividend = 0.0;
					double divider1 = 0.0;
					double divider2 = 0.0;
					for(int i = 0;i < attrVectSize;i++) {
						int tempAttr = attrVect.get(i);
						double tempD = attrsWeight[i] * attrsCount[tempAttr] / conceptCount;
						divider1 += tempD * tempD;
						if(vectData.contains(tempAttr)) {
							dividend += tempD * tempD;
						}
					}
					for(int i = 0;i < vectDataSize;i++) {
						int tempAttr = vectData.get(i);
						if(!attrVect.contains(tempAttr)) {
							double tempD = attrsWeight[attrVectSize] * attrsCount[tempAttr] / conceptCount;
							divider2 += tempD * tempD;
						}
					}
					divider2 += dividend;
					tempSim = dividend / Math.sqrt(divider1 * divider2);
					if(tempSim > maxSim) {
						maxSim = tempSim;
						maxConcept = concept;
					}
				}
			}
		}
		return maxConcept;
	}
	
	public static Concept computeSimilarityModOnly(Vector<Integer> rowData, Map<Integer, List<Concept>> concepts, int conceptCount, int[] attrsCount) {
		Vector<Integer> vectData = rowData;
		int vectDataSize = vectData.size();
		Concept maxConcept = new Concept();
		double maxSim = 0.0;
//		for (int j = 0; j <= attrsCount.length; j++) {
//		List<Concept> list = this.concepts.get(j);
		for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter.hasNext();) {
			List<Concept> list = iter.next();
			if (list != null) {
				int listSize = list.size();
				for(int index = 0 ;index < listSize;index++) {
					Concept concept = list.get(index);
					Vector<Integer> attrVect = concept.getAttrSet();
					//����attrsWeight
					int attrVectSize = attrVect.size();
					if(attrVectSize == 0 || concept.getObjSet().size() == 0)
						continue;
//					/*
//					if(attrVectSize < 1 || attrVect.get(attrVectSize - 1) < NoClass_LOOCV.point)
//						continue;
//					*/
					double[] attrsWeight = new double[attrVectSize + 1];
					for(int i = 0;i < attrVectSize + 1;i++) {
						attrsWeight[i] = 0.0;
					}
					Vector<ConIndexPair> pVect = concept.getParentConcept();
					int pSize = pVect.size();
					List<Concept> pList = new LinkedList<Concept>();
					int count = 1;
					for(int i = 0;i < pSize;i++) {
						ConIndexPair pair = pVect.get(i);
						Concept temp = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
						pList.add(temp);
						count++;
					}
					while(!pList.isEmpty()) {
						Concept temp = pList.remove(0);
						Vector<Integer> tempAttr = temp.getAttrSet();
						for(int i = 0;i < attrVectSize;i++) {
							if(tempAttr.contains(attrVect.get(i)))
								attrsWeight[i] += 1;
						}
						Vector<ConIndexPair> tempPVect = temp.getParentConcept();
						int tempPVectSize = tempPVect.size();
						for(int i = 0;i < tempPVectSize;i++) {
							ConIndexPair pair = tempPVect.get(i);
							Concept tempPConcept = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
							if(!pList.contains(tempPConcept)) {
								pList.add(tempPConcept);
								count++;
							}
						}
					}
					double sum = 0.0;
					for(int i = 0;i < attrVectSize;i++) {
						if(count == attrsWeight[i] + 1)
							attrsWeight[i] = 1.0 * (count + 1) / (attrsWeight[i] + 1);
						else
							attrsWeight[i] = 1.0 * count / (attrsWeight[i] + 1);
						sum += attrsWeight[i];
					}
					attrsWeight[attrVectSize] = sum / attrVectSize;
					//��ʼ�������ƶ�
					double tempSim = 0.0;
					double dividend = 0.0;
					double divider1 = 0.0;
					double divider2 = 0.0;
					for(int i = 0;i < attrVectSize - 1;i++) {
						int tempAttr = attrVect.get(i);
						double tempD = Math.log10(attrsWeight[i] * conceptCount / attrsCount[tempAttr]);
						divider1 += tempD * tempD;
						if(vectData.contains(tempAttr)) {
							dividend += tempD * tempD;
						}
					}
					if(attrVect.get(attrVectSize - 1) < NoClass_LOOCV.point) {
						int tempAttr = attrVect.get(attrVectSize - 1);
						double tempD = Math.log10(attrsWeight[attrVectSize - 1] * conceptCount / attrsCount[tempAttr]);
						divider1 += tempD * tempD;
						if(vectData.contains(tempAttr)) {
							dividend += tempD * tempD;
						}
					}
					for(int i = 0;i < vectDataSize;i++) {
						int tempAttr = vectData.get(i);
						if(!attrVect.contains(tempAttr)) {
							double tempD = 0.0;
							if(attrsCount[tempAttr] == 0)
								tempD = Math.log10(attrsWeight[attrVectSize] * conceptCount / (attrsCount[tempAttr] + 1));
							else 
								tempD = Math.log10(attrsWeight[attrVectSize] * conceptCount / attrsCount[tempAttr]);
							divider2 += tempD * tempD;
						}
					}
					divider2 += dividend;
					tempSim = dividend / Math.sqrt(divider1 * divider2);
					if(tempSim > maxSim) {
						maxSim = tempSim;
						maxConcept = concept;
					}
				}
			}
		}
		return maxConcept;
	}
	
	//����ɸѡ��ȡ�ȵ����
	public static Concept computeSimilarityModOnly2(Vector<Integer> rowData, Map<Integer, List<Concept>> concepts, int conceptCount, int[] attrsCount) {
		Vector<Integer> vectData = rowData;
		int vectDataSize = vectData.size();
		Concept maxConcept = new Concept();
		double maxSim = 0.0;
//		for (int j = 0; j <= attrsCount.length; j++) {
//		List<Concept> list = this.concepts.get(j);
		for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter.hasNext();) {
			List<Concept> list = iter.next();
			if (list != null) {
				int listSize = list.size();
				for(int index = 0 ;index < listSize;index++) {
					Concept concept = list.get(index);
					Vector<Integer> attrVect = concept.getAttrSet();
					//����attrsWeight
					int attrVectSize = attrVect.size();
					if(attrVectSize == 0 || concept.getObjSet().size() == 0)
						continue;
//					/*
//					if(attrVectSize < 1 || attrVect.get(attrVectSize - 1) < NoClass_LOOCV.point)
//						continue;
//					*/
					double[] attrsWeight = new double[attrVectSize + 1];
					for(int i = 0;i < attrVectSize + 1;i++) {
						attrsWeight[i] = 1.0;
					}
					
//					System.out.println(concept);
					Vector<ConIndexPair> pVect = concept.getParentConcept();
					int pSize = pVect.size();
					List<Concept> pList = new ArrayList<Concept>();
					int count = 1;
					
					for(int i = 0;i < pSize;i++) {
						ConIndexPair pair = pVect.get(i);
						Concept temp = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
						pList.add(temp);
					}
					while(!pList.isEmpty()) {
						count++;
//						System.out.println(pList);
						for(int i = 0;i < attrVectSize;i++) {
							int tempEle = attrVect.get(i);
							int pListSize = pList.size();
							for(int j = 0;j < pListSize;j++) {
								Concept tConcept = pList.get(j);
								if(tConcept.getAttrSet().contains(tempEle)) {
									attrsWeight[i]++;
									break;
								}
							}
						}
						List<Concept> pTempList = new ArrayList<Concept>();
						while(!pList.isEmpty()) {
							Concept temp = pList.remove(0);
							Vector<ConIndexPair> tempPVect = temp.getParentConcept();
							int tempPVectSize = tempPVect.size();
							for(int k = 0;k < tempPVectSize;k++) {
								ConIndexPair pair = tempPVect.get(k);
								Concept tempPConcept = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
								if(!pTempList.contains(tempPConcept)) {
									pTempList.add(tempPConcept);
								}
							}
						}
						pList = pTempList;
					}
					double sum = 0.0;
					for(int i = 0;i < attrVectSize;i++) {
						if(count == attrsWeight[i])
							attrsWeight[i] = 1.0 * (count + 1) / (attrsWeight[i]);
						else
							attrsWeight[i] = 1.0 * count / (attrsWeight[i]);
//						System.out.println("sep " + count + " " + attrsWeight[i]);
//						System.out.println(attrsWeight[i]);
						sum += attrsWeight[i];
					}
					attrsWeight[attrVectSize] = sum / attrVectSize;
//					System.out.println(attrsWeight[attrVectSize]);
					//��ʼ�������ƶ�
					double tempSim = 0.0;
					double dividend = 0.0;
					double divider1 = 0.0;
					double divider2 = 0.0;
					for(int i = 0;i < attrVectSize - 1;i++) {
						int tempAttr = attrVect.get(i);
						double tempD = Math.log10(attrsWeight[i] * conceptCount / attrsCount[tempAttr]);
						divider1 += tempD * tempD;
						if(vectData.contains(tempAttr)) {
							dividend += tempD * tempD;
						}
					}
					if(attrVect.get(attrVectSize - 1) < NoClass_LOOCV.point) {
						int tempAttr = attrVect.get(attrVectSize - 1);
						double tempD = Math.log10(attrsWeight[attrVectSize - 1] * conceptCount / attrsCount[tempAttr]);
						divider1 += tempD * tempD;
						if(vectData.contains(tempAttr)) {
							dividend += tempD * tempD;
						}
					}
					for(int i = 0;i < vectDataSize;i++) {
						int tempAttr = vectData.get(i);
						if(!attrVect.contains(tempAttr)) {
							double tempD = 0.0;
							if(attrsCount[tempAttr] == 0)
								tempD = Math.log10(attrsWeight[attrVectSize] * conceptCount / (attrsCount[tempAttr] + 1));
							else 
								tempD = Math.log10(attrsWeight[attrVectSize] * conceptCount / attrsCount[tempAttr]);
							divider2 += tempD * tempD;
						}
					}
					divider2 += dividend;
					tempSim = dividend / Math.sqrt(divider1 * divider2);
//					System.out.println(maxSim + " " + tempSim);
					if(tempSim >= maxSim) {
						maxSim = tempSim;
						maxConcept = concept;
					}
				}
			}
		}
//		System.out.println(rowData);
//		System.out.println(maxConcept.getId() + " " + maxSim);
		return maxConcept;
	}
	
	private static Map<Integer, Integer> computeIterated(Concept target, Vector<Integer> rowData, Map<Integer, List<Concept>> concepts, int conceptCount, int[] attrsCount, Map<Integer, Map<Integer, Integer>> indexMap, Map<Concept, Double> resultMap, Map<Integer, Integer> attributeHeight) {
		countOfConcepts++;
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		Vector<Integer> attrVect = target.getAttrSet();
		int rowDataSize = rowData.size();
		int attrVectSize = attrVect.size();
		Vector<ConIndexPair> pParent = target.getParentConcept();
		if(target.getAttrSet().size() == 0) {
			map.put(-1, 1);
			indexMap.put(target.hashCode(), map);
			resultMap.put(target, 0.0);
			return map;
		} else if(pParent.size() == 0){
			map.put(-1, 1);
			for(int i = 0;i < attrVectSize;i++) {
				map.put(attrVect.get(i), 1);
				attributeHeight.put(attrVect.get(i), 1);
			}
		}
		for(int i = 0;i < pParent.size();i++) {
			ConIndexPair pair = pParent.get(i);
			Concept concept = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
			Map<Integer, Integer> tempMap = indexMap.get(concept.hashCode());
			if(tempMap == null) {
				tempMap = computeIterated(concept, rowData, concepts, conceptCount, attrsCount, indexMap, resultMap, attributeHeight);
			}
			for(Map.Entry<Integer, Integer> entry : tempMap.entrySet()) {
				int key = entry.getKey();
				int value = entry.getValue();
				Integer tempValue = map.get(key);
				if(tempValue == null) {
					map.put(key, value + 1);
				} else {
					int temp = tempValue.intValue();
					if(temp <= value)
					//if(temp - 1 > value)
						map.put(key, value + 1);
				}
			}
		}
		//�˴������Ż�
		for(int i = 0;i < attrVectSize;i++) {
			int tempAttr = attrVect.get(i);
			if(map.get(tempAttr) == null) {
				map.put(tempAttr, 1);
				if(attributeHeight.containsKey(tempAttr)) {
					throw new RuntimeException(tempAttr + " should not appear here");
				} else {
					attributeHeight.put(tempAttr, map.get(-1));
				}
			}
		}
		
		//�������ƶ�
		double tempSim = 0.0;
		double dividend = 0.0;
		double divider1 = 0.0;
		double divider2 = 0.0;
		Integer bottom = map.get(-1);
		if(bottom == null) {
			throw new RuntimeException("-1 wrong");
		}
		int bot = bottom.intValue();
		double sumWeight = 0.0;
		for(int i = 0;i < attrVectSize - 1;i++) {
			int tempAttr = attrVect.get(i);
			if(map.get(tempAttr) == null)
				throw new RuntimeException("wrong at " + tempAttr);
			int tempValue = map.get(tempAttr);
			double tempD = 0.0;
			/*����bot = Dis(temp,attr)+Height(tempAttr)������bottom�ľ���*/
			/*
			bot = tempValue + attributeHeight.get(tempAttr);
			if(tempValue == bot) {
				sumWeight += 1.0 * (bot + 1) / tempValue;
				tempD = Math.log10(1.0 * (bot + 1) / tempValue * conceptCount / attrsCount[tempAttr]);
				tempD = 1.0 * (bot + 1) / tempValue * Math.log10(conceptCount / attrsCount[tempAttr]);
				tempD = Math.log(1.0 * (bot + 1) / tempValue * attrsCount[tempAttr]);
			}
			else {
				sumWeight += 1.0 * bot / tempValue;
				tempD = Math.log10(1.0 * bot / tempValue * conceptCount / attrsCount[tempAttr]);
				tempD = 1.0 * bot / tempValue * Math.log10(conceptCount / attrsCount[tempAttr]);
				tempD = Math.log(1.0 * bot / tempValue * attrsCount[tempAttr]);
			}
			*/
			int attrHeight = attributeHeight.get(tempAttr);
//			sumWeight += 1.0 * (bot + 1) / tempValue;//1
			sumWeight += 1.0 * (attrHeight + tempValue - 1) / tempValue;//2,3,5
//			sumWeight += 1.0 * attrHeight / (attrHeight + tempValue - 1);//4
			tempD = MySimilarity_LOOCV.weightFun3(tempValue, attributeHeight.get(tempAttr), bot, conceptCount, attrsCount[tempAttr]);
//			divider1 += tempD * tempD;
			divider1 += tempD;
//			divider1 += Math.sqrt(tempD);
			if(rowData.contains(tempAttr)) {
//				dividend += tempD * tempD;
				dividend += tempD;
//				sumWeight += 1.0 * (attrHeight + tempValue - 1) / tempValue;//2,3,5
//				dividend += Math.sqrt(tempD);
			}
		}
		int lab = 1;
		if(attrVect.get(attrVectSize - 1) < NoClass_LOOCV.point) {
//		if(attrVect.get(attrVectSize - 1) >= 0) {
//			lab--;
			int tempAttr = attrVect.get(attrVectSize - 1);
			if(map.get(tempAttr) == null)
				throw new RuntimeException("wrong at " + tempAttr);
			int tempValue = map.get(tempAttr);
			double tempD = 0.0;
			/*����bot = Dis(temp,attr)+Height(tempAttr)������bottom�ľ���*/
			/*
			bot = tempValue + attributeHeight.get(tempAttr);
			if(tempValue == bot) {
				sumWeight += 1.0 * (bot + 1) / tempValue;
				tempD = Math.log10(1.0 * (bot + 1) / tempValue * conceptCount / attrsCount[tempAttr]);
				tempD = Math.log(1.0 * (bot + 1) / tempValue * attrsCount[tempAttr]);
			}
			else {
				sumWeight += 1.0 * bot / tempValue;
				tempD = Math.log10(1.0 * bot / tempValue * conceptCount / attrsCount[tempAttr]);
				tempD = Math.log(1.0 * bot / tempValue * attrsCount[tempAttr]);
			}
			*/
			int attrHeight = attributeHeight.get(tempAttr);
//			sumWeight += 1.0 * (bot + 1) / tempValue;//1
			sumWeight += 1.0 * (attrHeight + tempValue - 1) / tempValue;//2,3,5
//			sumWeight += 1.0 * attrHeight / (attrHeight + tempValue - 1);//4
			tempD = MySimilarity_LOOCV.weightFun3(tempValue, attributeHeight.get(tempAttr), bot, conceptCount, attrsCount[tempAttr]);
//			if(attrVect.get(attrVectSize - 1) >= NoClass_LOOCV.point) {
//				tempD *= 2;
//			}
			//			divider1 += tempD * tempD;
			divider1 += tempD;
//			divider1 += Math.sqrt(tempD);
			if(rowData.contains(tempAttr)) {
//				dividend += tempD * tempD;
				dividend += tempD;
//				sumWeight += 1.0 * (attrHeight + tempValue - 1) / tempValue;//2,3,5
//				dividend += Math.sqrt(tempD);
			}
		}
//		else {
//			int tempAttr = attrVect.get(attrVectSize - 1);
//			if(map.get(tempAttr) == null)
//				throw new RuntimeException("wrong at " + tempAttr);
//			int tempValue = map.get(tempAttr);
////			/*����bot = Dis(temp,attr)+Height(tempAttr)������bottom�ľ���*/
////			bot = tempValue + attributeHeight.get(tempAttr);
//			int attrHeight = attributeHeight.get(tempAttr);
//			sumWeight += 1.0 * (bot + 1) / tempValue;//1
////			sumWeight += 1.0 * (attrHeight + tempValue - 1) / tempValue;//2,3,5
////			sumWeight += 1.0 * attrHeight / (attrHeight + tempValue - 1);//4
//		}
		if(attrVectSize - lab != 0) {
			sumWeight /= attrVectSize - lab;
		}
//		sumWeight *= 2;
//		sumWeight = 10;
//		sumWeight = Math.E;
//		sumWeight = 1.0;
		if(sumWeight == 0)
			sumWeight = Math.E;
		for(int i = 0;i < rowDataSize;i++) {
			int tempAttr = rowData.get(i);
			if(!attrVect.contains(tempAttr)) {
				double tempD = 0.0;
				if(attrsCount[tempAttr] == 0) {
//					tempD = Math.log10(sumWeight * conceptCount / (attrsCount[tempAttr] + 1));//1,2
					tempD = sumWeight * Math.log10(1.0 * conceptCount / (attrsCount[tempAttr] + 1));//3,4
//					tempD = Math.log1p(sumWeight) * Math.log10(1.0 * conceptCount / (attrsCount[tempAttr] + 1));//5
//					tempD = Math.log(sumWeight * attrsCount[tempAttr]);
				}
				else {
//					tempD = Math.log10(sumWeight * conceptCount / attrsCount[tempAttr]);//1,2
					tempD = sumWeight * Math.log10(1.0 * conceptCount / attrsCount[tempAttr]);//3,4
//					tempD = Math.log1p(sumWeight) * Math.log10(1.0 * conceptCount / attrsCount[tempAttr]);//5
//					tempD = Math.log(sumWeight * attrsCount[tempAttr]);
				}
//				divider2 += tempD * tempD;
				
//				System.out.println(conceptCount + " " + attrsCount[tempAttr]);
//				System.out.println(divider2);
//				divider2 += Math.sqrt(tempD);
				divider2 += tempD;
			}
		}
		divider2 += dividend;
		//���ƶȼ��㹫ʽ
		//�����ذ�
		Vector<Integer> attrVector = target.getAttrSet();
		Vector<Integer> interSet = GIC_AddIntent_LOOCV.intersect(rowData, attrVector);
		Vector<Integer> unionSet = GIC_AddIntent_LOOCV.union(
				rowData, attrVector);
		if(unionSet.size() == interSet.size()) {
			tempSim = 1;
		} else {
//			double rate2 = 1.0 * (rowData.size() - interSet.size()) / (unionSet.size() - interSet.size());
//			double rate1 = 1.0 * (attrVector.size() - interSet.size()) / (unionSet.size() - interSet.size());
//			tempSim = dividend / (dividend + (divider1 - dividend) * rate1 + (divider2 - dividend) * rate2);//1
//			
			double rate2 = Math.exp(1.0 * (rowData.size() - interSet.size()) / unionSet.size());
			double rate1 = Math.exp(1.0 * (attrVector.size() - interSet.size()) / unionSet.size());
			rate1 = 0.5;
			rate2 = 0.5;
			tempSim = dividend / (dividend + (divider1 - dividend) * rate1 / (rate1 + rate2) + (divider2 - dividend) * rate2 / (rate1 + rate2));//2
//			tempSim = dividend;
//			tempSim = dividend / (dividend + (divider1 - dividend) * rate2 / (rate1 + rate2) + (divider2 - dividend) * rate1 / (rate1 + rate2));//5similarity>1
//			tempSim = dividend / Math.sqrt(divider1 * divider2) * interSet.size() / Math.sqrt(1.0 * rowData.size() * attrVector.size());//3
//			tempSim = dividend / Math.sqrt(divider1 * divider2) * interSet.size() / unionSet.size();//3mod
//			System.out.println(interSet.size() + " " + unionSet.size());
			//paper��
//			tempSim = dividend / Math.sqrt(divider1 * divider2);//4
//			System.out.println(dividend + " " + divider1 + " " + divider2 + " " + tempSim);
		}
		double minSim = tempSim;
		Concept minConcept = null;
		if(resultMap.size() < NoClass_LOOCV.NUM_OF_CLUSTER) {
			resultMap.put(target, tempSim);
		}
		else {
			for(Map.Entry<Concept, Double> entry : resultMap.entrySet()) {
				Concept key = entry.getKey();
				Double value = entry.getValue();
				if(key == null || value == null)
					throw new RuntimeException("result restoring wrong!");
				if(value < minSim) {
					minConcept = key;
					minSim = value;
//					resultMap.remove(key);
//					resultMap.put(target, tempSim);
//					System.out.println("similarity: " + tempSim);
//					System.out.println(target.getAttrSet() + " " + rowData);
				}
			}
			if(minConcept != null) {
				resultMap.remove(minConcept);
				resultMap.put(target, tempSim);
			}
		}
		indexMap.put(target.hashCode(), map);
		return map;
	}
	
	/*
	 * tempD = Math.log10(1.0 * (bot + 1) / tempValue * conceptCount / attrsCount[tempAttr]);
	 */
	
	/**
	 * ����Ӧ����m  ���ڸ���C cToA aToT cToT��Ϊ��ӦDistance+1
	 * �������C��top�����C��m���Ը�������ֵ
	 * @param cToA distance from concept to attribute concept
	 * @param aToT distance from attribute concept to top concept
	 * @param cToT distance from concept to top concept
	 * @param amountOfL concept amount of concept lattice
	 * @param amountOfSubL concept amount of sub concept lattice
	 * @return weight
	 */
	public static double weightFun0(int cToA, int aToT, int cToT, int amountOfL, int amountOfSubL) {
		double result = 0.0;
		result = Math.log10(1.0 * amountOfL / amountOfSubL);
		return result;
	}
	
	/**
	 * ����Ӧ����m  ���ڸ���C cToA aToT cToT��Ϊ��ӦDistance+1
	 * �������C��top�����C��m���Ը�������ֵ
	 * @param cToA distance from concept to attribute concept
	 * @param aToT distance from attribute concept to top concept
	 * @param cToT distance from concept to top concept
	 * @param amountOfL concept amount of concept lattice
	 * @param amountOfSubL concept amount of sub concept lattice
	 * @return weight
	 */
	public static double weightFun1(int cToA, int aToT, int cToT, int amountOfL, int amountOfSubL) {
		double result = 0.0;
		if(cToT < cToA) {
			result = Math.log10(1.0 * amountOfL / amountOfSubL);
		} else {
			result = Math.log10(1.0 * cToT / cToA * amountOfL / amountOfSubL);
		}
		if(result < 0)
			System.out.println(result);
		return result;
	}
	
	/**
	 * ����Ӧ����m  ���ڸ���C  ���Ը���C��top����֮��ľ��루log�ڣ�
	 * (DL(m) + Dl(C)) / Dl(C)
	 * @param cToA distance from concept to attribute concept
	 * @param aToT distance from attribute concept to top concept
	 * @param cToT (useless)distance from concept to top concept
	 * @param amountOfL concept amount of concept lattice
	 * @param amountOfSubL concept amount of sub concept lattice
	 * @return weight
	 */
	public static double weightFun2(int cToA, int aToT, int cToT, int amountOfL, int amountOfSubL) {
		double result = 0.0;
//		if(aToT == 0) {
//			result = Math.log10(1.0 * (aToT + cToA + 1) / cToA * amountOfL / amountOfSubL);
//		} else {
//			result = Math.log10(1.0 * (aToT + cToA) / cToA * amountOfL / amountOfSubL);
//		}
		result = Math.log10(1.0 * (aToT + cToA - 1) / cToA * amountOfL / amountOfSubL);
		return result;
	}
	
	/**
	 * ����Ӧ����m  ���ڸ���C  ���Ը���C��top����֮��ľ��� ��log�⣩
	 * (DL(m) + Dl(C)) / Dl(C)
	 * @param cToA distance from concept to attribute concept
	 * @param aToT distance from attribute concept to top concept
	 * @param cToT distance from concept to top concept
	 * @param amountOfL concept amount of concept lattice
	 * @param amountOfSubL concept amount of sub concept lattice
	 * @return weight
	 */
	public static double weightFun3(int cToA, int aToT, int cToT, int amountOfL, int amountOfSubL) {
		double result = 0.0;
		result = 1.0 * (aToT + cToA - 1) / cToA * Math.log10(1.0 * amountOfL / amountOfSubL);
//		result = 1.0 * Math.log10(1.0 * amountOfL / amountOfSubL);
		return result;
	}
	
	/**
	 * ����Ӧ����m  ���ڸ���C   ���Ը���C��top����֮��ľ���(����log�ں�log������)
	 * DL(m) / (DL(m) + Dl(C))
	 * @param cToA distance from concept to attribute concept
	 * @param aToT distance from attribute concept to top concept
	 * @param cToT distance from concept to top concept
	 * @param amountOfL concept amount of concept lattice
	 * @param amountOfSubL concept amount of sub concept lattice
	 * @return weight
	 */
	public static double weightFun4(int cToA, int aToT, int cToT, int amountOfL, int amountOfSubL) {
		double result  = 0.0;
		result = 1.0 * aToT /(aToT + cToA - 1) * Math.log10(1.0 * amountOfL / amountOfSubL);
//		if(aToT == 0) {
//			result = Math.log10(1.0 * amountOfL / amountOfSubL);
//		} else {
//			result = Math.log10(1.0 * aToT /(aToT + cToA) * amountOfL / amountOfSubL);
//		}
		return result;
	}
	
	/**
	 * ����Ӧ����m  ���ڸ���C  ���Ը���C��top����֮��ľ��� ��log�⣩
	 * (DL(m) + Dl(C)) / Dl(C)
	 * @param cToA distance from concept to attribute concept
	 * @param aToT distance from attribute concept to top concept
	 * @param cToT distance from concept to top concept
	 * @param amountOfL concept amount of concept lattice
	 * @param amountOfSubL concept amount of sub concept lattice
	 * @return weight
	 */
	public static double weightFun5(int cToA, int aToT, int cToT, int amountOfL, int amountOfSubL) {
		double result = 0.0;
		result = Math.log1p( 1.0 * (aToT + cToA - 1) / cToA) * Math.log10(1.0 * amountOfL / amountOfSubL);
//		result = Math.sqrt(result);
		return result;
	}
	
	public static Map<Concept,Double> computeSimilarityModPath(Vector<Integer> rowData, Map<Integer, List<Concept>> concepts, int conceptCount, int[] attrsCount) {
		Map<Integer, Map<Integer, Integer>> indexMap = new HashMap<Integer, Map<Integer, Integer>>(2000 * 2);
		Map<Concept, Double> resultSim = new HashMap<Concept, Double>();
		Map<Integer, Integer> attributeHeight = new HashMap<Integer, Integer>();
//		System.out.println(rowData);
		//compute path weight
		Concept bottomConcept = new Concept();
		for(int i = attrsCount.length;i > 0;i--)
			if(concepts.get(i) != null) {
				bottomConcept = concepts.get(i).get(0);
				break;
			}
//		System.out.println(attrsCount.length - 1);
//		System.out.println(bottomConcept);
		Vector<ConIndexPair> bParent = bottomConcept.getParentConcept();
		List<Vector<Integer>> recordComEle = new ArrayList<Vector<Integer>>();
		//������bottom concept�ĸ���������˧ѡ��֦
		List<ConIndexPair> listRemain = new LinkedList<ConIndexPair>();
		int max = 1;
		int firstPSize = bParent.size();
		//ɸѡ���Ԫ��
		for(int i = 0;i < firstPSize;i++) {
			ConIndexPair pair = bParent.get(i);
			Concept parentConcept = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
			Vector<Integer> parentAttr = parentConcept.getAttrSet();
			Vector<Integer> interSet = GIC_AddIntent_LOOCV.intersect(parentAttr, rowData);
			int interSize = interSet.size();
			recordComEle.add(interSet);
			if(interSize < max)
				continue;
			if(interSize == max) {
				listRemain.add(pair);
				continue;
			}
			max = interSize;
			listRemain.clear();
			listRemain.add(pair);
		}
//		System.out.println(max + "  " + listRemain.size());
		//����prun���Լ�֦
		for(int i = 0;i < firstPSize;i++) {
			Vector<Integer> iSet = recordComEle.get(i);
			int iSize = iSet.size();
			if(iSize == 0 || iSize == max) {
				continue;
			}//ɾ��û�й���Ԫ�صģ���ʵ����ֱ��ɾ��ֻ��һ������Ԫ�ص������
			boolean flag = true;//true--�����listRemain
			for(int j = 0;j < listRemain.size();j++) {
				flag = true;
				ConIndexPair subPair = listRemain.get(j);
				Concept subConcept = concepts.get(subPair.getIndexOfMap()).get(subPair.getIndexOfBucket());
				Vector<Integer> subAttr = subConcept.getAttrSet();
				Vector<Integer> interSet = GIC_AddIntent_LOOCV.intersect(iSet, subAttr);
				int interSize = interSet.size();
				if(interSize == iSize) {
					flag = false;
					break;
				}
				if(interSize < iSize) {
					if(GIC_AddIntent_LOOCV.intersect(rowData, subAttr).size() > interSize)
						continue;
					listRemain.remove(j);
					j--;
				}
			}
			if(flag)
				listRemain.add(bParent.get(i));
		}
//		System.out.println(bParent.size() + "  " + listRemain.size());
		Map<Integer, Integer> curMap = new HashMap<Integer, Integer>();
		for(int i = 0;i < listRemain.size();i++) {
			ConIndexPair pair = listRemain.get(i);
			Concept parentConcept = concepts.get(pair.getIndexOfMap()).get(pair.getIndexOfBucket());
			int sizez = GIC_AddIntent_LOOCV.intersect(parentConcept.getAttrSet(), rowData).size();
			if(sizez == 0)
				continue;
			Map<Integer, Integer> map = computeIterated(parentConcept, rowData, concepts, conceptCount, attrsCount, indexMap, resultSim, attributeHeight);
			for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
				int key = entry.getKey();
				int value = entry.getValue();
				Integer tempValue = curMap.get(key);
				if(tempValue == null) {
					curMap.put(key, value);
				} else {
					int temp = tempValue.intValue();
					if(temp < value) {
						curMap.put(key, value);
					}
				}
			}
		}
//		Concept maxConcept = new Concept();
//		for(Map.Entry<Concept, Double> entry : resultSim.entrySet()) {
//			maxConcept = entry.getKey();
//			Double value = entry.getValue();
//			if(maxConcept == null || value == null)
//				throw new RuntimeException("result restoring wrong!");
//		}
		return resultSim;
	}
	
	public static Concept computeSimilarityNew(Vector<Integer> rowData, int[][] context, Map<Integer, List<Concept>> concepts, int objCount, int[] attrsCount) {
//		System.out.println("computeSimilarityNew");
		Vector<Integer> vectData = rowData;
		int vectDataSize = vectData.size();
		Concept maxConcept = new Concept();
		double maxSim = 0.0;
		for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter.hasNext();) {
			List<Concept> list = iter.next();
			if (list != null) {
				int listSize = list.size();
				for(int index = 0 ;index < listSize;index++) {
					Concept concept = list.get(index);
					Vector<Integer> attrVect = concept.getAttrSet();
					int objNum = concept.getObjSet().size();
//					int totalNum = context.length;
					//����attrsWeight
					int attrVectSize = attrVect.size();
//					/*
					if(attrVectSize < 1 || attrVect.get(attrVectSize - 1) < NoClass_LOOCV.point || concept.getObjSet().size() == 0)
						continue;
//					*/
					//��ʼ�������ƶ�
					double[] attrsWeight = new double[attrVectSize];
					for(int i = 0;i < attrVectSize;i++) {
//						int tempCount = 0;
//						for(int j = 0;j < totalNum;j++) {
//							if(context[j][attrVect.get(i)] == 1)
//								tempCount++;
//						}
						attrsWeight[i] = 1.0 * objNum / attrsCount[attrVect.get(i)];
//						System.out.println(attrsWeight[i]);
					}
					double tempSim = 0.0;
					double dividend = 0.0;
					double divider1 = 0.0;
					double divider2 = 0.0;
					for(int i = 0;i < attrVectSize;i++) {
						int tempAttr = attrVect.get(i);
						double tempD = Math.log10(1.0 / attrsWeight[i]);
						divider1 += tempD * tempD;
						if(vectData.contains(tempAttr)) {
							dividend += tempD * tempD;
						}
					}
					for(int i = 0;i < vectDataSize;i++) {
						int tempAttr = vectData.get(i);
						if(!attrVect.contains(tempAttr)) {
//							int tempCount = 0;
//							for(int j = 0;j < totalNum;j++) {
//								if(context[j][tempAttr] == 1)
//									tempCount++;
//							}
							double tempD = Math.log10(1.0 * objCount / attrsCount[tempAttr]);
							divider2 += tempD * tempD;
						}
					}
					divider2 += dividend;
					tempSim = dividend / Math.sqrt(divider1 * divider2);
					if(tempSim > maxSim) {
						maxSim = tempSim;
						maxConcept = concept;
					}
				}
			}
		}
		return maxConcept;
	}
	
	public static int countOfConcepts = 0;
//	@Test
//	void testSimilarity() {
//		Concept r = new Concept();
//		Concept l = new Concept();
//		int conceptCount = 22;
//		this.similarity(r, l, conceptCount, attrsCount, objCount);
//	}

}
