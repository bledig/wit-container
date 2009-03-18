package witcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import witcontainer.ConsoleMonitor;
import witcontainer.WitContainer;
import witcontainer.DuplicateBindException;
import witcontainer.sample.A;
import witcontainer.sample.AExtended;
import witcontainer.sample.B;
import witcontainer.sample.C;
import witcontainer.sample.SampleSimpleClassProvider;
import witcontainer.sample.SampleStringProvider;
import witcontainer.sample.SimpleClass;


public class InjectOptionalTest {

	private static WitContainer crmContainer;
	@SuppressWarnings("unused")
	private static A a;
	private static B b;


	@Before
	public void setUp() throws Exception {
		crmContainer = new WitContainer();
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
