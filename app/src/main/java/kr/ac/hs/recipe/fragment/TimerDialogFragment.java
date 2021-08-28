package kr.ac.hs.recipe.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import java.lang.reflect.Field;

import kr.ac.hs.recipe.L;
import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.databinding.DialogTimerSettingBinding;

public class TimerDialogFragment extends DialogFragment {


    private DialogTimerSettingBinding binding;
    private TimeSetCallback callback;

    public interface TimeSetCallback {
        void onTimeSet(int hour, int minute, int second);
    }


    public TimerDialogFragment() {

    }

    public void setCallback(TimeSetCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_timer_setting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setStartValueRange(binding.hour, 1, 24, 1);
        setStartValueRange(binding.min, 1, 60, 1);
        setStartValueRange(binding.sec, 1, 60, 1);


        binding.btnTimeSet.setOnClickListener(view1 -> {
            String hour = String.valueOf(binding.hour.getValue() - 1);
            String minute = String.valueOf(binding.min.getValue() - 1);
            String second = String.valueOf(binding.sec.getValue() - 1);

            L.i(":::hour " + hour + " minute " + minute + " second " + second);
            if (callback != null) {
                dismiss();
                callback.onTimeSet(Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));
            }
        });
    }

    private void setStartValueRange(NumberPicker view, int min, int max, int val) {
        if (view != null) {
            int value = val;
            int pos = 0;

            int minValue = min;
            int maxValue = max;
            int interval = value;
            int cnt = 0;

            L.i("::::버퍼생성...." + (((maxValue - minValue) / interval) + 1));
            String[] valueSet = new String[((maxValue - minValue) / interval) + 1];
            for (int i = minValue; i <= maxValue; i += interval) {
                String data = String.valueOf(i - 1);
                valueSet[cnt++] = data;
            }
            view.setDisplayedValues(null);
            view.setMaxValue(cnt);

            view.setMinValue(1);

            view.setDisplayedValues(valueSet);

            view.setValue(pos + 1);

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
