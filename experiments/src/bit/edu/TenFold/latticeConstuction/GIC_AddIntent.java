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

public class GIC_AddIntent {

	/**
	 * 
	 * @param attrCount
	 * @param objCount
	 * @param switchFlag
	 * @param dataSource
	 */
	
	public GIC_AddIntent(int attrCount, int objCount, int switchFlag, String dataSource) {
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
		this.anaMatrix = new int[NoClass_Com.solutionPoint][NoClass_Com.solutionPoint];
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
		
		Vector<Integer> attrVect = this.bottomConcept.getAttrSet();
		ConIndexPair cip = this.bottomConcept.getId();
		cip.setIndexOfMap(this.attrCount - 1);
		cip.setIndexOfBucket(0);
		for(int i = 0;i < this.attrCount - 1;i++) {
			attrVect.add(i);
		}
		List<Concept> tlist = new ArrayList<Concept>();
		tlist.add(this.bottomConcept);
		this.concepts.put(this.attrCount - 1, tlist);
		
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
//					System.out.println(list.size());
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

//		System.out.println(testStart + " " + testEnd);
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
			// ֮ǰ��ɸѡ����
			/**
			 * ɸ�������score���̫���concepts
			 */
			double sSim = 0.0;
			Concept sConcept = null;
			int sCount = 0;
			Map<Concept, Double> tempConcepts = new HashMap<Concept, Double>();
			double caseCount = 0;
			for(Entry<Concept, Double> conSimPair : selectedConcepts.entrySet()) {
				Concept maxConcept = conSimPair.getKey();
				caseCount += maxConcept.getObjSet().size();
				Double selectedSim = conSimPair.getValue();
				if(sCount == 0) {
					sSim = selectedSim;
					sConcept = maxConcept;
					tempConcepts.put(sConcept, sSim);
					sCount++;
					continue;
				}
				if(selectedSim > sSim) {
					if((selectedSim - sSim) / selectedSim > NoClass_Com.A) {
						tempConcepts.remove(sConcept);
						sSim = selectedSim;
						sConcept = maxConcept;
						sCount++;
					}
					tempConcepts.put(maxConcept, selectedSim);
				} else {
					if((sSim - selectedSim) / sSim > NoClass_Com.A) {
						sCount++;
					} else {
						tempConcepts.put(maxConcept, selectedSim);
					}
				}
			}
			
			selectedConcepts = tempConcepts;
			for(Entry<Concept, Double> conSimPair : selectedConcepts.entrySet()) {
				Concept maxConcept = conSimPair.getKey();
				Double selectedSim = conSimPair.getValue();
				selectedSim = selectedSim * maxConcept.getObjSet().size() / caseCount;
				Vector<Integer> attrOfMax = maxConcept.getAttrSet();
				Vector<Integer> objOfMax = maxConcept.getObjSet();
				if(attrOfMax.get(attrOfMax.size() - 1) >= NoClass_Com.point) {
					int classLab = attrOfMax.get(attrOfMax.size() - 1) - NoClass_Com.point + 1;
					Double accumulatedSim = solutionSim.get(classLab);
					if(accumulatedSim == null) {
						solutionSim.put(new Integer(classLab), selectedSim);
					} else {
						solutionSim.put(new Integer(classLab), selectedSim + accumulatedSim);
					}
				} else {
					Map<Integer, Double> newSolutionSim = new HashMap<Integer, Double>();//��ʱ��¼�ۼ����ƶ�ֵ
//					int objSize = objOfMax.size();
					double sumWeight = 0;
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
//						double tempWeight = Math.log1p(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
//						double tempWeight = Math.sqrt(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
						double tempWeight = Math.pow(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1], 0);
//						double tempWeight = 1.0;
						
						Vector<Integer> tempVector = new Vector<Integer>();
						for(int m = 0;m < this.attsCount.length - NoClass_Com.solutionPoint;m++) {
							if(this.context[index][m] == 1)
								tempVector.add(m);
						}
						int tempVSize = tempVector.size();
						int tempInSize = intersect(tempVector, vectData).size();
						double sim = 1.0 * tempInSize / tempVSize;
						tempWeight /= sim * sim * sim;
						
						if(tempWeight == 0) {
							tempWeight = Math.log1p(2);
						}
						sumWeight += 1.0 / tempWeight;
						Double accumulatedSim = newSolutionSim.get(classLab);
						if(accumulatedSim == null) {
							newSolutionSim.put(new Integer(classLab), selectedSim / tempWeight);
						} else {
							newSolutionSim.put(new Integer(classLab), selectedSim / tempWeight + accumulatedSim);
						}
					}
					for(Entry<Integer, Double> entry : newSolutionSim.entrySet()) {
						Integer key = entry.getKey();
						Double sim = entry.getValue();
						double tempSim = sim / sumWeight;
						Double accumulatedSim = solutionSim.get(key);
						if(accumulatedSim == null) {
							solutionSim.put(key, tempSim);
						} else {
							solutionSim.put(key, accumulatedSim + tempSim);
						}
					}
//					System.out.println(solutionSim);
				}
			}
			
			/**
			 *  test without weighted adaption
			 */
