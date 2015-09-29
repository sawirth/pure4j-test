package org.pure4j.collections;

import java.util.HashMap;
import java.util.Map;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.PureParameters;

public class TransientHashMap<K, V> extends HashMap<K, V> implements ITransientMap<K, V> {
	
	public TransientHashMap() {
		super();
	}

	public TransientHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public TransientHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	@PureParameters(Enforcement.NOT_PURE)
	public TransientHashMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	@Override
	public int count() {
		return size();
	}

	@Override
	public IPersistentMap<K, V> persistent() {
		return new PersistentHashMap<K,V>(this);
	}

}