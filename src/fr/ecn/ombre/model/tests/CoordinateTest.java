package fr.ecn.ombre.model.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.ecn.ombre.model.Coordinate;

public class CoordinateTest {
	
	protected Coordinate coordinate;

	@Before
	public void setUp() throws Exception {
		//Create a test Coordinate object
		this.coordinate = new Coordinate("47\"14'56", "N");
	}

	@Test
	public void testToDouble() {
		assertEquals(47.24888888888889, this.coordinate.toDouble(), 0.000001);
	}

}