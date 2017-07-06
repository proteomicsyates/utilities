package edu.scripps.yates.utilities.staticstorage;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

public class ItemStorage<T> {
	private final Map<String, Set<T>> itemByRunID = new THashMap<String, Set<T>>();
	private final Map<String, Set<T>> itemByConditionID = new THashMap<String, Set<T>>();
	private final TIntObjectHashMap<Set<T>> itemByExcelRow = new TIntObjectHashMap<Set<T>>();
	private final Map<String, Set<T>> itemByKey = new THashMap<String, Set<T>>();

	public void clearData() {
		itemByConditionID.clear();
		itemByExcelRow.clear();
		itemByRunID.clear();
		itemByKey.clear();
	}

	/**
	 *
	 * @param item
	 * @param msRunID
	 * @param conditionID
	 * @param excelRowIndex
	 * @param key
	 *            it can be a sequence, or a protein accession
	 */
	public void add(T item, String msRunID, String conditionID, int excelRowIndex, String key) {
		if (item != null) {
			if (msRunID != null && !"".equals(msRunID)) {
				addToMap(item, itemByRunID, msRunID);
			}
			if (conditionID != null && !"".equals(conditionID)) {
				addToMap(item, itemByConditionID, conditionID);
			}
			if (excelRowIndex > -1) {
				addToMap(item, itemByExcelRow, excelRowIndex);
			}
			if (key != null && !"".equals(key)) {
				addToMap(item, itemByKey, key);
			}
		}
	}

	/**
	 * Gets an item from the storage. Any non null input parameter will be
	 * checked and if not found, an empty set will be returned.<br>
	 * >If several non null parameters are submitted, it will return the
	 * intersection of the items retrieved from each of them.
	 *
	 * @param msRunID
	 * @param conditionID
	 * @param excelRowIndex
	 * @param key
	 * @return
	 */
	public Set<T> get(String msRunID, String conditionID, int excelRowIndex, String key) {
		Set<T> ret = new THashSet<T>();
		if (key != null && !"".equals(key)) {
			if (itemByKey.containsKey(key)) {
				if (ret.isEmpty()) {
					ret.addAll(itemByKey.get(key));
				} else {
					ret = getIntersection(ret, itemByKey.get(key));
				}
			} else {
				return Collections.EMPTY_SET;
			}
			if (ret.isEmpty()) {
				return Collections.EMPTY_SET;
			}
		}
		if (excelRowIndex > -1) {
			if (itemByExcelRow.containsKey(excelRowIndex)) {
				if (ret.isEmpty()) {
					ret.addAll(itemByExcelRow.get(excelRowIndex));
				} else {
					ret = getIntersection(ret, itemByExcelRow.get(excelRowIndex));
				}
			} else {
				return Collections.EMPTY_SET;
			}
			if (ret.isEmpty()) {
				return Collections.EMPTY_SET;
			}
		}

		if (msRunID != null && !"".equals(msRunID)) {
			if (itemByRunID.containsKey(msRunID)) {
				if (ret.isEmpty()) {
					ret.addAll(itemByRunID.get(msRunID));
				} else {
					ret = getIntersection(ret, itemByRunID.get(msRunID));
				}
			} else {
				return Collections.EMPTY_SET;
			}
			if (ret.isEmpty()) {
				return Collections.EMPTY_SET;
			}
		}
		// NEW in 4th Nov 2016
		// only in case of not having runID, we look for condition
		// this is because if someone looks for a protein with a certain runID,
		// it doesnt matter the condition
		// only in case of someone looking for all proteins in a condition (no
		// matter the msrun) this would be useful
		if (msRunID == null) {
			if (conditionID != null && !"".equals(conditionID)) {

				if (itemByConditionID.containsKey(conditionID)) {
					if (ret.isEmpty()) {
						ret.addAll(itemByConditionID.get(conditionID));
					} else {
						ret = getIntersection(ret, itemByConditionID.get(conditionID));
					}
				} else {
					return Collections.EMPTY_SET;
				}
				if (ret.isEmpty()) {
					return Collections.EMPTY_SET;
				}
			}
		}
		return ret;
	}

	/**
	 * Gets a set containing the elements present in both sets
	 *
	 * @param ret
	 * @param set
	 * @return
	 */
	private Set<T> getIntersection(Set<T> set1, Set<T> set2) {
		Set<T> ret = new THashSet<T>();
		Set<T> smallerSet = set1;
		Set<T> biggerSet = set2;
		if (set2.size() < set1.size()) {
			smallerSet = set2;
			biggerSet = set1;
		}
		for (T t1 : smallerSet) {
			if (biggerSet.contains(t1)) {
				ret.add(t1);
			}
		}
		return ret;
	}

	private void addToMap(T item, Map<String, Set<T>> map, String key) {
		if (map.containsKey(key)) {
			map.get(key).add(item);
		} else {
			Set<T> set = new THashSet<T>();
			set.add(item);
			map.put(key, set);
		}
	}

	private void addToMap(T item, TIntObjectHashMap<Set<T>> map, int key) {
		if (map.containsKey(key)) {
			map.get(key).add(item);
		} else {
			Set<T> set = new THashSet<T>();
			set.add(item);
			map.put(key, set);
		}
	}

	public boolean contains(String msRunID, String conditionID, int excelRowIndex, String key) {
		return !get(msRunID, conditionID, excelRowIndex, key).isEmpty();
	}
}
