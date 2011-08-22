package fr.ecn.ombre.tests.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.ecn.ombre.model.Coordinate;

public class CoordinateTest {
	
	protected Coordinate coordinate;

	@Before
	public void setUp() throws Exception {
		//Create a test Coordinate object
		this.coordinate = new Coordinate(47, 14, 56, "N");
	}

	@Test
	public void testGetDecimalDegrees() {
		assertEquals(47.24888888888889, this.coordinate.getDecimalDegrees(), 0.000001);
	}
	
	@Test
	public void testGetDMSString() {
		assertEquals("47°14'56.0\"", this.coordinate.getDMSString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParseCoordinateWithToMuchNumber() {
		Coordinate.parseCoordinate("14°45'64\"58", "N");
	}
	
	@Test(expected=NumberFormatException.class)
	public void testParseCoordinateWithInvalidNumber() {
		Coordinate.parseCoordinate("14°4f5'64\"", "N");
	}

}