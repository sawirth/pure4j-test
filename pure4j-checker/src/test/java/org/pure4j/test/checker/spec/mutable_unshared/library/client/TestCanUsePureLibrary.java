package org.pure4j.test.checker.spec.mutable_unshared.library.client;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.test.ShouldBePure;
import org.pure4j.test.checker.spec.mutable_unshared.library.supplier.PureLibrary;

public class TestCanUsePureLibrary {

	@Pure
	@ShouldBePure
	public static int someFunction(String in) {
		return new PureLibrary().doSomething(in);
	}
}
