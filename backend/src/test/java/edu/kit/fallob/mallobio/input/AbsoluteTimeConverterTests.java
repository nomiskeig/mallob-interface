package edu.kit.fallob.mallobio.input;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.fallob.dataobjects.JobConfiguration;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.MallobTimeListener;

public class AbsoluteTimeConverterTests {
	
	public static final int MINUTES_TO_S = 60;
	public static final int MINUTES_TO_MS = MINUTES_TO_S * 1000;
	
	
	private static final int MINUTES_SINCE_MALLOB_START = 2;
	private static final int MINUTES_UNTIL_ARRIVAL = 3;
	
	
	public static final int BUFFER_IN_S = 10;
	
	private static String timeInThreeMinutesAsISO;
	
	
	@BeforeEach
	public void beforeEach() {
		timeInThreeMinutesAsISO = getISOString(getDate(MINUTES_UNTIL_ARRIVAL * MINUTES_TO_MS));
	}
	
	private static String getISOString(Date d) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(tz);
		return df.format(d);
	}

	/**
	 * Current date + offset in MS
	 * @param i offset to the current time in milliseconds
	 * @return
	 */
	private static Date getDate(int offset) {
		Calendar current = Calendar.getInstance();
		return new Date(current.getTimeInMillis() + offset);
	}


	@Test
	public void testTimeConversion() {	
		MallobTimeListener.getInstance().setSecondsSinceStart(MINUTES_SINCE_MALLOB_START * MINUTES_TO_S);
		double timeInS = AbsoluteTimeConverter.convertTimeToDouble(timeInThreeMinutesAsISO);
		int expectedTimeInS = (MINUTES_SINCE_MALLOB_START + MINUTES_UNTIL_ARRIVAL) * MINUTES_TO_S;
		assertTrue(timeInS >= expectedTimeInS - BUFFER_IN_S);
		assertTrue(timeInS <= expectedTimeInS + BUFFER_IN_S);
	}
	
	@Test 
	public void testTimeConversionForIllegalTime() {
		String illegalTimeString = getISOString(getDate((-1) * 3 * MINUTES_TO_MS));
		assertTrue(AbsoluteTimeConverter.convertTimeToDouble(illegalTimeString) == JobConfiguration.DOUBLE_NOT_SET);
	}

	
}
