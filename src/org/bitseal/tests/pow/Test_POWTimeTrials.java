package org.bitseal.tests.pow;

import java.security.SecureRandom;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.bitseal.pow.POWProcessor;

import android.util.Log;

/**
 * Runs time trials for Bitmessage POW in Bitseal, using the
 * POW formula from Protocol Version 3. 
 * 
 * @author Jonathan Coe
 *
 */
public class Test_POWTimeTrials extends TestCase
{
	private static final int TRIALS_TO_RUN = 1;
	
	private static final int PAYLOAD_LENGTH = 800;
	
	private static final long NONCE_TRIALS_PER_BYTE = 1000;
	private static final long EXTRA_BYTES = 1000;
	
	private static final String TAG = "POW_TIME_TRIALS_VERSION_3";
	
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testPOWTimeTrials()
	{
		byte[] payload = new byte[PAYLOAD_LENGTH];
		new SecureRandom().nextBytes(payload);
		POWProcessor powProc = new POWProcessor();
		ArrayList<Long> times = new ArrayList<Long>();
		
		// Set an expiration time of 1 hour from now
		long expirationTime = (System.currentTimeMillis() / 1000) + 3600;
		
		for (int i = 0; i < TRIALS_TO_RUN; i++)
		{
 			long startTime = 0;
 			long endTime = 0;
 			long timeTaken = 0;
			
			//----------------------------------BEGIN TIMED TEST----------------------------------------------
 			startTime = System.nanoTime();
			
			long powNonce = powProc.doPOW(payload, expirationTime, NONCE_TRIALS_PER_BYTE, EXTRA_BYTES);
			
 			endTime = System.nanoTime();
 			//----------------------------------END TIMED TEST----------------------------------------------
 			
 			assertTrue(powProc.checkPOW(payload, powNonce, expirationTime, NONCE_TRIALS_PER_BYTE, EXTRA_BYTES));
 			timeTaken = endTime - startTime;
 			long timeTakenInSeconds = timeTaken / 1000000000; // Convert from nanoseconds to seconds
 			Log.i(TAG, "Time taken in seconds:          " + timeTakenInSeconds);
 			times.add(Long.valueOf(timeTakenInSeconds));
		}
		
 		// Get the average time taken to do the POW
 		long sumOfTimes = 0;
 		for (Long l : times)
 		{
 			sumOfTimes += l;
 		}
 		long averageTime = sumOfTimes / times.size();
 		Log.i(TAG, "Average time taken in seconds: " + averageTime);
	}
}