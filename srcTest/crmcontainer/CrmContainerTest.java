package crmcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import crmcontainer.sample.A;
import crmcontainer.sample.B;

public class CrmContainerTest {

	private static CrmContainer crmContainer;
	private static A a;
	private static B b;


	@Before
	public void setUp() throws Exception {
		crmContainer = new CrmContainer();
		crmContainer.setMonitor(new ConsoleMonitor());
		crmContainer.bind(A.class);
		crmContainer.bind(B.class);
		crmContainer.bind("db_name", "db1");
		
		a = crmContainer.getInstance(A.class);
		b = crmContainer.getInstance(B.class);
	}
	
	/**
	 * Testen der korrekten Erzeugung von Class A
	 * mit seiner Abhaengigkeit zu Class B
	 * und der Eigenschaft name
	 */
	@Test
	public void testA() {
		assertTrue(a instanceof A);
		assertEquals(a.getB(),b);
		assertEquals("db1", a.getName());
	}

	/**
	 * Testen der korrekten Erzeugung von Class B
	 * mit seiner Abhaengigkeit zu Class A
	 */
	@Test
	public void testB() {
		assertTrue(b instanceof B);
		assertEquals(b.getA(), a);
		assertTrue(b.isStarted());
	}

	/**
	 * Test das Binden und holen von Konstanten
	 */
	@Test
	public void testBindConstant() {
		crmContainer.bind("key1", "value1");
		assertEquals("value1", crmContainer.getInstance("key1"));
	}

	/**
	 * Test des Erkennens des Versuches 
	 * Konstanten unter selbem Key zu binden  
	 */
	@Test
	public void testBindConstantDuplicate() {
		crmContainer.bind("key1", "value1");
		boolean throwException = false;
		try {
			crmContainer.bind("key1", "value2");
		} catch (DuplicateBindException e) {
			throwException = true;
		}
		assertTrue(throwException);
	}
	

	/**
	 * Test des Erkennens des Versuches 
	 * Klassen unter selbem Key zu binden  
	 */
	@Test
	public void testBindClassDuplicate() {
		boolean throwException = false;
		try {
			crmContainer.bind(A.class);
		} catch (DuplicateBindException e) {
			throwException = true;
		}
		assertTrue(throwException);
	}
	

	
	@Test
	public void testRuntime() {
		long time = System.currentTimeMillis();
		CrmContainer container = new CrmContainer();
		container.bind(A.class);
		container.bind(B.class);
		container.bind("db_name", "db1");
		for(int i=1; i< 4500; i++) {
			container.bind("key"+i, "xxx");
		}
		time = printRunTime(time);
		
		a = container.getInstance(A.class);
		b = container.getInstance(B.class);
		time = printRunTime(time);
	}

	@Test
	public void testRuntimeWithInitialCapa() {
		long time = System.currentTimeMillis();
		CrmContainer container = new CrmContainer(5000);
		container.bind(A.class);
		container.bind(B.class);
		container.bind("db_name", "db1");
		for(int i=1; i< 4500; i++) {
			container.bind("key"+i, "xxx");
		}
		time = printRunTime(time);
		
		a = container.getInstance(A.class);
		b = container.getInstance(B.class);
		time = printRunTime(time);
	}


	private static long printRunTime(long time) {
		long currentTime = System.currentTimeMillis();
		System.out.println("runtime= "+(currentTime-time)+"ms");
		return currentTime;
	}
	
	
	@Test
	public void testThreadSafeGetInstance() {
		//TODO
	}
}
