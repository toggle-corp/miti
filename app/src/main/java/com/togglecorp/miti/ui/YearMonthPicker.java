package com.togglecorp.miti.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.Translator;


@SuppressLint("ValidFragment")
public class YearMonthPicker extends DialogFragment {
    private final Listener mListener;
    private final int mDefaultYear;
    private final int mDefaultMonth;

    public interface Listener {
        void onSelect(int year, int month);
    }

    public YearMonthPicker(Listener listener, int defaultYear, int defaultMonth) {
        mListener = listener;
        mDefaultYear = defaultYear;
        mDefaultMonth = defaultMonth;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View dialogView = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_year_month_picker, null);

        // The number pickers
        final NumberPicker yearPicker = (NumberPicker) dialogView.findViewById(R.id.year);
        final NumberPicker monthPicker = (NumberPicker) dialogView.findViewById(R.id.month);

        // Set ok, cancel buttons
        builder.setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSelect(yearPicker.getValue(), monthPicker.getValue());
                        getDialog().cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });

        // Years and MONTHS_NEPALI strings
        String[] years = new String[DateUtils.getNumYears()];
        for (int i=0; i<years.length; i++) {
            years[i] = Translator.getNumber((DateUtils.startNepaliYear + i)+"");
        }
        String[] months = Translator.MONTHS_NEPALI;

        yearPicker.setMinValue(DateUtils.startNepaliYear);
        yearPicker.setMaxValue(DateUtils.startNepaliYear + DateUtils.getNumYears()-1);
        yearPicker.setDisplayedValues(years);
        yearPicker.setValue(mDefaultYear);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(months);
        monthPicker.setValue(mDefaultMonth);
        return builder.create();
    }
}
