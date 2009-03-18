package working_it.witcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import working_it.witcontainer.ConsoleMonitor;
import working_it.witcontainer.DuplicateBindException;
import working_it.witcontainer.WitContainer;
import working_it.witcontainer.sample.A;
import working_it.witcontainer.sample.AExtended;
import working_it.witcontainer.sample.B;
import working_it.witcontainer.sample.C;
import working_it.witcontainer.sample.SampleSimpleClassProvider;
import working_it.witcontainer.sample.SampleStringProvider;
import working_it.witcontainer.sample.SimpleClass;


public class WitContainerTest {

	private static WitContainer witContainer;
	private static A a;
	private static B b;


	@Before
	public void setUp() throws Exception {
		witContainer = new WitContainer();
		bindClasses();
		
		a = witContainer.getInstance(A.class);
		b = witContainer.getInstance(B.class);
	}

	private void bindClasses() {
		witContainer.setMonitor(new ConsoleMonitor());
		witContainer.bind(A.class);
		witContainer.bind(B.class);
		witContainer.bind(C.class);
		witContainer.bind("db_name").to("db1");
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
	 * Testen der korrekten Erzeugung von Class B mit
	 * - seiner Abhaengigkeit zu Class A
	 * - seiner Abhaengikeit zu Classe C
	 * - Methode start aufgerufen wurde
	 * - Methode Start der Instance von Classe C aufgerufen wurde
	 * 
	 */
	@Test
	public void testB() {
		assertTrue(b instanceof B);
		assertEquals(b.getA(), a);
		assertTrue("b.start not called!",b.isStarted());
		C c = b.getC();
		assertNotNull(c);
		assertTrue("c.start not called!", c.isStarted());
	}

	
	/**
	 * Testen des Bindens eines Klasse unter einem
	 * Object-Key
	 *
	 */
	@Test
	public void testBindClassWithObjectAsKey() {
		witContainer.bind("other-a-instance").to(A.class);
		A a2 = (A) witContainer.getInstance("other-a-instance");
		assertTrue(a2 instanceof A);
		assertNotSame(a2, a);
	}
	
	enum TestEnum { EINS, ZWEI }
	
	/**
	 * Test das Binden und holen von Konstanten
	 */
	@Test
	public void testBindConstant() {
		witContainer.bind("key1").to("value1");
		witContainer.bind(TestEnum.EINS).to(new Integer(1));
		witContainer.bind(TestEnum.ZWEI).to(new Integer(2));
		assertEquals("value1", witContainer.getInstance("key1"));
		assertEquals(1, ((Integer)witContainer.getInstance(TestEnum.EINS)).intValue() );
		assertEquals(2, ((Integer)witContainer.getInstance(TestEnum.ZWEI)).intValue() );
	}

	/**
	 * Test des Erkennens des Versuches 
	 * Konstanten unter selbem Key zu binden  
	 */
	@Test
	public void testBindConstantDuplicate() {
		witContainer.bind("key1").to("value1");
		boolean throwException = false;
		try {
			witContainer.bind("key1").to("value2");
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
			witContainer.bind(A.class);
		} catch (DuplicateBindException e) {
			throwException = true;
		}
		assertTrue(throwException);
	}
	

	/**
	 * Testen der Provider-Funktionalitaet,
	 * d.h. der Container muss bei einer Implementierungsklasse, die das Interface Provider
	 * implementiert, nicht die Klasse selbst sondern ein Object der zu prividenden Klasse liefern
	 * (also ein get auf dieser Klasse aufrufen)
	 *
	 */
	@Test
	public void testProvider() {
		System.out.println("=== testProvider ===");
		witContainer.bind(SampleStringProvider.class);
		witContainer.bind(SampleSimpleClassProvider.class);
		
		witContainer.bind("message").toProvider(SampleStringProvider.class);
		witContainer.bind(SimpleClass.class).toProvider(SampleSimpleClassProvider.class);

		SimpleClass o = witContainer.getInstance(SimpleClass.class); 
		assertNotNull("SimpleClass-Instance is null! ", o);
		String s = o.getMsg();
		assertEquals("instance by provider", s);
		
		SimpleClass o2 = witContainer.getInstance(SimpleClass.class);
		assertEquals(o, o2);
	}
	
	@Test
	public void testInjectionOverExtendedClass() {
		witContainer.bind(AExtended.class);
		
		AExtended a = witContainer.getInstance(AExtended.class);
		assertNotNull(a.getB());
	}
	
	
	@Test
	public void testThreadSafeGetInstance() {
		//TODO
	}
	
	
	@Test
	public void testRuntime() {
		System.out.println("\n=== testRuntime ===");
		long time = System.currentTimeMillis();
		witContainer = new WitContainer();
		bindClasses();
		for(int i=1; i< 4500; i++) {
			witContainer.bind("key"+i).to("xxx");
		}
		time = printRunTime(time);
		
		a = witContainer.getInstance(A.class);
		b = witContainer.getInstance(B.class);
		time = printRunTime(time);
	}

	
	@Test
	public void testRuntimeWithInitialCapa() {
		System.out.println("\n=== testRuntimeWithInitialCapa ===");
		long time = System.currentTimeMillis();
		witContainer = new WitContainer(5000);
		bindClasses();
		for(int i=1; i< 4500; i++) {
			witContainer.bind("key"+i).to("xxx");
		}
		time = printRunTime(time);
		
		a = witContainer.getInstance(A.class);
		b = witContainer.getInstance(B.class);
		time = printRunTime(time);
	}


	private static long printRunTime(long time) {
		long currentTime = System.currentTimeMillis();
		System.out.println("runtime= "+(currentTime-time)+"ms");
		return currentTime;
	}
	

}
