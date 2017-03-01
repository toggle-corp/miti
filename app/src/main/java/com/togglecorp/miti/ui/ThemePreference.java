package com.togglecorp.miti.ui;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.togglecorp.miti.R;

public class ThemePreference extends Preference implements View.OnClickListener {
    public ThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);


        // NOTE: To save, persistInt()
    }

    @Override
    protected View onCreateView(ViewGroup parent )
    {
        LayoutInflater inflator = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.preference_theme, parent, false);

        view.findViewById(R.id.theme_purple).setOnClickListener(this);
        view.findViewById(R.id.theme_green).setOnClickListener(this);
        view.findViewById(R.id.theme_red).setOnClickListener(this);

        return view;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
//            mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
//            mCurrentValue = (Integer) defaultValue;
//            persistInt(mCurrentValue);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.theme_purple:
                persistString("Purple");
                break;
            case R.id.theme_green:
                persistString("Green");
                break;
            case R.id.theme_red:
                persistString("Red");
                break;
        }
    }
}
