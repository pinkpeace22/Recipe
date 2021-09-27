package kr.ac.hs.recipe.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import kr.ac.hs.recipe.L;
import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.activity.RecipeActivity;
import kr.ac.hs.recipe.databinding.DialogTimerBottomSheetBinding;
import kr.ac.hs.recipe.fragment.TimerDialogFragment;

public class TimerBottomDialog extends BaseBottomSheetDialog<DialogTimerBottomSheetBinding> {

    public static String TAG = "TimerBottomDialog";
    private TimerDialogFragment timerDialog;

    private Disposable disposalCheckUpdateTimer;
    private Disposable disposalCheckUpdateTimer2;
    private Disposable disposalCheckUpdateTimer3;

    private TimerComplete listener;

    private boolean isTimerPuase = false;
    private boolean isTimerPuase2 = false;
    private boolean isTimerPuase3 = false;

    public interface TimerComplete {
        void onComplete();
    }

    public void setListener(TimerComplete listener) {
        this.listener = listener;
    }

    public static TimerBottomDialog newInstance() {
        Bundle args = new Bundle();
        TimerBottomDialog fragment = new TimerBottomDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public TimerBottomDialog() {
        super(R.layout.dialog_timer_bottom_sheet);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListener();
    }

    private void setListener() {
        binding.btnTimerSetting.setOnClickListener(timerSettingListener);
        binding.btnTimerSetting2.setOnClickListener(timerSettingListener);
        binding.btnTimerSetting3.setOnClickListener(timerSettingListener);


        binding.btnTimerStop.setOnClickListener(timerPauseListener);
        binding.btnTimerStop2.setOnClickListener(timerPauseListener);
        binding.btnTimerStop3.setOnClickListener(timerPauseListener);

        binding.btnTimerReset.setOnClickListener(timerResetListener);
        binding.btnTimerReset2.setOnClickListener(timerResetListener);
        binding.btnTimerReset3.setOnClickListener(timerResetListener);
    }


    private View.OnClickListener timerSettingListener = view -> {
        switch (view.getId()) {
            case R.id.btn_timer_setting:
                showTimerSettingDialog(TimerType.TIMER_1);
                break;
            case R.id.btn_timer_setting2:
                showTimerSettingDialog(TimerType.TIMER_2);
                break;
            case R.id.btn_timer_setting3:
                showTimerSettingDialog(TimerType.TIMER_3);
                break;
        }
    };

    private void showTimerSettingDialog(TimerType timerPreSet) {
        timerDialog = new TimerDialogFragment();
        timerDialog.setCallback((hour, minute, second) -> {
            L.i(":::onTimeSet " + hour + " minute " + minute + " second " + second);
            int totalTime = (hour * 3600) + (minute * 60) + second;
            L.i("::::totalTime " + totalTime);
            timerStart(totalTime, timerPreSet);
        });
        timerDialog.show(getChildFragmentManager(), "TAG_TIMER_DIALOG");
    }


    private void timerStart(long targetTime, TimerType timerType) {
        switch (timerType) {
            case TIMER_1: {
                long totalTime = targetTime - 1;
                disposalCheckUpdateTimer = Flowable.intervalRange(0, targetTime, 0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(countTime -> {
                            L.i("::::::::::::::::(totalTime - countTime) " + (totalTime - countTime));
                            TimerType.sItemRowMap.put(timerType, (totalTime - countTime));
                            updateStartTimerUi(convertTime(totalTime - countTime), timerType);
                        })
                        .doOnComplete(() -> {
                            TimerType.sItemRowMap.put(timerType, 0L);
                            listener.onComplete();
                        }).subscribe();
                break;
            }

            case TIMER_2: {
                long totalTime = targetTime - 1;
                disposalCheckUpdateTimer2 = Flowable.intervalRange(0, targetTime, 0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(countTime -> {
                            TimerType.sItemRowMap.put(timerType, (totalTime - countTime));
                            updateStartTimerUi(convertTime(totalTime - countTime), timerType);
                        })
                        .doOnComplete(() -> {
                            TimerType.sItemRowMap.put(timerType, 0L);
                            listener.onComplete();
                        }).subscribe();
                break;
            }
            case TIMER_3: {
                long totalTime = targetTime - 1;
                disposalCheckUpdateTimer3 = Flowable.intervalRange(0, targetTime, 0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(countTime -> {
                            TimerType.sItemRowMap.put(timerType, (totalTime - countTime));
                            updateStartTimerUi(convertTime(totalTime - countTime), timerType);
                        })
                        .doOnComplete(() -> {
                            TimerType.sItemRowMap.put(timerType, 0L);
                            listener.onComplete();
                        }).subscribe();
                break;
            }
        }

    }

    private View.OnClickListener timerResetListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_timer_reset:
                    TimerType.sItemRowMap.put(TimerType.TIMER_1, 0L);
                    cancelTimer(TimerType.TIMER_1);
                    updateRestTimerUi(TimerType.TIMER_1);
                    break;
                case R.id.btn_timer_reset2:
                    TimerType.sItemRowMap.put(TimerType.TIMER_2, 0L);
                    cancelTimer(TimerType.TIMER_2);
                    updateRestTimerUi(TimerType.TIMER_2);
                    break;
                case R.id.btn_timer_reset3:
                    TimerType.sItemRowMap.put(TimerType.TIMER_3, 0L);
                    cancelTimer(TimerType.TIMER_3);
                    updateRestTimerUi(TimerType.TIMER_3);
                    break;
            }
        }
    };

    private View.OnClickListener timerPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_timer_stop:
                    L.i(":::::btn_timer_stop");
                    if (TimerType.byRemainTime(TimerType.TIMER_1) == 0) {
                        return;
                    }
                    updatePuaseTimerUi(TimerType.TIMER_1);
                    break;
                case R.id.btn_timer_stop2:
                    L.i(":::::btn_timer_stop2");
                    if (TimerType.byRemainTime(TimerType.TIMER_2) == 0) {
                        return;
                    }
                    updatePuaseTimerUi(TimerType.TIMER_2);
                    break;
                case R.id.btn_timer_stop3:
                    L.i(":::::btn_timer_stop3");
                    if (TimerType.byRemainTime(TimerType.TIMER_3) == 0) {
                        return;
                    }
                    updatePuaseTimerUi(TimerType.TIMER_3);
                    break;
            }
        }
    };


    private void cancelTimer(TimerType preSet) {
        switch (preSet) {
            case TIMER_1:
                if (disposalCheckUpdateTimer != null) disposalCheckUpdateTimer.dispose();
                break;
            case TIMER_2:
                if (disposalCheckUpdateTimer2 != null) disposalCheckUpdateTimer2.dispose();
                break;
            case TIMER_3:
                if (disposalCheckUpdateTimer3 != null) disposalCheckUpdateTimer3.dispose();
                break;
        }
    }

    public void allTimerClear(){
        cancelTimer(TimerType.TIMER_1);
        cancelTimer(TimerType.TIMER_2);
        cancelTimer(TimerType.TIMER_3);
    }

    private void updateRestTimerUi(TimerType timerType) {
        switch (timerType) {
            case TIMER_1:
                if (binding != null) {
                    isTimerPuase = false;
                    binding.tvTimerValue.setText("");
                    binding.btnTimerStop.setTag("일시 정지");
                }
                break;
            case TIMER_2:
                if (binding != null) {
                    isTimerPuase2 = false;
                    binding.tvTimerValue2.setText("");
                    binding.btnTimerStop2.setTag("일시 정지");
                }
                break;
            case TIMER_3:
                if (binding != null) {
                    isTimerPuase3 = false;
                    binding.tvTimerValue3.setText("");
                    binding.btnTimerStop3.setTag("일시 정지");
                }
                break;
        }

    }

    private void updatePuaseTimerUi(TimerType timerType) {
        switch (timerType) {
            case TIMER_1:
                if (binding != null) {
                    if (!isTimerPuase) {
                        binding.btnTimerStop.setText("재시작");
                        isTimerPuase = true;
                        cancelTimer(TimerType.TIMER_1);
                    } else {
                        binding.btnTimerStop.setText("일시 정지");
                        isTimerPuase = false;
                        timerStart(TimerType.byRemainTime(TimerType.TIMER_1), TimerType.TIMER_1);
                    }
                }
                break;
            case TIMER_2:
                if (binding != null) {
                    if (!isTimerPuase2) {
                        binding.btnTimerStop2.setText("재시작");
                        isTimerPuase2 = true;
                        cancelTimer(TimerType.TIMER_2);
                    } else {
                        binding.btnTimerStop2.setText("일시 정지");
                        isTimerPuase2 = false;
                        timerStart(TimerType.byRemainTime(TimerType.TIMER_2), TimerType.TIMER_2);
                    }
                }
                break;
            case TIMER_3:
                if (binding != null) {
                    if (!isTimerPuase3) {
                        binding.btnTimerStop3.setText("재시작");
                        isTimerPuase3 = true;
                        cancelTimer(TimerType.TIMER_3);
                    } else {
                        binding.btnTimerStop3.setText("일시 정지");
                        isTimerPuase3 = false;
                        timerStart(TimerType.byRemainTime(TimerType.TIMER_3), TimerType.TIMER_3);
                    }
                }
                break;
        }

    }


    private void updateStartTimerUi(String remain, TimerType timerType) {
        switch (timerType) {
            case TIMER_1:
                if (binding != null) {
                    binding.tvTimerValue.setText(remain);
                }
                break;
            case TIMER_2:
                if (binding != null) {
                    binding.tvTimerValue2.setText(remain);
                }
                break;
            case TIMER_3:
                if (binding != null) {
                    binding.tvTimerValue3.setText(remain);
                }
                break;
        }

    }


    private String convertTime(long time) {
        int sec = (int) time;
        int min = sec / 60;
        int hour = min / 60;
        sec = sec % 60;
        min = min % 60;
        return String.format("%d:%d:%d", hour, min, sec);
    }



}
