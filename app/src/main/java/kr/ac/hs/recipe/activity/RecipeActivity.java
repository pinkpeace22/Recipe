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
import kr.ac.hs.recipe.dialog.TimerBottomDialog;
import kr.ac.hs.recipe.fragment.TimerDialogFragment;
import kr.ac.hs.recipe.recipeDB.SummaryData;
import kr.ac.hs.recipe.recipeDB.ingredientsData;
import kr.ac.hs.recipe.recipeDB.stepData;
import kr.ac.hs.recipe.ui.search.ListView;

public class RecipeActivity extends BasicActivity implements TimerBottomDialog.TimerComplete {
    private DatabaseReference mTotalReference = null; //전체 항목.
    private DatabaseReference mReceipeReference = null; //레시피 항목
    private DatabaseReference mIrdntListReference = null; //재료 항목.
    private EpoxyRecyclerView rvRecipeStep;
    private TimerBottomDialog timerBottomDialog;


    //    private RecipeAdapter adapter;
    private RecepieController recepieController;
    private TimerDialogFragment timerDialog;
    private MediaManager mp3MediaManager;
    private ListView item;


    @Override
    public void onComplete() {
        onPlayMusic();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        item = Parcels.unwrap(getIntent().getParcelableExtra("EXTRA_SELECTED_ITEM"));
        setToolbarTitle(item == null ? "" : item.getName());


        L.i(":::::::::::::::::::item " + item);

        recepieController = new RecepieController(EpoxyAsyncUtil.getAsyncBackgroundHandler(), EpoxyAsyncUtil.getAsyncBackgroundHandler());
        mp3MediaManager = MediaManager.getInstance(getApplicationContext());

        rvRecipeStep = findViewById(R.id.rv_content);


        rvRecipeStep.setLayoutManager(new LinearLayoutManager(this));
        rvRecipeStep.setHasFixedSize(true);
        rvRecipeStep.setAdapter(recepieController.getAdapter());
        rvRecipeStep.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        timerBottomDialog = TimerBottomDialog.newInstance();
        timerBottomDialog.setListener(this);

        if (item != null) {
            setToolbarTitle(item.getName());
            mTotalReference = FirebaseDatabase.getInstance().getReference().child("recipeDB").child("recipe_ID").child(item.getSeq());
            mReceipeReference = FirebaseDatabase.getInstance().getReference().child("recipeDB").child("recipe_ID").child(item.getSeq()).child("STEP");
            mIrdntListReference = FirebaseDatabase.getInstance().getReference().child("recipeDB").child("recipe_ID").child(item.getSeq()).child("IRDNT_LIST");
            requestSummary();
        }

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

        if (id == R.id.action_timer) {
            timerBottomDialog.show(getSupportFragmentManager(), TimerBottomDialog.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mp3MediaManager.doStop();
        if (timerBottomDialog != null) timerBottomDialog.allTimerClear();
        super.onDestroy();
    }
}
