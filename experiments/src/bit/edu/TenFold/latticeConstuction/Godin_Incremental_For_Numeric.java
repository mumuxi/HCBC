package bit.edu.TenFold.latticeConstuction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;


import bit.edu.TenFold.MySimilarityCom;
import bit.edu.TenFold.NoClass_Com;
import bit.edu.entity.Concept;
import bit.edu.entity.ConIndexPair;;

public class Godin_Incremental_For_Numeric {

	/**
	 * 
	 * @param attrCount
	 * @param objCount
	 * @param switchFlag
	 * @param dataSource
	 */
	
	public Godin_Incremental_For_Numeric(int attrCount, int objCount, int switchFlag, String dataSource) {
		this.attrCount = attrCount;
		this.objCount = objCount;
		this.switchFlag = switchFlag;
		this.contextFileName = dataSource;
		this.initial();
	}
	
	public void initial() {
		this.setTestConsumingTime(0);
		this.probabilityByLabel = new int[NoClass_Com.solutionPoint][attrCount - NoClass_Com.solutionPoint];
		this.context = new int[objCount][attrCount - 1];
		this.attsCount = new int[attrCount - 1];
		this.classValue = new int[objCount];
		this.concepts = new HashMap<Integer, List<Concept>>(this.attrCount * 2);
		newConcepts = new HashMap<Integer, List<Concept>>(this.attrCount * 2);
		int count = 0;
		File input = new File(path + "/" + this.contextFileName);
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(input);
			br = new BufferedReader(fr);
			String temp = null;
			temp = br.readLine();
			if(temp != null) {
				String[] counts = temp.split(" ");
				if(counts.length != this.attrCount - 1)
					throw new RuntimeException(
							"with wrong column # of attribute!");
				for(int i = 0;i < counts.length;i++) {
					this.attsCount[i] = Integer.parseInt(counts[i]);
				}
			}
			while ((temp = br.readLine()) != null) {
				String[] data = temp.split(",");
				if (data.length != 2)
					throw new RuntimeException(
							"with wrong column # of attribute!");
				this.classValue[count] = Integer.valueOf(data[1]);
				String[] context = data[0].split(" ");
				if (context.length != this.attrCount - 1)
					throw new RuntimeException(
							"with wrong column # of attribute!");
				for (int i = 0; i < this.attrCount - 1; i++) {
					String value = context[i];
					if (value.trim().equals(""))
						throw new RuntimeException(
								"There is value of attribute missing!");
					this.context[count][i] = Integer.parseInt(value);
				}
				count++;
				if (count >= this.objCount)
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
//		for(int i = 0;i < this.objCount;i++) {
//			for(int j = 0;j < this.attrCount;j++) {
//				System.out.print(this.context[i][j] + " ");
//			}
//			System.out.println();
//		}
	}
	
	public void computeGIM() {
		long startTic = (new Date()).getTime();
		int testStart = 0;
		int testEnd = 0;
		testStart = this.objCount * (switchFlag - 1) / NoClass_Com.testSetp;
		testEnd = this.objCount * switchFlag / NoClass_Com.testSetp - 1;
		for(int i = 0;i < this.objCount;i++) {
			if(i >= testStart && i <= testEnd) 
				continue;
			this.computeRow(this.context[i]);
		}
		long endTic = (new Date()).getTime();
		System.out.println("time for GIM:" + (endTic - startTic));
		//����class lab��˳��
//		int classCount = this.classValue.length - 1;
//		for(int i = 0;i < this.objCount / 10;i++) {
//			int tempValue = this.classValue[classCount - i];
//			this.classValue[classCount - i] = this.classValue[testEnd - i];
//			this.classValue[testEnd - i] = tempValue;
//		}
//		int count = 0;
//		for (int j = 0; j <= this.attrCount; j++) {
//			List<Concept> list = this.concepts.get(j);
//			if(list != null)
//				for(int n = 0;n < list.size();n++) {
//					System.out.println(list.get(n));
//					count++;
//				}
//		}
//		System.out.println(count);
	}
	
	public double startTest() {
//		int trainCount = this.objCount * switchFlag / 10;
//		int testCount = this.objCount - trainCount;
		int testStart = 0;
		int testEnd = 0;
		testStart = this.objCount * (switchFlag - 1) / 10;
		if(switchFlag != 10) {
			testEnd = testStart + this.objCount / 10 - 1;
		} else {
			testEnd = this.objCount - 1;
		}
		int accuracy = 0;
		for(int i = 0;i < this.objCount;i++) {
			if(i >= testStart && i <= testEnd) 
				continue;
			for(int j = 0;j < this.attrCount - 1;j++) {
				if(i == 0)
					this.attsCount[j] = 0;
				if(this.context[i][j] == 1)
					this.attsCount[j]++;
			}
		}
		//֮ǰ��
//		for(int i = 0;i < trainCount;i++) {
//			for(int j = 0;j < this.attrCount - 1;j++) {
//				if(i == 0)
//					this.attsCount[j] = 0;
//				if(this.context[i][j] == 1)
//					this.attsCount[j]++;
//			}
//		}
//		for (int i = trainCount; i < this.objCount; i++) {
		for (int i = testStart; i <= testEnd; i++) {
			Vector<Integer> vectData = new Vector<Integer>(this.context[i].length);;
			for(int n = 0;n < this.context[i].length - NoClass_Com.solutionPoint;n++) {
				if(this.context[i][n] == 1)
					vectData.add(n);
			}
			//ö�ٱ���
//			Concept maxConcept = MySimilarity.computeSim(this.context[i], this.concepts, this.objCount, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimMod(vectData, this.concepts, i, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimilarity(vectData, this.concepts, i, this.attsCount);
			Concept maxConcept = MySimilarityCom.computeSimilarityNew(vectData, context, this.concepts, i, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimilarityModPath(vectData, this.concepts, i, this.attsCount);
//			int conceptCount = 0;
//			for (int j = 0; j <= this.attrCount; j++) {
//				List<Concept> list = this.concepts.get(j);
//				if(list != null)
//					for(int n = 0;n < list.size();n++) {
//						conceptCount++;
//						Concept concept = list.get(n);
//						double tempSim = MySimilarity.computeSim(this.context[i], concept);
//						if(tempSim > maxSim) {
//							maxSim = tempSim;
//							maxConPair = concept.getId();
//						}
//					}
//			}
//			Concept maxConcept = this.concepts.get(maxConPair.getIndexOfMap()).get(maxConPair.getIndexOfBucket());
			Vector<Integer> objOfMax = maxConcept.getObjSet();
//			Vector<Integer> attrOfMax = maxConcept.getAttrSet();
			//δɸѡ����
			for(int j = 0;j < objOfMax.size();j++) {
				if(this.classValue[objOfMax.get(j)] == this.classValue[i]) {
					accuracy++;
					break;
				}
			}
			//����ɸѡģ��
//			if(attrOfMax.size() == 0)
//				System.out.println(maxConcept);
//			if(objOfMax.size() == 1 || attrOfMax.get(attrOfMax.size() - 1) >= NoClass_Com.point) {
//				for(int j = 0;j < objOfMax.size();j++) {
//					if(this.classValue[objOfMax.get(j)] == this.classValue[i]) {
//						accuracy++;
//						break;
//					}
//				}
//			}
//			else {
//				int solution = this.computeSolution(maxConcept);
//				if(solution == this.classValue[i])
//					accuracy++;
//			}
			
			
			this.computeRow(this.context[i]);
			for(int j = 0;j < this.attrCount - 1;j++)
				if(this.context[i][j] == 1)
					this.attsCount[j]++;
		}
//		return 1.0 * accuracy / testCount;
		return 1.0 * accuracy / (testEnd - testStart + 1);
	}
	
	/*
	 * computeSimConIm
	 */
	public double startTestConIm() {
		
		int testStart = 0;
		int testEnd = 0;
		testStart = this.objCount * (switchFlag - 1) / NoClass_Com.testSetp;
		testEnd = this.objCount * switchFlag / NoClass_Com.testSetp - 1;
		int accuracy = 0;
		int conceptCount = 0;

		for (int i = testStart; i <= testEnd; i++) {
			//ͳ�����Եĸ���ڵ����
			long startTic = (new Date()).getTime();
			conceptCount = 0;
			for(int n = 0;n < this.attsCount.length;n++)
				this.attsCount[n] = 1;
			Vector<Integer> vectData = new Vector<Integer>(this.attsCount.length);
			for (int n = 0; n < this.attsCount.length - NoClass_Com.solutionPoint; n++) {
				if (this.context[i][n] == 1)
					vectData.add(n);
			}
			for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter
					.hasNext();) {
				List<Concept> list = iter.next();
				if(list != null) {
					int listSize = list.size();
					for (int n = 0; n < listSize; n++) {
						Concept tempConcept = list.get(n);
						if(tempConcept.getAttrSet().size() == 0 || tempConcept.getObjSet().size() == 0)
							continue;
						conceptCount++;
						Vector<Integer> attrVector = tempConcept.getAttrSet();
						for(int k = 0;k < attrVector.size();k++) {
							this.attsCount[attrVector.get(k)]++;
						}
					}
				}
			}
			//ö�ٱ���
//			Concept maxConcept = MySimilarity.computeSimConImPaper(vectData, this.concepts, conceptCount, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimilarityModOnly2(vectData, this.concepts, conceptCount, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimConIm(vectData, this.concepts, conceptCount, this.attsCount);
			Map<Concept, Double> selectedConcepts = MySimilarityCom.computeSimilarityModPath(vectData, this.concepts, conceptCount, this.attsCount);
			
			Map<Integer, Double> solutionSim = new HashMap<Integer, Double>();//��¼�ۼ����ƶ�ֵ
			//for situation that vectdata.size()==0
			if(vectData.size() == 0) {
				if(this.classValue[i] == 1) {
					accuracy++;
				}
				this.computeRow(this.context[i]);
				continue;
			}
			/**
			 * ɸ�������score���̫���concepts
			 */
//			double sSim = 0.0;
//			boolean attributeSetSame = false;
//			double rate = NoClass_Com.A;
//			for(Entry<Concept, Double> conSimPair : selectedConcepts.entrySet()) {
//				Double selectedSim = conSimPair.getValue();
//				if(selectedSim.doubleValue() == 1.0) {
//					attributeSetSame = true;
//					sSim = 1.0;
//					rate = 0.0001;
//					break;
//				}
//				if(sSim < selectedSim) {
//					sSim = selectedSim;
//				}
//			}
//			Map<Concept, Double> tempConcepts = new HashMap<Concept, Double>();
//			for(Entry<Concept, Double> conSimPair : selectedConcepts.entrySet()) {
//				Concept maxConcept = conSimPair.getKey();
//				Double selectedSim = conSimPair.getValue();
//				if((sSim - selectedSim) / sSim < rate) {
//					tempConcepts.put(maxConcept, selectedSim);
//				}
//			}
//			selectedConcepts = tempConcepts;
//			if(attributeSetSame) {
//				for(Entry<Concept, Double> conSimPair : selectedConcepts.entrySet()) {
//					Concept maxConcept = conSimPair.getKey();
//					Vector<Integer> attrOfMax = maxConcept.getAttrSet();
//					Vector<Integer> objOfMax = maxConcept.getObjSet();
//					if(attrOfMax.get(attrOfMax.size() - 1) >= NoClass_Com.point) {
//						int classLab = attrOfMax.get(attrOfMax.size() - 1) - NoClass_Com.point + 1;
//						Double accumulatedSim = solutionSim.get(classLab);
//						if(accumulatedSim == null) {
//							solutionSim.put(new Integer(classLab), new Double(objOfMax.size()));
//						} else {
//							solutionSim.put(new Integer(classLab), new Double(objOfMax.size()) + accumulatedSim);
//						}
//					}
//				}
////				for(Entry<Integer, Double> entry : solutionSim.entrySet()) {
////					Integer key = entry.getKey();
////					Double sim = entry.getValue();
////					int classLab = key.intValue();
//////					double tempWeight = Math.log1p(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
////					double tempWeight = Math.pow(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1], 1);
//////					double tempWeight = Math.sqrt(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
////					sim /= tempWeight;
////					solutionSim.put(key, sim);
////				}
//			}
//			else {
//				for(Entry<Concept, Double> conSimPair : selectedConcepts.entrySet()) {
//					Concept maxConcept = conSimPair.getKey();
//					Double selectedSim = conSimPair.getValue();
//					Vector<Integer> attrOfMax = maxConcept.getAttrSet();
//					Vector<Integer> objOfMax = maxConcept.getObjSet();
//					if(attrOfMax.get(attrOfMax.size() - 1) >= NoClass_Com.point) {
//						int classLab = attrOfMax.get(attrOfMax.size() - 1) - NoClass_Com.point + 1;
//						Double accumulatedSim = solutionSim.get(classLab);
//						if(accumulatedSim == null) {
//							solutionSim.put(new Integer(classLab), selectedSim);
//						} else {
//							solutionSim.put(new Integer(classLab), selectedSim + accumulatedSim);
//						}
//					} else {
//						Map<Integer, Double> newSolutionSim = new HashMap<Integer, Double>();//��ʱ��¼�ۼ����ƶ�ֵ
////						int objSize = objOfMax.size();
//						double sumWeight = 0;
//						for (int j = 0; j < objOfMax.size(); j++) {
//							int index = objOfMax.get(j);
//							if (switchFlag != 10) {
//								int interval = testEnd - testStart + 1;
//								int criticalPoint = this.objCount - interval;
//								if (index >= testStart && index < criticalPoint)
//									index += interval;//this.objCount / NoClass_Com.testSetp;
//								else if (index >= criticalPoint) {
//									index = testStart + (index - criticalPoint);
//								}
//							}
//							int classLab = this.classValue[index];
////							double tempWeight = Math.log1p(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
////							double tempWeight = Math.sqrt(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
//							double tempWeight = Math.pow(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1], 1.75);
////							double tempWeight = 1.0;
//							
//							Vector<Integer> tempVector = new Vector<Integer>();
//							for(int m = 0;m < this.attsCount.length - NoClass_Com.solutionPoint;m++) {
//								if(this.context[index][m] == 1)
//									tempVector.add(m);
//							}
//							int tempVSize = tempVector.size();
//							int tempInSize = Godin_Incremental_Board.intersect(tempVector, vectData).size();
//							double sim = 1.0 * tempInSize / tempVSize;
//							tempWeight /= sim * sim * sim;
//							
//							if(tempWeight == 0) {
//								tempWeight = Math.log1p(2);
//							}
//							sumWeight += 1.0 / tempWeight;
//							Double accumulatedSim = newSolutionSim.get(classLab);
//							if(accumulatedSim == null) {
//								newSolutionSim.put(new Integer(classLab), selectedSim / tempWeight);
//							} else {
//								newSolutionSim.put(new Integer(classLab), selectedSim / tempWeight + accumulatedSim);
//							}
//						}
//						for(Entry<Integer, Double> entry : newSolutionSim.entrySet()) {
//							Integer key = entry.getKey();
//							Double sim = entry.getValue();
//							double tempSim = sim / sumWeight;
//							Double accumulatedSim = solutionSim.get(key);
//							if(accumulatedSim == null) {
//								solutionSim.put(key, tempSim);
//							} else {
//								solutionSim.put(key, accumulatedSim + tempSim);
//							}
//						}
////						System.out.println(solutionSim);
//					}
//				}
//			}
			/**
			 *  test without weighted adaption
			 */
			for(Entry<Concept, Double> conSimPair : selectedConcepts.entrySet()) {
				Concept maxConcept = conSimPair.getKey();
//				Double selectedSim = conSimPair.getValue();
				Vector<Integer> attrOfMax = maxConcept.getAttrSet();
				Vector<Integer> objOfMax = maxConcept.getObjSet();
				if(attrOfMax.get(attrOfMax.size() - 1) >= NoClass_Com.point) {
					int classLab = attrOfMax.get(attrOfMax.size() - 1) - NoClass_Com.point + 1;
					Double accumulatedSim = solutionSim.get(classLab);
					if(accumulatedSim == null) {
						solutionSim.put(new Integer(classLab), new Double(objOfMax.size()));
					} else {
						solutionSim.put(new Integer(classLab), new Double(objOfMax.size()) + accumulatedSim);
					}
				} else {
					Map<Integer, Double> newSolutionSim = new HashMap<Integer, Double>();//��ʱ��¼�ۼ����ƶ�ֵ
//					int objSize = objOfMax.size();
					for (int j = 0; j < objOfMax.size(); j++) {
						int index = objOfMax.get(j);
						if (switchFlag != 10) {
							int interval = testEnd - testStart + 1;
							int criticalPoint = this.objCount - interval;
							if (index >= testStart && index < criticalPoint)
								index += interval;//this.objCount / NoClass_Com.testSetp;
							else if (index >= criticalPoint) {
								index = testStart + (index - criticalPoint);
							}
						}
						int classLab = this.classValue[index];
						Double accumulatedSim = newSolutionSim.get(classLab);
						if(accumulatedSim == null) {
							newSolutionSim.put(new Integer(classLab), new Double(1));
						} else {
							newSolutionSim.put(new Integer(classLab), new Double(1) + accumulatedSim);
						}
					}
					for(Entry<Integer, Double> entry : newSolutionSim.entrySet()) {
						Integer key = entry.getKey();
						Double sim = entry.getValue();
						Double accumulatedSim = solutionSim.get(key);
						if(accumulatedSim == null) {
							solutionSim.put(key, sim);
						} else {
							solutionSim.put(key, accumulatedSim + sim);
						}
					}
				}
			}
//			for(Entry<Integer, Double> entry : solutionSim.entrySet()) {
//				Integer key = entry.getKey();
//				Double sim = entry.getValue();
//				int classLab = key.intValue();
////				double tempWeight = Math.log1p(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
//				double tempWeight = Math.pow(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1], 1);
////				double tempWeight = Math.sqrt(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
//				sim /= tempWeight;
//				solutionSim.put(key, sim);
//			}
			Double maxSim = 0.0;
			Integer maxKey = 0;
			/**
			 * �ɵ�ɸѡ����
			 */
			for(Entry<Integer, Double> entry : solutionSim.entrySet()) {
				Integer key = entry.getKey();
				Double sim = entry.getValue();
				if(sim > maxSim) {
					maxSim = sim;
					maxKey = key;
				}
			}
			if(maxKey.intValue() == this.classValue[i]) {
				accuracy++;
			} 
			else {
				System.out.println(solutionSim + " " + this.classValue[i] + " " + selectedConcepts.values());
			}
			
			/**
			 * ���ڲ���ɸѡ��K��concept�а�����ȷ��solution�ı���
			 */
//			for(Entry<Integer, Double> entry : solutionSim.entrySet()) {
//				Integer key = entry.getKey();
//				if(key.intValue() == this.classValue[i]) {
//					accuracy++;
//					break;
//				}
//			}
			
			long endTic = (new Date()).getTime();
			this.setTestConsumingTime(this.getTestConsumingTime()
					+ (endTic - startTic));
			this.computeRow(this.context[i]);
		}
		System.out.println(accuracy + "  " + testEnd + " " + testStart);
		return accuracy;
	}
	
	/*
	 *ÿ�θ������ݼ�����������Ҫ�޸ĵĵط�
	 *computeSimConImPaperObj(Concept, Map<Integer, List<Concept>>, int, int, int[], int[])
	 *computeSimConImPaper
	 *1.NoClass_Com�и����ݼ���ص�static����
	 *2.startTestConImObj�в���ʱ��Ҫȥ���������ݵ����Ͳ���
	 */
	public double startTestConImObj() {
		int trainCount = this.objCount * switchFlag / 10;
		int testCount = this.objCount - trainCount;
		int accuracy = 0;
		int conceptCount = 0;
		int[] objsCount = new int[this.objCount];
		Concept constructConcept = new Concept();
//		Concept contrastConcept = new Concept();
//		for (int i = 0; i <= testCount - 1;i++) {
		for (int i = trainCount; i < this.objCount; i++) {
			//ͳ�����Եĸ���ڵ����
			conceptCount = 0;
			for(int n = 0;n < this.attsCount.length;n++)
				this.attsCount[n] = 0;
			for(int n = 0;n < objsCount.length;n++)
				objsCount[n] = 0;
			Vector<Integer> constructAttr = new Vector<Integer>(this.context[i].length);
			for(int n = 0;n < this.context[i].length - NoClass_Com.solutionPoint;n++) {
				if(this.context[i][n] == 1)
					constructAttr.add(n);
			}
			constructConcept.setAttrSet(constructAttr);
			int maxCount = 0;
			for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter
					.hasNext();) {
				List<Concept> list = iter.next();
				if(list != null) {
					int listSize = list.size();
					for (int n = 0; n < listSize; n++) {
						Concept tempConcept = list.get(n);
						Vector<Integer> attrVector = tempConcept.getAttrSet();
						Vector<Integer> objVector = tempConcept.getObjSet();
						if(attrVector.size() == 0) {
							continue;
						}
						if(objVector.size() == 0) {
							continue;
						}
						conceptCount++;
						for(int k = 0;k < attrVector.size();k++) {
							this.attsCount[attrVector.get(k)]++;
						}
						int interCount = intersect(attrVector, constructAttr).size();
						if(interCount > maxCount) {
							maxCount = interCount;
							constructConcept.setObjSet(tempConcept.getObjSet());
//							contrastConcept = tempConcept;
						}
						for(int k = 0;k < objVector.size();k++) {
							objsCount[objVector.get(k)]++;
						}
					}
				}
			}
			//ö�ٱ���
//			Concept maxConcept = MySimilarity.computeSimConImPaper(constructAttr, concepts, conceptCount, this.attsCount);
			Concept maxConcept = MySimilarityCom.computeSimConImPaperObj(constructConcept, this.concepts, conceptCount, this.attrCount, this.attsCount, objsCount);
			Vector<Integer> objOfMax = maxConcept.getObjSet();
//			Vector<Integer> attrOfMax = maxConcept.getAttrSet();
			//δɸѡ����
			for(int j = 0;j < objOfMax.size();j++) {
				if(this.classValue[objOfMax.get(j)] == this.classValue[i]) {
					accuracy++;
					break;
				}
			}
			//����ɸѡģ��1
//			if(attrOfMax.size() == 0)
//				System.out.println(maxConcept);
//			if(objOfMax.size() == 1 || attrOfMax.get(attrOfMax.size() - 1) >= NoClass_Com.point) {
//				for(int j = 0;j < objOfMax.size();j++) {
//					if(this.classValue[objOfMax.get(j)] == this.classValue[i]) {
//						accuracy++;
//						break;
//					}
//				}
//			}
//			else {
//				int solution = this.computeSolution(maxConcept);
//				if(solution == this.classValue[i])
//					accuracy++;
//			}
			
			//����ɸѡģ��2
			
			
			this.computeRow(this.context[i]);
		}
		System.out.println(testCount + " " + accuracy);
		return 1.0 * accuracy / testCount;
	}
	
