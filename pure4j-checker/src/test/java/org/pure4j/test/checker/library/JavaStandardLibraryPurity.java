package org.pure4j.test.checker.library;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
//import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

//import javafx.scene.shape.Box;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.collections.AImmutableCollection;
import org.pure4j.collections.AImmutableSet;
import org.pure4j.collections.AMapEntry;
import org.pure4j.collections.APersistentMap;
import org.pure4j.collections.ASeq;
import org.pure4j.collections.ArrayChunk;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.Cons;
import org.pure4j.collections.Counted;
import org.pure4j.collections.Hasher;
import org.pure4j.collections.MapEntry;
import org.pure4j.collections.PersistentArrayMap;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.collections.PersistentHashSet;
import org.pure4j.collections.PersistentList;
import org.pure4j.collections.PersistentQueue;
import org.pure4j.collections.PersistentTreeMap;
import org.pure4j.collections.PersistentTreeSet;
import org.pure4j.collections.PersistentVector;
import org.pure4j.collections.PureCollections;
import org.pure4j.collections.PureListIterator;
import org.pure4j.collections.Reversible;
import org.pure4j.collections.SeqEnumeration;
import org.pure4j.collections.SeqIterator;
import org.pure4j.collections.Seqable;
import org.pure4j.collections.Settable;
import org.pure4j.collections.Sorted;
import org.pure4j.collections.ToStringFunctions;
import org.pure4j.collections.TransientHashMap;
import org.pure4j.collections.TransientHashSet;
import org.pure4j.collections.TransientList;
import org.pure4j.collections.TransientQueue;
import org.pure4j.collections.TransientTreeMap;
import org.pure4j.collections.TransientTreeSet;
import org.pure4j.collections.TransientVector;
import org.pure4j.collections.Util;
import org.pure4j.exception.Pure4JException;
import org.pure4j.lambda.PureCollectors;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.processor.Callback;
import org.pure4j.processor.ClassFileModelBuilder;
import org.pure4j.processor.PurityChecker;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;


public class JavaStandardLibraryPurity {
	
	@Test
	public void createJavaStandardPurityList() throws IOException, ClassNotFoundException {
		checkPurityOfClasses("target/java-builtins.pure", new ClassListProvider() {

			@Override
			public List<Class<?>> topLevelClasses() {
				List<Class<?>> out = new ArrayList<Class<?>>();
				out.addAll(javaLangClasses());
				out.addAll(javaIOClasses());
				out.addAll(javaNetClasses());
				out.addAll(javaUtilClasses());
				out.addAll(otherClasses());
				return out;
			}
		}, "java.", true, false, false);
	}
	
	@Test
	public void createPure4JCollectionsPurityList() throws IOException, ClassNotFoundException {
		checkPurityOfClasses("target/pure4j-collections.pure", new ClassListProvider() {

			@Override
			public List<Class<?>> topLevelClasses() {
				return Arrays.asList((Class<?>) 
						PureCollections.class,
						Util.class,
						PureCollectors.class,
						
						APersistentMap.class,
						PersistentHashMap.class,
						PersistentHashSet.class,
						PersistentArrayMap.class,
						PersistentTreeMap.class,
						PersistentTreeSet.class,
						PersistentList.class,
						PersistentQueue.class,
						PersistentVector.class,

						ArraySeq.class,
						TransientHashSet.class,
						TransientHashMap.class,
						TransientTreeMap.class,
						TransientTreeSet.class, 
						TransientList.class,
						TransientQueue.class,
						TransientVector.class,

						AImmutableCollection.class,
						AImmutableSet.class,
						AMapEntry.class,
						ArrayChunk.class,
						ArraySeq.class,
						ASeq.class,
//						Box.class,
						Cons.class,
						Counted.class,
						Hasher.class,
						MapEntry.class,
						PureCollections.class,
						PureListIterator.class,
						Reversible.class,
						Seqable.class,
						SeqEnumeration.class,
						SeqIterator.class,
						Settable.class,
						Sorted.class,
						ToStringFunctions.class,
						Util.class
						
						);
			}
		}, "org.pure4j", false, true, true);
	}
	
	interface ClassListProvider {
		
		public List<Class<?>> topLevelClasses();
		
	}

