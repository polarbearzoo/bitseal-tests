package org.bitseal.tests.network;

import java.util.concurrent.TimeUnit;

import org.bitseal.core.AddressProcessor;
import org.bitseal.core.PubkeyProcessor;
import org.bitseal.data.Pubkey;
import org.bitseal.database.DatabaseContentProvider;
import org.bitseal.network.ServerCommunicator;

import android.content.Context;
import android.os.SystemClock;
import android.test.AndroidTestCase;
import android.util.Log;

/** 
 * Tests the requestPubkeyFromServer() method in the ServerCommunicator class.
 * This applies to addresses of version 3 and below.<br><br>
 * 
 * Note: The use of AndroidTestCase is necessary in order to ensure that 
 * the application context will be available when the main body of the test 
 * runs. The static methods provided by the ApplicationContextProvider class
 * will often throw a null pointer exception if the app has only been running
 * for a very short time. 
**/
public class Test_RequestPubkeyFromServer extends AndroidTestCase
{
	// Start the test with a String representing a Bitmessage address. For 'real world' testing,
	// this should be an address owned by a real Bitmessage node that is active on the network.
	private static final String TEST_ADDRESS = "BM-2DBSnCkJFSVEAyJLtJWYhtdQEJVEojzbc7";
	
	private static final String TAG = "TEST_REQUEST_PUBKEY_FROM_SERVER";
	
	protected void setUp() throws Exception
	{
		super.setUp();
		
		// Open the database
		DatabaseContentProvider.openDatabase();
		SystemClock.sleep(5000); // We have to allow some extra time for the database to be opened
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	// Note: This override of the setContext() method is necessary because AndroidTestCase
	// will in some cases return a null application context. 
	// See https://stackoverflow.com/questions/6516441/why-does-androidtestcase-getcontext-getapplicationcontext-return-null
	// Credit to James Wald on StackOverflow for this method
	@Override
    public void setContext(Context context) 
	{
        super.setContext(context);

        long endTime = SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(50);

        while (null == context.getApplicationContext())
        {
            if (SystemClock.elapsedRealtime() >= endTime)
            {
                Log.e(TAG, "Attempt to get application context timed out");
            	fail();
            }
            SystemClock.sleep(50);
        }
	}
	
	public void testRequestPubkeyFromServer()
	{
		// Wait for five seconds in order to make it more likely that we will be able to get application context
		SystemClock.sleep(5000);
		
		// Validate the address - if it is not valid then we should stop the test now
		AddressProcessor addProc = new AddressProcessor();
		assertTrue(addProc.validateAddress(TEST_ADDRESS));
		
		// Extract the address version number from the address
		int[] addressNumbers = addProc.decodeAddressNumbers(TEST_ADDRESS);
		int addressVersion = addressNumbers[0];
		
		// Extract the ripe hash from this address
		byte[] ripeHash = addProc.extractRipeHashFromAddress(TEST_ADDRESS);
		
		// Use the ripe hash extracted from the address to request the corresponding pubkey from a server
		ServerCommunicator servCom = new ServerCommunicator();
		Pubkey pubkey = servCom.requestPubkeyFromServer(TEST_ADDRESS, ripeHash, addressVersion);
		
		// Now check that the pubkey is valid 
		PubkeyProcessor pubProc = new PubkeyProcessor();
		assertTrue(pubProc.validatePubkey(pubkey, TEST_ADDRESS));
	}
}