	//for car
	public double[] testForBitSet() {
		int trainCount = this.objCount / 10;
		int testCount = this.objCount - trainCount;
		int[] objsCount = new int[this.objCount];
		int[] pointNum = new int[9];
		int result[] = new int[9];
		double[] rate = new double[9];
		int accuracy = 0;
		Concept constructConcept = new Concept();
		int conceptCount = 0;
		for(int i = 0;i < 9;i++) {
			pointNum[i] = this.objCount * (i + 1) / 10;
		}
//		for(int i = 0;i < trainCount;i++) {
//			for(int j = 0;j < this.attrCount - 1;j++) {
//				if(i == 0)
//					this.attsCount[j] = 0;
//				if(this.context[i][j] == 1)
//					this.attsCount[j]++;
//			}
//		}
		for (int i = trainCount; i < this.objCount; i++) {
			Vector<Integer> constructAttr = new Vector<Integer>(this.context[i].length - 2);
			for(int n = 0;n < this.context[i].length - NoClass_Com.solutionPoint;n++) {
				if(this.context[i][n] == 1)
					constructAttr.add(n);
			}
			for(int j = 1;j < 9;j++) {
				if(pointNum[j] == i)
					result[j] = accuracy;
			}
			conceptCount = 0;
			for(int n = 0;n < this.attsCount.length;n++)
				this.attsCount[n] = 0;
			for(int n = 0;n < objsCount.length;n++)
				objsCount[n] = 0;
			constructConcept.setAttrSet(constructAttr);
			int maxCount = 0;
			for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter
					.hasNext();) {
				List<Concept> list = iter.next();
				if(list != null) {
					int listSize = list.size();
					for (int n = 0; n < listSize; n++) {
						Concept tempConcept = list.get(n);
						Vector<Integer> attrVector = tempConcept.getAttrSet();
						Vector<Integer> objVector = tempConcept.getObjSet();
						if(attrVector.size() == 0) {
							continue;
						}
						if(objVector.size() == 0) {
							continue;
						}
						conceptCount++;
						for(int k = 0;k < attrVector.size();k++) {
							this.attsCount[attrVector.get(k)]++;
						}
						int interCount = intersect(attrVector, constructAttr).size();
						if(interCount > maxCount) {
							maxCount = interCount;
							constructConcept.setObjSet(tempConcept.getObjSet());
						}
						for(int k = 0;k < objVector.size();k++) {
							objsCount[objVector.get(k)]++;
						}
					}
				}
			}
			//ö�ٱ���computeSimConImPapercomputeSimilarity
//			Concept maxConcept = MySimilarity.computeSimilarity(constructAttr, this.concepts, i, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimMod(constructAttr, this.concepts, i, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimConImPaper(constructAttr, this.concepts, conceptCount, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimilarityNew(constructAttr, context, this.concepts, i, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimConImPaperObj(constructConcept, this.concepts, conceptCount, this.attrCount, this.attsCount, objsCount);
//			Concept maxConcept = MySimilarity.computeSimilarityModOnly(constructAttr, this.concepts, conceptCount, this.attsCount);
			Concept maxConcept = MySimilarityCom.computeSimilarityModOnly2(constructAttr, this.concepts, conceptCount, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimilarityModPath(constructAttr, this.concepts, conceptCount, this.attsCount);
			Vector<Integer> objOfMax = maxConcept.getObjSet();
			//δɸѡ����
			for(int j = 0;j < objOfMax.size();j++) {
				if(this.classValue[objOfMax.get(j)] == this.classValue[i]) {
					accuracy++;
					break;
				}
			}
			this.computeRow(this.context[i]);
			for(int j = 0;j < this.attrCount - 1;j++)
				if(this.context[i][j] == 1)
					this.attsCount[j]++;
		}
		System.out.println(1.0 * accuracy / testCount);
		for(int i = 1;i < 9;i++) {
			rate[i] = 1.0 * (accuracy - result[i]) / (this.objCount - pointNum[i]);
			System.out.println(rate[i]);
		}
		rate[0] = 1.0 * accuracy / testCount;
		return rate;
	}
	
