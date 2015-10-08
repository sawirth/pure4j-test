package org.pure4j.test.checker.corner_cases.anon;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ISeq;
import org.pure4j.test.checker.support.AbstractChecker;
import org.pure4j.test.checker.support.ShouldBePure;

public class AnonymousInnerClass extends AbstractChecker {
	
	@Pure
	@ShouldBePure
	public static int doSomething(ISeq<Integer> in) {
		final int[] count = { 0 };
		in.forEach(new Consumer<Integer>() {

			@Override
			@ShouldBePure
			@Pure
			public void accept(Integer t) {
				count[0] += t;
			}
			
		});
		
		return count[0];
	}
	
	@Test
	public void addEmUp() {
		Assert.assertEquals(121, doSomething(ArraySeq.create(5, 6, 12, 88, 10)));
	}
	
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
