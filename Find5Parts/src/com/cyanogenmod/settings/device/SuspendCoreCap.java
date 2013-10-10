package com.cyanogenmod.settings.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

public class SuspendCoreCap extends ListPreference implements OnPreferenceChangeListener {

	private static final String FILE = "/sys/devices/system/cpu/cpuquiet/cpuquiet_driver/screen_off_cap";
	private static final String FILE_ENABLE = "/sys/devices/system/cpu/cpuquiet/cpuquiet_driver/screen_off_max_cpus";
    private static final String NUM_OF_CPUS_PATH = "/sys/devices/system/cpu/present";
    
    public SuspendCoreCap(Context context) {
		super(context);

		CharSequence[] entries = new CharSequence[4];
		entries[0]="Disabled";
		int numCpus = getNumOfCpus();
		for (int i = 1; i <numCpus; i++){
			entries[i]= String.valueOf(i) + " core";
		}

		CharSequence[] entryValues = new CharSequence[4];
		for (int i = 1; i <numCpus; i++){
			entryValues[i]= String.valueOf(i);
		}
		
		setEntries(entries);
		setEntryValues(entryValues);
	}

    public static boolean isSupported() {
        return Utils.fileWritable(FILE) && Utils.fileWritable(FILE_ENABLE);
    }

	public static String getValue(Context context) {
		String value = null;
		if (isDisabled()){
			value = "0";
		} else {
			value = Utils.getFileValue(FILE, "0");			
		}
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(DeviceSettings.KEY_SUSPEND_CAP_CORE, value);
	}
	
    /**
     * Restore SuspendCoreCap setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

		String value = getValue(context);
		if (value.equals("0")){
			disableCap();
		} else {
			enableCap();
			Utils.writeValue(FILE, value);
		}
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Utils.writeValue(FILE, (String) newValue);
        return true;
    }
    
    private static void disableCap(){
        Utils.writeValue(FILE_ENABLE, "0");
    }

    private static void enableCap(){
        Utils.writeValue(FILE_ENABLE, "1");
    }
    
    private static boolean isDisabled(){
		String value = Utils.getFileValue(FILE_ENABLE, "0");
		return value.equals("0");
    }
    /**
     * Get total number of cpus
     * @return total number of cpus
     */
    private static int getNumOfCpus() {
        int numOfCpu = 1;
        String numOfCpus = Utils.getFileValue(NUM_OF_CPUS_PATH, "1");
        String[] cpuCount = numOfCpus.split("-");
        if (cpuCount.length > 1) {
            try {
                int cpuStart = Integer.parseInt(cpuCount[0]);
                int cpuEnd = Integer.parseInt(cpuCount[1]);

                numOfCpu = cpuEnd - cpuStart + 1;

                if (numOfCpu < 0)
                    numOfCpu = 1;
            } catch (NumberFormatException ex) {
                numOfCpu = 1;
            }
        }
        return numOfCpu;
    }
}