	//ͨ������Ԫ�ص�Ȩ�أ�path·�������нڵ�Ͱ�������Ԫ�صĽڵ�֮�ȣ�����ȡ���Ȩ��Ԫ����Ϊ�������ȡ�ۼ�Ȩ��
	public int computeSolution(Concept target) {
		Vector<Integer> objTarget = target.getObjSet();
		int objsNum = objTarget.size();
		int conceptCount = 0;
		int[] objsCount = new int[this.objCount];
		int childCount = 0;
		int[] targetObjCount = new int[this.objCount];
		double[] scores = new double[objsNum];
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for(int i = 0;i <= this.attrCount;i++) {
			List<Concept> list = this.concepts.get(i);
			if(list == null)
				continue;
			int listSize = list.size();
			for(int j = 0;j < listSize;j++) {
				Concept tempConcept = list.get(j);
				Vector<Integer> tempObj = tempConcept.getObjSet();
				int tempObjSize = tempObj.size();
				Vector<Integer> interSet = intersect(objTarget, tempObj);
				int interSetSize = interSet.size();
				boolean flag = false;
				if(interSetSize == tempObjSize) {
					flag = true;
					childCount++;
				}
				for(int n = 0;n < interSetSize;n++) {
					objsCount[interSet.get(n)]++;
					if(flag)
						targetObjCount[interSet.get(n)]++;
				}
				conceptCount++;
			}
		}
		double maxScore = 0;
		int bestSolution = -1;
		for(int i = 0;i < objsNum;i++) {
			int index = objTarget.get(i);
			scores[i] = Math.log10(1.0 * childCount / targetObjCount[index] * conceptCount / objsCount[index]);
			int solution = this.classValue[index];
			double score = 0.0;
			if(map.get(solution) != null) {
				score = map.get(solution) + scores[i];
				if(maxScore < score) {
					maxScore = score;
					bestSolution = solution;
				}
			}
			else {
				score = scores[i];
				if(maxScore < score) {
					maxScore = score;
					bestSolution = solution;
				}
			}
			map.put(solution, score);
		}
		//ѡȡ����ObjԪ����Ϊ����
		
//		double maxObjScore = 0.0;
//		int maxIndex = -1;
//		for(int i = 0;i < objsNum;i++) {
//			if(maxObjScore < scores[i]) {
//				maxObjScore = scores[i];
//				maxIndex = i;
//			}
//		}
//		bestSolution = this.classValue[objTarget.get(maxIndex)];
		
//		�Ƚ���Ԫ�ص����Լ��������������֮��Ĺ�ϵ�Ƚϣ��ó����
		System.out.println(map);
		return bestSolution;
	}
	
