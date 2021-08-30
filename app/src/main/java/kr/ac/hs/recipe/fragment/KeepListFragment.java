package kr.ac.hs.recipe.fragment;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.activity.RecipeActivity;
import kr.ac.hs.recipe.adapter.KeepListAdapter;
import kr.ac.hs.recipe.recipeDB.recipeData;
import kr.ac.hs.recipe.ui.search.CustomAdapter;

public class KeepListFragment extends Fragment {

    ListView keeplistView;
    KeepListAdapter keepadapter;
    LinearLayout keepLoading, keepLayout;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference recipeDBRef = myRef.child("recipeDB");

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_keep_list, container, false);
        LayoutInflater inf = getLayoutInflater();

        keepLoading = v.findViewById(R.id.keepLoading_layout);
        keepLayout = v.findViewById(R.id.keeplist_layout);
        keepadapter = new KeepListAdapter();
        keeplistView = v.findViewById(R.id.keeplist);
        keeplistView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        recipeDBRef.child("recipe_ID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        recipeData getResult = postSnapshot.getValue(recipeData.class);
                        for (String item : CustomAdapter.keepList) {
                            if (String.valueOf(getResult.RECIPE_ID).equals(item)) { // 검색 내용이 포함된 메뉴만 반환
                                keepadapter.addItem(getResult.IMG_URL, getResult.RECIPE_NM_KO, getResult.SUMRY, String.valueOf(getResult.RECIPE_ID));

                            }
                        }
                    } catch (Exception e) {
                    }
                }
                keepLoading.setVisibility(View.INVISIBLE);
                keepLayout.setVisibility(View.VISIBLE);

                keeplistView.setAdapter(keepadapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        // 목록 눌렀을 때 > 레시피 세부 페이지
        keeplistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // textView.setText();
                //Toast.makeText(getActivity(), adapter.itemList.get(position).getName() + " 선택! ", Toast.LENGTH_SHORT).show();

                kr.ac.hs.recipe.ui.search.ListView item = (kr.ac.hs.recipe.ui.search.ListView) keepadapter.getItem(position);
                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                intent.putExtra("EXTRA_SELECTED_ITEM", Parcels.wrap(item));
                startActivity(intent);
            }
        });

        return v;
    }
}
