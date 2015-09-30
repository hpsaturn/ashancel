package org.hansel.myAlert;

/**
 * Created by hasus on 8/12/15.
 */
public class Config {

	public static final boolean DEBUG=true;    // ==> R E V I S A R  P A R A  T I E N D A S //
	public static final boolean DEBUG_SERVICE = true;
	public static final boolean DEBUG_LOCATION = false;
	public static final boolean DEBUG_TASKS = true;

    // location
 	public static final int     TIME_AFTER_START   = 15;  // Start on x seconds after init Scheduler
	public static final long    DEFAULT_INTERVAL   = 1000 * 30 * 1;  // Default interval for background service: 3 minutes
	public static final long 	DEFAULT_INTERVAL_FASTER = 1000 * 30 * 2;
	public static final float   ACCURACY 	= 200;
	public static final long    LOCATION_ROUTE_INTERVAL = 1000 * 60;
	public static final long    LOCATION_MAP_INTERVAL = 1000 * 120;

	public static final int		VIBRATION_TIME_SMS = 2000;

}
