package crmcontainerTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import crmcontainer.ConsoleMonitor;
import crmcontainer.CrmContainer;
import crmcontainer.DuplicateBindException;
import crmcontainerTest.sample.A;
import crmcontainerTest.sample.AExtended;
import crmcontainerTest.sample.B;
import crmcontainerTest.sample.C;
import crmcontainerTest.sample.SampleSimpleClassProvider;
import crmcontainerTest.sample.SampleStringProvider;
import crmcontainerTest.sample.SimpleClass;

public class InjectOptionalTest {

	private static CrmContainer crmContainer;
	private static A a;
	private static B b;


	@Before
	public void setUp() throws Exception {
		crmContainer = new CrmContainer();
		crmContainer.setMonitor(new ConsoleMonitor());
		crmContainer.bind(A.class);
		crmContainer.bind(B.class);
		crmContainer.bind("db_name").to("db1");
	}

	

	/**
	 * Testen der korrekten Erzeugung von Class B mit
	 * - seiner optionalen Abhaengikeit zu Classe C (hier null)
	 */
	@Test
	public void testB() {
		b = crmContainer.getInstance(B.class);
		assertTrue(b instanceof B);
		C c = b.getC();
		assertNull(c);
	}

	/**
	 * Testen der korrekten Erzeugung von Class B mit
	 * - seiner optionalen Abhaengikeit zu Classe C (hier gesetzt)
	 */
	@Test
	public void testBwithC() {
		crmContainer.bind(C.class);
		b = crmContainer.getInstance(B.class);
		assertTrue(b instanceof B);
		C c = b.getC();
		assertNotNull(c);
	}

	

}