	//ͨ�������������Ԫ�ص�pathȨ�أ�Ѱ�����Լ��Ͼ������Ȩ�غ͵Ķ���Ԫ��
	public int computeSolutionMod2(Concept target, int conceptCount, int[] attrsCount) {
		Vector<Integer> objTarget = target.getObjSet();
		int objsNum = objTarget.size();
		double min = 1.0 * conceptCount * (this.attrCount - 1);
		int minIndex = -1;
		for(int i = 0;i < objsNum;i++) {
			double tempMin = 0;
			for(int j = 0;j < this.attrCount - 1 - NoClass_Com.solutionPoint;j++) {
				int objIndex = objTarget.get(i);
				if(this.context[objIndex][j] == 0)
					break;
				tempMin += 1.0 * attrsCount[j] / conceptCount;
			}
			if(tempMin < min) {
				min = tempMin;
				minIndex = i;
			}
		}
		return this.classValue[objTarget.get(minIndex)];
	}
	
	//������Ԫ�ض�Ӧ�����Լ��ϵ�ģ�����ڵ����Լ��ϵ�ģ֮����ΪȨ�أ���ȡ��СȨ�صĶ���Ԫ��
	public int computeSolutionMod(Concept target, int[] objSup) {
		Vector<Integer> objTarget = target.getObjSet();
		int objsNum = objTarget.size();
		int attrsNum = target.getAttrSet().size();
		double[] objSupRes = new double[objsNum];
		double max = 0.0;
		int maxIndex = -1;
		for(int i = 0;i < objsNum;i++) {
			double temp = 1.0 * attrsNum / objSup[objTarget.get(i)];
			objSupRes[i] = temp;
			if(max < temp) {
				max = temp;
				maxIndex = i;
			}
			System.out.print(this.classValue[objTarget.get(i)] + " " + temp + " ");
		}
		System.out.println();
		return this.classValue[objTarget.get(maxIndex)];
	}
	
