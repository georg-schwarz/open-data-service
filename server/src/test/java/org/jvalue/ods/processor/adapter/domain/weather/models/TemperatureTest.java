package org.jvalue.ods.processor.adapter.domain.weather.models;

import org.junit.Assert;
import org.junit.Test;

public class TemperatureTest {

	private final static double DELTA = 0.001;

	@Test
	public void testGetValueInKelvin() {
		Temperature celsiusL = new Temperature(0, TemperatureType.CELSIUS);
		Temperature celsiusM = new Temperature(26.85, TemperatureType.CELSIUS);
		Temperature celsiusH = new Temperature(500, TemperatureType.CELSIUS);

		Temperature fahrenheitL = new Temperature(32, TemperatureType.FAHRENHEIT);
		Temperature fahrenheitM = new Temperature(80.33, TemperatureType.FAHRENHEIT);
		Temperature fahrenheitH = new Temperature(932, TemperatureType.FAHRENHEIT);

		Assert.assertEquals(273.15, celsiusL.getValueInKelvin(), DELTA);
		Assert.assertEquals(300, celsiusM.getValueInKelvin(), DELTA);
		Assert.assertEquals(773.15, celsiusH.getValueInKelvin(), DELTA);

		Assert.assertEquals(273.15, fahrenheitL.getValueInKelvin(), DELTA);
		Assert.assertEquals(300, fahrenheitM.getValueInKelvin(), DELTA);
		Assert.assertEquals(773.15, fahrenheitH.getValueInKelvin(), DELTA);
	}


	@Test
	public void testEquals() {
		Temperature tempA = new Temperature(31, TemperatureType.CELSIUS);
		Temperature tempB = new Temperature(31, TemperatureType.CELSIUS);
		Temperature tempC = new Temperature(31, TemperatureType.KELVIN);
		Temperature tempD = new Temperature(31, TemperatureType.FAHRENHEIT);

		Assert.assertEquals(tempA, tempB);
		Assert.assertNotEquals(tempA, tempC);
		Assert.assertNotEquals(tempA, tempD);
	}
}
