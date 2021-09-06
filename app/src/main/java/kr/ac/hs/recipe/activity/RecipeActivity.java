package kr.ac.hs.recipe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.epoxy.EpoxyAsyncUtil;
import com.airbnb.epoxy.EpoxyRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import kr.ac.hs.recipe.L;
import kr.ac.hs.recipe.MediaManager;
import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.adapter.recipe.RecepieController;
import kr.ac.hs.recipe.fragment.TimerDialogFragment;
import kr.ac.hs.recipe.recipeDB.SummaryData;
import kr.ac.hs.recipe.recipeDB.ingredientsData;
import kr.ac.hs.recipe.recipeDB.stepData;
import kr.ac.hs.recipe.ui.search.ListView;

public class RecipeActivity extends BasicActivity {
    private DatabaseReference mTotalReference = null; //전체 항목.
    private DatabaseReference mReceipeReference = null; //레시피 항목
    private DatabaseReference mIrdntListReference = null; //재료 항목.
    private EpoxyRecyclerView rvRecipeStep;


    //타이머 1번 항목 뷰
    private TextView tvCountDownValue;
    private Button btnTimerSetting; // 타임설정
    private Button btnTimerStop; // 정지
    private Button btnTimerReset; // 초기화


    //타이머 2번 항목 뷰
    private TextView tvCountDownValue2;
    private Button btnTimerSetting2; // 타임설정
    private Button btnTimerStop2; // 정지
    private Button btnTimerReset2; // 초기화


    //타이머 3번 항목 뷰
    private TextView tvCountDownValue3;
    private Button btnTimerSetting3; // 타임설정
    private Button btnTimerStop3; // 정지
    private Button btnTimerReset3; // 초기화


    //    private RecipeAdapter adapter;
    private RecepieController recepieController;
    private TimerDialogFragment timerDialog;
    private MediaManager mp3MediaManager;
    private ListView item;


    private Disposable disposalCheckUpdateTimer;
    private Disposable disposalCheckUpdateTimer2;
    private Disposable disposalCheckUpdateTimer3;

    private boolean isTimerPuase = false;
    private boolean isTimerPuase2 = false;
    private boolean isTimerPuase3 = false;


    public enum TimerPreSet {
        TIMER_1(0),
        TIMER_2(0),
        TIMER_3(0);

        long remain;


        static private final HashMap<TimerPreSet, Long> sItemRowMap;

        static {
            sItemRowMap = new HashMap<>(TimerPreSet.values().length);
            for (TimerPreSet type : TimerPreSet.values()) {
                sItemRowMap.put(type, type.remain);
            }
        }

        TimerPreSet(long remainAmount) {
            this.remain = remainAmount;
        }

        public static Long byRemainTime(TimerPreSet timerPreSet) {
            return sItemRowMap.get(timerPreSet);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        item = Parcels.unwrap(getIntent().getParcelableExtra("EXTRA_SELECTED_ITEM"));
        setToolbarTitle(item.getName());


        L.i(":::::::::::::::::::item " + item);

        recepieController = new RecepieController(EpoxyAsyncUtil.getAsyncBackgroundHandler(), EpoxyAsyncUtil.getAsyncBackgroundHandler());
        mp3MediaManager = MediaManager.getInstance(getApplicationContext());

        rvRecipeStep = findViewById(R.id.rv_content);
        tvCountDownValue = findViewById(R.id.tv_timer_value);

        btnTimerSetting = findViewById(R.id.btn_timer_setting);
        btnTimerStop = findViewById(R.id.btn_timer_stop);
        btnTimerReset = findViewById(R.id.btn_timer_reset);

        tvCountDownValue2 = findViewById(R.id.tv_timer_value2);
        btnTimerSetting2 = findViewById(R.id.btn_timer_setting2);
        btnTimerStop2 = findViewById(R.id.btn_timer_stop2);
        btnTimerReset2 = findViewById(R.id.btn_timer_reset2);

        tvCountDownValue3 = findViewById(R.id.tv_timer_value3);
        btnTimerSetting3 = findViewById(R.id.btn_timer_setting3);
        btnTimerStop3 = findViewById(R.id.btn_timer_stop3);
        btnTimerReset3 = findViewById(R.id.btn_timer_reset3);

        rvRecipeStep.setLayoutManager(new LinearLayoutManager(this));
        rvRecipeStep.setHasFixedSize(true);
        rvRecipeStep.setAdapter(recepieController.getAdapter());
        rvRecipeStep.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        if (item != null) {
            setToolbarTitle(item.getName());
            mTotalReference = FirebaseDatabase.getInstance().getReference().child("recipeDB").child("recipe_ID").child(item.getSeq());
            mReceipeReference = FirebaseDatabase.getInstance().getReference().child("recipeDB").child("recipe_ID").child(item.getSeq()).child("STEP");
            mIrdntListReference = FirebaseDatabase.getInstance().getReference().child("recipeDB").child("recipe_ID").child(item.getSeq()).child("IRDNT_LIST");
            requestSummary();
        }
        setListener();
    }

