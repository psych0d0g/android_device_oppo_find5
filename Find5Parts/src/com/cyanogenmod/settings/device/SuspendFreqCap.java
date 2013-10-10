package com.cyanogenmod.settings.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

public class SuspendFreqCap extends ListPreference implements OnPreferenceChangeListener {

	private static final String FILE = "/sys/kernel/cpufreq_cap/screen_off_max_freq";
	private static final String FILE_ENABLE = "/sys/kernel/cpufreq_cap/screen_off_cap";
	private static final String STEPS_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    
    public SuspendFreqCap(Context context) {
		super(context);
		
        String availableFrequenciesLine = Utils.getFileValue(STEPS_PATH, "");
        if (availableFrequenciesLine != null) {
        	String[] frequencies = availableFrequenciesLine.split(" ");

        	List<String> entriesList = new ArrayList<String>();
        	entriesList.add("Disabled");
        	entriesList.addAll(Arrays.asList(frequencies));
        	CharSequence[] entries = entriesList.toArray(new CharSequence[entriesList.size()]);

        	List<String> entriyValuesList = new ArrayList<String>();
        	entriyValuesList.add("0");
        	entriyValuesList.addAll(Arrays.asList(frequencies));
        	CharSequence[] entryValues = entriyValuesList.toArray(new CharSequence[entriyValuesList.size()]);

    		setEntries(entries);
    		setEntryValues(entryValues);
        }
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
        return sharedPrefs.getString(DeviceSettings.KEY_SUSPEND_CAP_FREQ, value);
	}
	
    /**
     * Restore Sweep2Wake stroke setting from SharedPreferences. (Write to kernel.)
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
}