//			for(Entry<Concept, Double> conSimPair : selectedConcepts.entrySet()) {
//				Concept maxConcept = conSimPair.getKey();
////				Double selectedSim = conSimPair.getValue();
//				Vector<Integer> attrOfMax = maxConcept.getAttrSet();
//				Vector<Integer> objOfMax = maxConcept.getObjSet();
//				if(attrOfMax.get(attrOfMax.size() - 1) >= NoClass_Com.point) {
//					int classLab = attrOfMax.get(attrOfMax.size() - 1) - NoClass_Com.point + 1;
//					Double accumulatedSim = solutionSim.get(classLab);
//					if(accumulatedSim == null) {
//						solutionSim.put(new Integer(classLab), new Double(objOfMax.size()));
//					} else {
//						solutionSim.put(new Integer(classLab), new Double(objOfMax.size()) + accumulatedSim);
//					}
//				} else {
//					Map<Integer, Double> newSolutionSim = new HashMap<Integer, Double>();//��ʱ��¼�ۼ����ƶ�ֵ
////					int objSize = objOfMax.size();
//					for (int j = 0; j < objOfMax.size(); j++) {
//						int index = objOfMax.get(j);
//						if (switchFlag != 10) {
//							int interval = testEnd - testStart + 1;
//							int criticalPoint = this.objCount - interval;
//							if (index >= testStart && index < criticalPoint)
//								index += interval;//this.objCount / NoClass_Com.testSetp;
//							else if (index >= criticalPoint) {
//								index = testStart + (index - criticalPoint);
//							}
//						}
//						int classLab = this.classValue[index];
//						Double accumulatedSim = newSolutionSim.get(classLab);
//						if(accumulatedSim == null) {
//							newSolutionSim.put(new Integer(classLab), new Double(1));
//						} else {
//							newSolutionSim.put(new Integer(classLab), new Double(1) + accumulatedSim);
//						}
//					}
//					for(Entry<Integer, Double> entry : newSolutionSim.entrySet()) {
//						Integer key = entry.getKey();
//						Double sim = entry.getValue();
//						Double accumulatedSim = solutionSim.get(key);
//						if(accumulatedSim == null) {
//							solutionSim.put(key, sim);
//						} else {
//							solutionSim.put(key, accumulatedSim + sim);
//						}
//					}
//				}
//			}
			
			Double maxSim = 0.0;
			Integer maxKey = 0;
			//�µ�ɸѡ����
