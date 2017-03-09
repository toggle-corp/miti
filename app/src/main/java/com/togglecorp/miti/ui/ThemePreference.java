package com.togglecorp.miti.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.preference.Preference;
import android.support.v4.content.ContextCompat;
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
    protected View onCreateView(ViewGroup parent ) {
        super.onCreateView(parent);

        LayoutInflater inflator = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.preference_theme, parent, false);

        view.findViewById(R.id.theme_purple).setOnClickListener(this);
        ((GradientDrawable)view.findViewById(R.id.theme_purple).getBackground()).setColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimaryPurple));
        view.findViewById(R.id.theme_green).setOnClickListener(this);
        ((GradientDrawable)view.findViewById(R.id.theme_green).getBackground()).setColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimaryGreen));
        view.findViewById(R.id.theme_red).setOnClickListener(this);
        ((GradientDrawable)view.findViewById(R.id.theme_red).getBackground()).setColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimaryRed));
        view.findViewById(R.id.theme_black).setOnClickListener(this);
        ((GradientDrawable)view.findViewById(R.id.theme_black).getBackground()).setColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimaryDarkBlack));
        view.findViewById(R.id.theme_white).setOnClickListener(this);
        ((GradientDrawable)view.findViewById(R.id.theme_white).getBackground()).setColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimaryWhite));

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
            case R.id.theme_black:
                persistString("Black");
                break;
            case R.id.theme_white:
                persistString("White");
                break;
        }
    }
}
