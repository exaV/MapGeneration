package ch.fhnw.util;

import java.util.prefs.Preferences;

public class PreferencesStore {

	public static Preferences get() {
		return Preferences.userNodeForPackage(PreferencesStore.class);
	}

}