    private void requestSummary() {

        mTotalReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    //에러처리 해주세요. 토탈정보. 가 없을떄
                    L.e("::::getValue  null");
                    return;
                }
                String calorie = null;
                String cookingTime = null;

                SummaryData data = new SummaryData();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey() == null) return;

                    if (child.getKey().equalsIgnoreCase("CALORIE")) {
                        calorie = child.getValue(String.class);
                    }

                    if (child.getKey().equalsIgnoreCase("COOKING_TIME")) {
                        cookingTime = child.getValue(String.class);
                    }

                    if (calorie != null && cookingTime != null) {
                        data.calorie = calorie;
                        data.cookingTime = cookingTime;
                        data.imgUrl = item.getBImg();
                        break;
                    }

                }
                recepieController.updateSummaryItems(data);

                reqeustIngredient();
                reqeustReceipe();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void reqeustIngredient() {
        mIrdntListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    //에러처리 해주세요. 재료 가 없을떄
                    L.e("::::getValue  null");
                    return;
                }

                List<ingredientsData> list = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ingredientsData data = child.getValue(ingredientsData.class);
                    list.add(data);
                }
                recepieController.updateIngredientItems(list);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void reqeustReceipe() {
        mReceipeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    //에러처리 해주세요. 레시피 STEP가 없을떄
                    L.e("::::getValue  null");
                    return;
                }

                List<stepData> list = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    L.i("::::child " + child);
                    stepData data = child.getValue(stepData.class);
                    list.add(data);
                }

                recepieController.updateReceipeItems(list);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                L.e("::::Error " + error);
            }
        });
    }

    private View.OnClickListener timerSettingListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_timer_setting:
                    showTimerSettingDialog(TimerPreSet.TIMER_1);
                    break;
                case R.id.btn_timer_setting2:
                    showTimerSettingDialog(TimerPreSet.TIMER_2);
                    break;
                case R.id.btn_timer_setting3:
                    showTimerSettingDialog(TimerPreSet.TIMER_3);
                    break;
            }
        }
    };

    private View.OnClickListener timerResetListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_timer_reset:
                    TimerPreSet.sItemRowMap.put(TimerPreSet.TIMER_1, 0L);
                    cancelTimer(TimerPreSet.TIMER_1);
                    tvCountDownValue.setText("");
                    isTimerPuase = false;
                    btnTimerStop.setText("일시 정지");
                    break;
                case R.id.btn_timer_reset2:
                    TimerPreSet.sItemRowMap.put(TimerPreSet.TIMER_2, 0L);
                    cancelTimer(TimerPreSet.TIMER_2);
                    tvCountDownValue2.setText("");
                    isTimerPuase2 = false;
                    btnTimerStop2.setText("일시 정지");
                    break;
                case R.id.btn_timer_reset3:
                    TimerPreSet.sItemRowMap.put(TimerPreSet.TIMER_3, 0L);
                    cancelTimer(TimerPreSet.TIMER_3);
                    tvCountDownValue3.setText("");
                    isTimerPuase3 = false;
                    btnTimerStop3.setText("일시 정지");
                    break;
            }
        }
    };

    private View.OnClickListener timerPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_timer_stop:
                    if (TimerPreSet.byRemainTime(TimerPreSet.TIMER_1) == 0) {
                        Toast.makeText(getApplicationContext(), "시간 설정이 되어있어야합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isTimerPuase) {
                        btnTimerStop.setText("재시작");
                        isTimerPuase = true;
                        cancelTimer(TimerPreSet.TIMER_1);
                    } else {
                        btnTimerStop.setText("일시 정지");
                        isTimerPuase = false;
                        timerStart(TimerPreSet.byRemainTime(TimerPreSet.TIMER_1), TimerPreSet.TIMER_1);
                    }
                    break;
                case R.id.btn_timer_stop2:
                    if (TimerPreSet.byRemainTime(TimerPreSet.TIMER_2) == 0) {
                        Toast.makeText(getApplicationContext(), "시간 설정이 되어있어야합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!isTimerPuase2) {
                        btnTimerStop2.setText("재시작");
                        isTimerPuase2 = true;
                        cancelTimer(TimerPreSet.TIMER_2);
                    } else {
                        btnTimerStop2.setText("일시 정지");
                        isTimerPuase2 = false;
                        timerStart(TimerPreSet.byRemainTime(TimerPreSet.TIMER_2), TimerPreSet.TIMER_2);
                    }
                    break;
                case R.id.btn_timer_stop3:
                    if (TimerPreSet.byRemainTime(TimerPreSet.TIMER_3) == 0) {
                        Toast.makeText(getApplicationContext(), "시간 설정이 되어있어야합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isTimerPuase3) {
                        btnTimerStop3.setText("재시작");
                        isTimerPuase3 = true;
                        cancelTimer(TimerPreSet.TIMER_3);
                    } else {
                        btnTimerStop3.setText("일시 정지");
                        isTimerPuase3 = false;
                        timerStart(TimerPreSet.byRemainTime(TimerPreSet.TIMER_3), TimerPreSet.TIMER_3);
                    }
                    break;
            }
        }
    };

    private void showTimerSettingDialog(TimerPreSet timerPreSet) {
        timerDialog = new TimerDialogFragment();
        timerDialog.setCallback((hour, minute, second) -> {
            L.i(":::onTimeSet " + hour + " minute " + minute + " second " + second);
            int totalTime = (hour * 3600) + (minute * 60) + second;
            L.i("::::totalTime " + totalTime);
            cancelTimer(timerPreSet);
            timerStart(totalTime, timerPreSet);
        });
        timerDialog.show(getSupportFragmentManager(), "TAG_TIMER_DIALOG");
    }


    private void setListener() {
        btnTimerSetting.setOnClickListener(timerSettingListener);
        btnTimerStop.setOnClickListener(timerPauseListener);
        btnTimerReset.setOnClickListener(timerResetListener);
        btnTimerSetting2.setOnClickListener(timerSettingListener);
        btnTimerStop2.setOnClickListener(timerPauseListener);
        btnTimerReset2.setOnClickListener(timerResetListener);
        btnTimerSetting3.setOnClickListener(timerSettingListener);
        btnTimerStop3.setOnClickListener(timerPauseListener);
        btnTimerReset3.setOnClickListener(timerResetListener);

    }


    private void cancelTimer(TimerPreSet preSet) {
        mp3MediaManager.doStop();
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

    private void timerStart(long targetTime, TimerPreSet timerPreSet) {

        switch (timerPreSet) {
            case TIMER_1: {
                //                ramainTimerCount = targetTime;
                L.i("::::카운트다운 시간...");
                long totalTime = targetTime - 1;
                disposalCheckUpdateTimer = Flowable.intervalRange(0, targetTime, 0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(countTime -> {
//                            ramainTimerCount = totalTime - countTime;
                            TimerPreSet.sItemRowMap.put(timerPreSet, (totalTime - countTime));
                            tvCountDownValue.setText(RecipeActivity.this.convertTime(totalTime - countTime));
                        })
                        .doOnComplete(() -> {
//                            ramainTimerCount = 0;
                            TimerPreSet.sItemRowMap.put(timerPreSet, 0L);
                            L.i("::::::doOnComplete:::::::");
                            tvCountDownValue.setText("완료");
//                            onPlayMusic();
                            setVibrator();
                        }).subscribe();
                break;
            }
            case TIMER_2: {
                long totalTime = targetTime - 1;
                disposalCheckUpdateTimer2 = Flowable.intervalRange(0, targetTime, 0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(countTime -> {
                            TimerPreSet.sItemRowMap.put(timerPreSet, (totalTime - countTime));
                            tvCountDownValue2.setText(RecipeActivity.this.convertTime(totalTime - countTime));
                        })
                        .doOnComplete(() -> {
                            TimerPreSet.sItemRowMap.put(timerPreSet, 0L);
                            L.i("::::::doOnComplete:::::::");
                            tvCountDownValue2.setText("완료");
                            setVibrator();
                        }).subscribe();
                break;
            }

            case TIMER_3: {
                long totalTime = targetTime - 1;
                disposalCheckUpdateTimer3 = Flowable.intervalRange(0, targetTime, 0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(countTime -> {
                            TimerPreSet.sItemRowMap.put(timerPreSet, (totalTime - countTime));
                            tvCountDownValue3.setText(RecipeActivity.this.convertTime(totalTime - countTime));
                        })
                        .doOnComplete(() -> {
                            TimerPreSet.sItemRowMap.put(timerPreSet, 0L);
                            L.i("::::::doOnComplete:::::::");
                            tvCountDownValue3.setText("완료");
                            setVibrator();
                        }).subscribe();
                break;
            }


        }

    }

    private void setVibrator() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // 0.5초 대기 -> 1초 진동 -> 0.5초 대기 -> 1초 진동
        final long[] vibratePattern = new long[]{500, 1000, 500, 1000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, -1));
        } else {
            vibrator.vibrate(vibratePattern, -1);
        }

    }

    private void onPlayMusic() {
        try {

            // 아래 설정된 mp3. 플레이


            mp3MediaManager.doPlayWithAsset("timer_sound.mp3");
        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.to_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ListView selected_item = Parcels.unwrap(getIntent().getParcelableExtra("EXTRA_SELECTED_ITEM"));
        if (id == R.id.action_setComment) {
            Intent intent = new Intent(this, ShowPostActivity.class);
            intent.putExtra("selectedItem", selected_item.getName());
            intent.putExtra("selectedId", selected_item.getSeq());
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
