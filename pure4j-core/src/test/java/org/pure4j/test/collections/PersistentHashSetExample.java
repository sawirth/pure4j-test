package org.pure4j.test.collections;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.IPersistentCollection;
import org.pure4j.collections.IPersistentSet;
import org.pure4j.collections.ITransientSet;
import org.pure4j.collections.PersistentHashSet;
import org.pure4j.test.ShouldBePure;

public class PersistentHashSetExample extends AbstractCollectionTest {

	@Pure
	@ShouldBePure
	public void pureMethod(IPersistentSet<String> in, int expected) {
		log("keys:");
		for (String entry : in) {
			log(entry);
		}
		assertEquals(expected, in.size());
		
		in.cons("bobob");
	}
	
	@Test
	public void sanityTestOfSet() {
		
		// check persistence
		IPersistentSet<String> phm1 = new PersistentHashSet<String>("bob", "jeff", "gogo");
		IPersistentSet<String> phm = phm1.cons("spencer");
		pureMethod(phm, 4);
		phm = phm.cons("spencera");
		phm = phm.cons("spencerb");
		phm = phm.cons("spencerc");
		phm = phm.cons("spencerc");		
		pureMethod(phm, 7);
		
		// check transient
		ITransientSet<String> set = phm.asTransient();
		set.add("something");
		set.remove("spencera");
		IPersistentSet<String> out = set.persistent();
		System.out.println(out);
		
		assertEquals(phm1, phm1.cons("bb").cons("jeff").disjoin("bb").disjoin("np"));
		
		assertEquals(0, PersistentHashSet.emptySet().size());
	}
	
	@Test
	public void testConstruction() {
		// array
		int entries = 100;
		PersistentHashSet<String> pm = new PersistentHashSet<String>(makeSet(entries));
		assertEquals(entries, pm.size());
		System.out.println(pm);
		
		// iseq
		PersistentHashSet<String> pm2 = new PersistentHashSet<String>(pm.seq());
		assertEquals(pm2, pm);
//		
//		// map-based
		PersistentHashSet<String> pm3 = new PersistentHashSet<String>(pm);
		assertEquals(pm3, pm);
//		 
//		// no-args
		PersistentHashSet<String> pm4 = new PersistentHashSet<String>();
		for (String entry : pm) {
			pm4 = pm4.cons(entry);
		}
		assertEquals(pm, pm4);

	}
	
	
	@Pure
	@ShouldBePure
	public IPersistentCollection<String> getInstance() {
		return new PersistentHashSet<String>();
	}

	private String[] makeSet(int i) {
		String[] out = new String[i];
		for (int j = 0; j < i; j++) {
			out[j] = "k"+j;
		}
		return out;
	}
	
}