//			for(Entry<Integer, Double> entry : solutionSim.entrySet()) {
//				Integer key = entry.getKey();
//				Double sim = entry.getValue();
//				int classLab = key.intValue();
////				double tempWeight = Math.log1p(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
//				double tempWeight = Math.pow(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1], 1);
////				double tempWeight = Math.sqrt(this.probabilityByLabel[classLab - 1][attrCount - NoClass_Com.solutionPoint - 1]);
//				sim /= tempWeight;
//				if(sim > maxSim) {
//					maxSim = sim;
//					maxKey = key;
//				}
//			}
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
//				System.out.println(solutionSim + " " + this.classValue[i] + " " + selectedConcepts.values());
			}
			this.anaMatrix[this.classValue[i] - 1][maxKey.intValue()-1]++;
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
//		System.out.println(accuracy + "  " + testEnd + " " + testStart);
		for(int i = 0;i < NoClass_Com.solutionPoint;i++) {
			for(int j = 0;j < NoClass_Com.solutionPoint;j++) {
				System.out.print(this.anaMatrix[i][j] + " ");
			}
			System.out.println();
		}
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
	
	@SuppressWarnings("unchecked")
	public void computeRow(int[] anObj) {
		row++;
		int classLab = 0;
		for(int i = NoClass_Com.point;i < anObj.length;i++) {
			if (anObj[i] == 1) {
				classLab = i - NoClass_Com.point;
				break;
			}
		}
		Vector<Integer> intent = new Vector<Integer>();
		for (int i = 0; i < anObj.length; i++) {
			if(anObj[i] == 1)
				intent.add(i);
			if(i < attrCount - NoClass_Com.solutionPoint - 1) {
				this.probabilityByLabel[classLab][i] += anObj[i];
			}
		}
		this.probabilityByLabel[classLab][attrCount - NoClass_Com.solutionPoint - 1]++;
		Concept concept = this.addIntent(intent, bottomConcept);
		concept.getObjSet().add(row);
		Vector<ConIndexPair> parents = concept.getParentConcept();
		while(parents.size() != 0) {
			int pSize = parents.size();
			Vector<ConIndexPair> newParents = new Vector<ConIndexPair>();
			for(int j = 0;j < pSize;j++) {
				ConIndexPair pCIP = parents.get(j);
				Concept parent = this.concepts.get(pCIP.getIndexOfMap()).get(pCIP.getIndexOfBucket());
				Vector<Integer> obj = parent.getObjSet();
				if(obj.get(obj.size() - 1) < row)
					obj.add(row);
				Vector<ConIndexPair> pa = parent.getParentConcept();
				for(int i = 0;i < pa.size();i++) {
					ConIndexPair tempCIP = pa.get(i);
					if(!newParents.contains(tempCIP)) {
						newParents.add(tempCIP);
					}
				}
			}
			parents = (Vector<ConIndexPair>) newParents.clone();
			newParents.clear();
		}
	}
	
	public Concept addIntent(Vector<Integer> intent, Concept generatorConcept) {
		generatorConcept = this.getMaximalConcept(intent, generatorConcept);
//		System.out.println(generatorConcept);
		if(generatorConcept.getAttrSet().size() == intent.size())
			return generatorConcept;
		Vector<ConIndexPair> generatorParent = generatorConcept.getParentConcept();
		Vector<Concept> newParents = new Vector<Concept>();
		boolean addParent = true;
		int gpSize = generatorParent.size();
//		System.out.println("gpsize" + gpSize);
		for(int i = 0;i < gpSize;i++) {
			ConIndexPair ciPair = generatorParent.get(i);
//			System.out.println(ciPair);
			Concept candidate = this.concepts.get(ciPair.getIndexOfMap()).get(ciPair.getIndexOfBucket());
//			System.out.println(this.concepts);
			Vector<Integer> cIntent = candidate.getAttrSet();
			Vector<Integer> interset = intersect(cIntent, intent);
//			System.out.println(cIntent + " " + intent);
//			System.out.println("candidata" + candidate);
			if(interset.size() < cIntent.size()) {
				candidate = this.addIntent(interset, candidate);
			}
			addParent = true;
			for(int j = 0;j < newParents.size();) {
				Concept parent = newParents.get(j);
				Vector<Integer> left = candidate.getAttrSet();
				Vector<Integer> right = parent.getAttrSet();
				Vector<Integer> interset2 = intersect(left, right);
				if(interset2.size() == left.size()) {
					addParent = false;
					break;
				} else if(interset2.size() == right.size()) {
					newParents.remove(parent);
					continue;
				}
				j++;
			}
			if(addParent)
				newParents.add(candidate);
		}
		@SuppressWarnings("unchecked")
		Concept newConcept = new Concept(intent, (Vector<Integer>) generatorConcept.getObjSet().clone());
		List<Concept> bucket = this.concepts.get(intent.size());
		if(bucket == null) {
			bucket = new ArrayList<Concept>();
			ConIndexPair cip = newConcept.getId();
			cip.setIndexOfMap(intent.size());
			cip.setIndexOfBucket(0);
			bucket.add(newConcept);
			this.concepts.put(intent.size(), bucket);
		} else {
			ConIndexPair cip = newConcept.getId();
			cip.setIndexOfMap(intent.size());
			cip.setIndexOfBucket(bucket.size());
			bucket.add(newConcept);
		}
		int newSize = newParents.size();
		ConIndexPair geneCIP = generatorConcept.getId();
		ConIndexPair newCIP = newConcept.getId();
		Vector<ConIndexPair> geneParent = generatorConcept.getParentConcept();
		for(int j = 0;j < newSize;j++) {
			Concept parent = newParents.get(j);
			ConIndexPair pCIP = parent.getId();
//			System.out.println(parent);
			Vector<ConIndexPair> children = parent.getChildConcept();
			int cSize = children.size();
//			System.out.println(children + " " + geneCIP);
			for(int k = 0;k < cSize;k++) {
				if(children.get(k) == geneCIP) {
					children.remove(geneCIP);
					geneParent.remove(pCIP);
					break;
				}
			}
			newConcept.getParentConcept().add(pCIP);
			parent.getChildConcept().add(newCIP);
		}
		newConcept.getChildConcept().add(geneCIP);
		geneParent.add(newCIP);
		return newConcept;
	}
	
	public Concept getMaximalConcept(Vector<Integer> intent, Concept generatorConcept) {
		boolean isMaximal = true;
		while(isMaximal) {
			isMaximal = false;
			Vector<ConIndexPair> parents = generatorConcept.getParentConcept();
			for(int i = 0;i < parents.size();i++) {
				ConIndexPair ciPair = parents.get(i);
				Concept parent = this.concepts.get(ciPair.getIndexOfMap()).get(ciPair.getIndexOfBucket());
				Vector<Integer> pIntent = parent.getAttrSet();
				Vector<Integer> interset = intersect(pIntent, intent);
				if(interset.size() == intent.size()) {
					generatorConcept = parent;
					isMaximal = true;
					break;
				}
			}
		}
		return generatorConcept;
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

	/**
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
	*/

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
	public int[][] anaMatrix;
	
	private int[][] probabilityByLabel;
	private long testConsumingTime;
	private int[] classValue;
	private int[] attsCount;
	private int[][] context;
	private String contextFileName;
	private int attrCount;
	private int objCount;
	private int switchFlag;
	private Concept bottomConcept = new Concept();
	int row = -1;
}