	protected void checkPurityOfClasses(String outputName, ClassListProvider clp, String packageStem, boolean assumePurity, boolean checkInterface, boolean expectNoErrors) throws IOException {
		FileCallback fc = new FileCallback(new File(outputName));
		ClassFileModelBuilder cfmb = new ClassFileModelBuilder(false);
		ClassLoader cl = this.getClass().getClassLoader();
		DefaultResourceLoader drl = new DefaultResourceLoader(cl);
		Set<Resource> resources = new HashSet<Resource>();
		
		for (Class<?> c : clp.topLevelClasses()) {
			visitAllOf(c, drl, cfmb, "", new HashSet<Class<?>>(), resources);
		}
		
		for (Resource resource : resources) {
			cfmb.visit(resource);
		}
		
		ProjectModel pm = cfmb.getModel();
		PurityChecker checker = new PurityChecker(cl, checkInterface, true);
		if (assumePurity) {
			for (String classInModel : pm.getAllClasses()) {
				ClassHandle ch = new ClassHandle(classInModel);
				if (!ch.hydrate(cl).isInterface()) {
					checker.addMethodsFromClassToPureList(ch.hydrate(cl), fc, pm, false, true);	
				}
			}
		}
		checker.checkModel(pm, fc);
		fc.close();
		
		if (expectNoErrors) {
			Assert.assertEquals(0, fc.errors.size());
		}
	}

	protected List<Class<?>> javaUtilClasses() {
		return Arrays.asList(
		//Objects.class,
		AbstractSet.class,
		AbstractCollection.class, 
		AbstractMap.class,
		ArrayList.class,
		AbstractList.class,
		ListIterator.class,
		Arrays.class,
		LinkedList.class,
		HashMap.class,
		HashSet.class,
		TreeMap.class,
		TreeSet.class,
		Deque.class,
		EnumMap.class,
		EnumSet.class,
		Hashtable.class,
		Vector.class,
		Iterator.class,
		StringTokenizer.class,
		Stack.class,
		Collections.class, 
		Currency.class
		);
	}

	protected List<Class<?>> javaIOClasses() {
	
		return Arrays.asList(ArrayList.class,
		BufferedInputStream.class,
		BufferedOutputStream.class,
		CharArrayReader.class,
		CharArrayWriter.class,
		StringReader.class,
		StringWriter.class,
		PrintStream.class,
		PrintWriter.class);
	}
	
	protected List<Class<URI>> javaNetClasses() {
	
		return Arrays.asList(URI.class);
	}

	protected List<Class<?>>  javaLangClasses() {
		return Arrays.asList(
		Number.class,
		StringBuilder.class,
		StringBuffer.class,
		Math.class,
		StrictMath.class,
		Integer.class, 
		Double.class, 
		Float.class,
		Character.class,
		Boolean.class,
		Long.class,
		String.class, 
		Class.class, 
		Enum.class, 
		Void.class);
	}
	
	protected List<Class<Assert>> otherClasses() {
		return Arrays.asList(Assert.class);
	}
	
	private void visitAllOf(Class<?> c, DefaultResourceLoader drl, ClassFileModelBuilder cfmb, String packageStem, Set<Class<?>> done, Set<Resource> resources) throws IOException {
		if ((c != Object.class) && (c != null) && (!done.contains(c))) {
			done.add(c);
			System.out.println("visiting: "+c);
			if (c.getName().startsWith(packageStem)) {
				resources.add(drl.getResource("classpath:/"+c.getName().replace(".", "/")+".class"));
				for (Class<?> intf : c.getInterfaces()) {
					visitAllOf(intf, drl, cfmb, packageStem, done,  resources);
				}
				
				for (Class<?> cl : c.getClasses()) {				
					visitAllOf(cl, drl, cfmb, packageStem, done, resources);
				}
				
				
				visitAllOf(c.getSuperclass(), drl, cfmb, packageStem, done, resources);
			}
		}
	}

	static class FileCallback implements Callback, Closeable {
		
		private BufferedWriter stream;
		
		private List<String> errors = new ArrayList<String>();
		
		public FileCallback(File out) throws IOException {
			stream = new BufferedWriter(new FileWriter(out));
		}
		
		@Override
		public void send(String s) {
			System.out.println(s);
		}
		
		@Override
		public void registerError(Pure4JException t) {
			errors.add(t.getMessage());
		}

		List<String> pureSignatures = new ArrayList<String>();
		
		@Override
		public void registerPure(String signature, Boolean interfacePure, Boolean implementationPure) {
			if (implementationPure) {
				pureSignatures.add(signature);
			}
		}

		@Override
		public void close() throws IOException {
			Collections.sort(errors);
			for (String string : errors) {
				System.err.println(string);
			}
			
			Collections.sort(pureSignatures);
			for (String s : pureSignatures) {
				stream.write("FORCE ");
				stream.write(s);
				stream.write("\n");
			}
			
			stream.close();
		}
	};
}
