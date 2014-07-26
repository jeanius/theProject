/**
 * Code from:
 * 
 * http://stackoverflow.com/questions/1714297/android-view-setidint-id-programmatically-how-to-avoid-id-conflicts/15442898#15442898
 * 
 * To provide unique IDs for dynamically added views.
 * 
*/

package com.jeanpower.reggieproject;

import java.util.concurrent.atomic.AtomicInteger;

public class Util {
	
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Dynamically Generate a value suitable for use in as ID
	 * @return a generated ID value
	 */
	public static int generateViewId() {
	    for (;;) {
	        final int result = sNextGeneratedId.get();
	        // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
	        int newValue = result + 1;
	        if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
	        if (sNextGeneratedId.compareAndSet(result, newValue)) {
	            return result;
	        }
	    }
	}

}
