/*
 * Copyright (C) 2021 AospExtended ROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cherish.settings.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import androidx.preference.PreferenceViewHolder;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import android.net.Uri;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;

import com.bumptech.glide.Glide;

import com.android.internal.util.cherish.clock.ClockFace;
import com.android.internal.util.cherish.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONException;

public class LockscreenClock extends SettingsPreferenceFragment {

    private static final String DEFAULT_CLOCK = "com.android.keyguard.clock.DefaultClockController";

    private List<ClockFace> mClocks;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.lock_screen_custom_clock_face_title);

        mClocks = new ThemeUtils(getActivity()).getClocks();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.item_view, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.layout);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        ClockAdapter mClockAdapter = new ClockAdapter(getActivity(), mClocks);
        mRecyclerView.setAdapter(mClockAdapter);

        return view;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CHERISH_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ClockViewHolder> {

        List<ClockFace> sClocks;
        Context context;
        ClockFace mSelectedClock;
        ClockFace mAppliedClock;

        public ClockAdapter(Context context, List<ClockFace> sClocks) {
            this.context = context;
            this.sClocks = sClocks;
        }

        @Override
        public ClockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option, parent, false);
            ClockViewHolder vh = new ClockViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ClockViewHolder holder, final int position) {
            ClockFace mClockFace = sClocks.get(position);

            String title = mClockFace.getTitle();
            String id = mClockFace.getId();
            Uri thumbnail = mClockFace.getThumbnailUri();

            Glide.with(getActivity()).load(thumbnail).into(holder.image);

            holder.name.setText(title);

            if (id.equals(getCurrentClock())) {
                mAppliedClock = mClockFace;
                if (mSelectedClock == null) {
                    mSelectedClock = mClockFace;
                }
            }

            holder.itemView.setActivated(mClockFace.equals(mSelectedClock));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateActivatedStatus(mSelectedClock, false);
                    updateActivatedStatus(mClockFace, true);
                    mSelectedClock = mClockFace;
                    Settings.Secure.putString(getActivity().getContentResolver(),
                            Settings.Secure.LOCK_SCREEN_CUSTOM_CLOCK_FACE, id);
                }
            });
        }

        @Override
        public int getItemCount() {
            return sClocks.size();
        }

        public class ClockViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView image;
            public ClockViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.option_label);
                image = (ImageView) itemView.findViewById(R.id.option_thumbnail);
            }
        }

        private void updateActivatedStatus(ClockFace clock, boolean isActivated) {
            int index = mClocks.indexOf(clock);
            if (index < 0) {
                return;
            }
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(index);
            if (holder != null && holder.itemView != null) {
                holder.itemView.setActivated(isActivated);
            }
        }
    }

    public String getCurrentClock() {
        String value = Settings.Secure.getString(getActivity().getContentResolver(),
                     Settings.Secure.LOCK_SCREEN_CUSTOM_CLOCK_FACE);

        if (value == null || value.isEmpty()) return DEFAULT_CLOCK;

        try {
            final JSONObject json = new JSONObject(value);
            return json.getString("clock");
        } catch (JSONException ex) {
            return value;
        }
    }
}
