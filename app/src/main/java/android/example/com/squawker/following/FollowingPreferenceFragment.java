/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package android.example.com.squawker.following;

import android.content.SharedPreferences;
import android.example.com.squawker.R;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;


/**
 * Shows the list of instructors you can follow
 */
// TODO (1) Implement onSharedPreferenceChangeListener
public class FollowingPreferenceFragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private final static String LOG_TAG = FollowingPreferenceFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Add visualizer preferences, defined in the XML file in res->xml->preferences_squawker
        addPreferencesFromResource(R.xml.following_squawker);
    }

    /* Triggered whenever a preference is changed */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        // TODO (2) When a SharedPreference changes, check which preference it is and subscribe or
        // un-subscribe to the correct topics.

        // Ex. FirebaseMessaging.getInstance().subscribeToTopic("key_lyla");
        // subscribes to Lyla's squawks.

        // HINT: Checkout res->xml->following_squawker.xml. Note how the keys for each of the
        // preferences matches the topic to subscribe to for each instructor.

        Preference preference = findPreference(key);
        if (preference != null && preference instanceof SwitchPreferenceCompat) {
            // Retrieve the current state
            boolean isOn = sharedPreferences.getBoolean(key, false);
            if (isOn) {
                // Then subscribe
                FirebaseMessaging.getInstance().subscribeToTopic(key);
                Log.e(LOG_TAG, "Subscribing to: " + key);
            } else {
                // Then unsubscribe
                FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                Log.e(LOG_TAG, "Un-subscribing from: " + key);
            }
        }

    }

    // TODO (3) Make sure to register and unregister this as a Shared Preference Change listener, in
    // onCreate and onDestroy.


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add the listener
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove the listener
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