	private void computeRow(int[] anObj) {
		this.newConcepts.clear();
		Vector<Integer> newAttr = new Vector<Integer>();
		row++;
		int classLab = 0;
		for(int i = NoClass_Com.point;i < anObj.length;i++) {
			if (anObj[i] == 1) {
				classLab = i - NoClass_Com.point;
				break;
			}
		}
		for (int i = 0; i < anObj.length; i++) {
			if (anObj[i] == 1) {
				newAttr.add(i);
			}
			if(i < attrCount - NoClass_Com.solutionPoint - 1) {
				this.probabilityByLabel[classLab][i] += anObj[i];
			}
		}
		this.probabilityByLabel[classLab][attrCount - NoClass_Com.solutionPoint - 1]++;
		if (this.bottomConcept == -1) {
			Vector<Integer> vo = new Vector<Integer>();
			vo.add(row);
			Concept concept = new Concept(newAttr, vo);
			this.bottomConcept = newAttr.size();
			concept.setId(new ConIndexPair(this.bottomConcept, 0));
			List<Concept> list = new ArrayList<Concept>();
			list.add(concept);
			this.concepts.put(this.bottomConcept, list);
			return;
		} else {
			Concept bottom = this.concepts.get(this.bottomConcept).get(0);
			int flag = relationOf2Vector(bottom.getAttrSet(), newAttr);
			if(flag == -1 || flag == -2) {
				Vector<Integer> botObj = bottom.getObjSet();
				Vector<Integer> botAttr = union(newAttr, bottom.getAttrSet());
				if(botObj.size() != 0) {
					Concept concept = new Concept(botAttr, new Vector<Integer>());
					concept.getParentConcept().add(new ConIndexPair(this.bottomConcept, 0));
					this.bottomConcept = botAttr.size();
					ConIndexPair bottomPair = new ConIndexPair(this.bottomConcept, 0);
					concept.setId(bottomPair);
					bottom.getChildConcept().add(bottomPair);
					List<Concept> list = new ArrayList<Concept>();
					list.add(concept);
					this.concepts.put(this.bottomConcept, list);
					this.newConcepts.put(this.bottomConcept, list);
				} else {
					int botAttrSize = botAttr.size();
					this.concepts.remove(this.bottomConcept);
					bottom.setAttrSet(botAttr);
					bottom.setId(new ConIndexPair(botAttrSize, 0));
					this.bottomConcept = botAttrSize;
					List<Concept> list = new ArrayList<Concept>();
					list.add(bottom);
					Vector<ConIndexPair> parents = bottom.getParentConcept();
					int parentsCount = parents.size();
					for(int i = 0;i < parentsCount;i++) {
						ConIndexPair parentsPair = parents.get(i);
						Concept temp = this.concepts.get(parentsPair.getIndexOfMap()).get(parentsPair.getIndexOfBucket());
						Vector<ConIndexPair> tempVector = temp.getChildConcept();
						tempVector.clear();
						tempVector.add(new ConIndexPair(this.bottomConcept, 0));
					}
					
					this.concepts.put(this.bottomConcept, list);
					this.newConcepts.put(this.bottomConcept, list);
				}
			}
		}
		for(int size = 0;size <= this.attrCount;size++) {
			List<Concept> bucket = this.concepts.get(size);
			if(bucket == null)
				continue;
			int sizeOfBucket = bucket.size();
			for(int j = 0;j < sizeOfBucket;j++) {
				Concept nodeConcept = bucket.get(j);
				Vector<Integer> nodeAttr = nodeConcept.getAttrSet();
				Vector<Integer> nodeObj = nodeConcept.getObjSet();
				int nodeFlag = relationOf2Vector(nodeAttr, newAttr);
				if(nodeFlag == 0) {
					nodeObj.add(row);
					return ;
				} else if(nodeFlag == -1) {
					nodeObj.add(row);
					List<Concept> newBucket = this.newConcepts.get(size);
					if(newBucket == null) {
						newBucket = new ArrayList<Concept>();
						newBucket.add(nodeConcept);
						this.newConcepts.put(size, newBucket);
					} else
						newBucket.add(nodeConcept);
				} else {
					Vector<Integer> newIntent = intersect(nodeAttr, newAttr);
					int newInSize = newIntent.size();
					List<Concept> checkDul = this.newConcepts.get(newInSize);
					boolean generatorFlag = true;
					if(checkDul == null)
						generatorFlag = true;
					else {
						int checkDulSize = checkDul.size();
						for(int i = 0;i < checkDulSize;i++) {
							int contrastFlag = relationOf2Vector(newIntent, checkDul.get(i).getAttrSet());
							if(contrastFlag == 0)
								generatorFlag = false;
						}
					}
					if(generatorFlag) {
						@SuppressWarnings("unchecked")
						Vector<Integer> newExtend = (Vector<Integer>) nodeObj.clone();
						newExtend.add(row);
						Concept concept = new Concept(newIntent, newExtend);
						List<Concept> list = this.concepts.get(newInSize);
						if(list == null) {
							list = new ArrayList<Concept>();
							list.add(concept);
							this.concepts.put(newInSize, list);
						} else {
							list.add(concept);
						}
						if(checkDul == null) {
							checkDul = new ArrayList<Concept>();
							checkDul.add(concept);
							this.newConcepts.put(newInSize, checkDul);
						} else {
							checkDul.add(concept);
						}
						ConIndexPair conIndexPair = new ConIndexPair(newInSize, list.size() - 1);
						concept.setId(conIndexPair);
						ConIndexPair nodeConceptId = nodeConcept.getId();
						Vector<ConIndexPair> nodeConceptParent = nodeConcept.getParentConcept();
						concept.getChildConcept().add(nodeConceptId);
						nodeConceptParent.add(conIndexPair);
						for(int n = newInSize - 1;n >= 0;n--) {
							List<Concept> bucketOfNew = this.newConcepts.get(n);
							if(bucketOfNew == null)
								continue;
							int sizeOfBON = bucketOfNew.size();
							for(int m = 0;m < sizeOfBON;m++) {
								Concept parentCon = bucketOfNew.get(m);
								ConIndexPair pConIndexPair = parentCon.getId();
								int parentFlag = relationOf2Vector(parentCon.getAttrSet(), newIntent);
								if(n == newInSize - 1 && parentFlag == -1) {
									if(nodeConceptParent.remove(parentCon.getId())) {
										parentCon.getChildConcept().remove(nodeConceptId);
									}
									concept.getParentConcept().add(pConIndexPair);
									parentCon.getChildConcept().add(conIndexPair);
									continue;
								}
								if(parentFlag == -1) {
									Vector<ConIndexPair> pCCV = parentCon.getChildConcept();
									boolean parent = true;
									for(int ii = 0;ii < pCCV.size();ii++) {
										ConIndexPair tPair = pCCV.get(ii);
										if(tPair.getIndexOfMap() >= newInSize)
											continue;
										Concept hConcept = this.concepts.get(tPair.getIndexOfMap()).get(tPair.getIndexOfBucket());
										Vector<Integer> hConAttr = hConcept.getAttrSet();
										int flagHN = relationOf2Vector(hConAttr, newIntent);
										if(flagHN == -1) {
											parent = false;
											break;
										}
									}
									if(parent) {
										if(nodeConceptParent.remove(parentCon.getId())) {
											parentCon.getChildConcept().remove(nodeConceptId);
										}
										concept.getParentConcept().add(pConIndexPair);
										parentCon.getChildConcept().add(conIndexPair);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	

	public static Vector<Integer> intersect(Vector<Integer> left, Vector<Integer> right) {
		Vector<Integer> result = new Vector<Integer>();
		int i = 0;
		int j = 0;
		int lsize = left.size();
		int rsize = right.size();
		while (i < lsize && j < rsize) {
			int lvalue = left.get(i);
			int rvalue = right.get(j);
			if (lvalue < rvalue)
				i++;
			else if (lvalue > rvalue)
				j++;
			else {
				result.add(lvalue);
				i++;
				j++;
			}
		}
		return result;
	}

	public void addByOrder(Vector<Integer> vector, int ele) {
		int i;
		for (i = 0; i < vector.size(); i++) {
			int temp = vector.get(i);
			if (ele == temp)
				return;
			if (ele < temp)
				break;
		}
		vector.add(i, ele);
	}

	/*
	 * 1 left contains right
	 * -1 right contains left
	 * 0 left equals right
	 * -2 independent
	 */
	public static int relationOf2Vector(Vector<Integer> left, Vector<Integer> right) {
		int result = 0;
		int i = 0, j = 0;
		int lsize = left.size();
		int rsize = right.size();
		if (lsize > rsize) {
			while (i < lsize && j < rsize) {
				int lvalue = left.get(i);
				int rvalue = right.get(j);
				if (lvalue == rvalue) {
					i++;
					j++;
					continue;
				}
				if (lvalue > rvalue)
					break;
				else {
					i++;
				}
			}
			if (j == rsize)
				result = 1;
			else
				result = -2;
		} else {
			while (i < lsize && j < rsize) {
				int lvalue = left.get(i);
				int rvalue = right.get(j);
				if (lvalue == rvalue) {
					i++;
					j++;
					continue;
				}
				if (lvalue < rvalue)
					break;
				else {
					j++;
				}
			}
			if (lsize == rsize && i == lsize)
				result = 0;
			else if (i == lsize)
				result = -1;
			else
				result = -2;
		}
		return result;
	}

	public static Vector<Integer> union(Vector<Integer> left, Vector<Integer> right) {
		Vector<Integer> result = new Vector<Integer>();
		int i = 0;
		int j = 0;
		int lsize = left.size();
		int rsize = right.size();
		while (i < lsize || j < rsize) {
			int lvalue;
			if (i < lsize)
				lvalue = left.get(i);
			else
				lvalue = MAXINT;
			int rvalue;
			if (j < rsize)
				rvalue = right.get(j);
			else
				rvalue = MAXINT;
			if (lvalue == rvalue) {
				result.add(lvalue);
				i++;
				j++;
			} else if (lvalue < rvalue) {
				result.add(lvalue);
				i++;
			} else {
				result.add(rvalue);
				j++;
			}
		}
		return result;
	}

//	public static void main(String[] args) {
//		String[] test = {"0", "1"};
//		Godin_Incremental_Board gim = new Godin_Incremental_Board(30, 100, test);
//	}

	public int[] getClassValue() {
		return classValue;
	}

	public void setClassValue(int[] classValue) {
		this.classValue = classValue;
	}

	public long getTestConsumingTime() {
		return testConsumingTime;
	}

	public void setTestConsumingTime(long testConsumingTime) {
		this.testConsumingTime = testConsumingTime;
	}

	static final String path = "./data/cbr";
	static final String suffix = ".data";
//	static final int cbr_AttrCount = 30;
	public final static int MAXINT = 1000000;
	public Map<Integer, List<Concept>> concepts;
	
	private int[][] probabilityByLabel;
	private long testConsumingTime;
	private int[] classValue;
	private int[] attsCount;
	private int[][] context;
	private String contextFileName;
	private int attrCount;
	private int objCount;
	private int switchFlag;
	private Map<Integer, List<Concept>> newConcepts;
	private int bottomConcept = -1;
	int row = -1;
}