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

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		crmContainer = new CrmContainer();
		crmContainer.setMonitor(new ConsoleMonitor());
		crmContainer.bind(A.class);
		crmContainer.bind(B.class);
		crmContainer.bind("db_name", "db1");

		a = crmContainer.getInstance(A.class);
		b = crmContainer.getInstance(B.class);
}

	@Before
	public void setUp() throws Exception {
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

	@Test
	public void testB() {
		assertTrue(b instanceof B);
		assertEquals(b.getA(), a);
		assertTrue(b.isStarted());
	}

	@Test
	public void testBindConstant() {
		crmContainer.bind("key1", "value1");
		assertEquals("value1", crmContainer.getInstance("key1"));
	}
}
