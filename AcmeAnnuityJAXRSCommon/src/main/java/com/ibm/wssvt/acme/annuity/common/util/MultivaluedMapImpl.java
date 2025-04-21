package com.ibm.wssvt.acme.annuity.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

public class MultivaluedMapImpl<K, V> implements MultivaluedMap<K, V> {
	

	private final Map<K, List<V>> map;

	public MultivaluedMapImpl() {
		map = new LinkedHashMap<K, List<V>>();
	}

	public MultivaluedMapImpl(Map<K, V> map) {
		this();
		for (K key : map.keySet()) {
			add(key, map.get(key));
		}
	}

	public void add(K key, V value) {
		List<V> list = getOrCreate(key);
		list.add(value);
	}

	public V getFirst(K key) {
		List<V> list = get(key);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	public void putSingle(K key, V value) {
		List<V> list = getOrCreate(key);
		list.clear();
		list.add(value);
	}

	private List<V> getOrCreate(K key) {
		List<V> list = this.get(key);
		if (list == null) {
			list = createValueList(key);
			this.put(key, list);
		}
		return list;
	}

	private List<V> createValueList(K key) {
		return new ArrayList<V>();
	}

	public MultivaluedMapImpl<K, V> clone() {
		return clone(this);
	}

	public static <K, V> MultivaluedMapImpl<K, V> clone(MultivaluedMap<K, V> src) {
		MultivaluedMapImpl<K, V> clone = new MultivaluedMapImpl<K, V>();
		copy(src, clone);
		return clone;
	}

	public static <K, V> void copy(MultivaluedMap<K, V> src,
			MultivaluedMap<K, V> dest) {
		for (K key : src.keySet()) {
			List<V> value = src.get(key);
			List<V> newValue = new ArrayList<V>();
			newValue.addAll(value);
			dest.put(key, newValue);
		}
	}

	public static <K, V> void addAll(MultivaluedMap<K, V> src,
			MultivaluedMap<K, V> dest) {
		for (K key : src.keySet()) {
			List<V> srcList = src.get(key);
			List<V> destList = dest.get(key);
			if (destList == null) {
				destList = new ArrayList<V>(srcList.size());
				dest.put(key, destList);
			}
			destList.addAll(srcList);
		}
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, List<V>>> entrySet() {
		return map.entrySet();
	}

	public boolean equals(Object o) {
		return map.equals(o);
	}

	public List<V> get(Object key) {
		return map.get(key);
	}

	public int hashCode() {
		return map.hashCode();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public List<V> put(K key, List<V> value) {
		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends List<V>> t) {
		map.putAll(t);
	}

	public List<V> remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<List<V>> values() {
		return map.values();
	}

	// The following 4 methods are new in JAX-RS2.0
	@Override
	public void addAll(K key, @SuppressWarnings("unchecked") V... newValues) {
	
		List<V> list = get(key);
		if (newValues == null)
			throw new NullPointerException("the supplied newValues must not be null");
		if (newValues.length == 0)
			return;
		for (V value : newValues){
			list.add(value);
		}
	}

	@Override
	public void addAll(K key, List<V> valueList) {
		List<V> list = get(key);
		if (valueList == null)
			throw new NullPointerException("the supplied valueList must not be null");
		if (valueList.isEmpty())
			return;
		for ( V value : valueList){
			list.add(value);
		}
		//list.addAll(values);
		
	}

	@Override
	public void addFirst(K key, V value) {
		List<V> list = this.get(key);
		list.add(0, value);
		
	}


	@Override
	public boolean equalsIgnoreValueOrder(MultivaluedMap<K, V> otherMap) {
		if (this.map == otherMap){
			return true;
		}
		if (!keySet().equals(otherMap.keySet())) {
		    return false;
	    }
			
		for (Entry<K, List<V>> e : entrySet()) {
			List<V> olist = otherMap.get(e.getKey());
			if (e.getValue().size() != olist.size()) {
			      return false;
			}
			for (V v : e.getValue()) {
				if (!olist.contains(v)) {
			         return false;
			    }
		   }
		}
		return true;
	}
}

