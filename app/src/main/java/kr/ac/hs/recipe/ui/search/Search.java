package kr.ac.hs.recipe.ui.search;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.activity.RecipeActivity;
import kr.ac.hs.recipe.recipeDB.ingredientsData;
import kr.ac.hs.recipe.recipeDB.recipeData;

public class Search extends Fragment {

    EditText searchText;
    LinearLayout classification, searchList, searchLoading;
    public ListView listView;
    CustomAdapter adapter;
    ToggleButton searchToggle;
    int[] lvBtn = {R.id.lv1, R.id.lv2, R.id.lv3}; // 난이도
    int[] nationBtn = {R.id.nation1, R.id.nation2, R.id.nation3, R.id.nation4, R.id.nation5, R.id.nation6, R.id.nation7}; // 유형분류
    int[] tyBtn =  {R.id.ty1, R.id.ty2, R.id.ty3, R.id.ty4, R.id.ty5, R.id.ty6, R.id.ty7, R.id.ty8, R.id.ty9, R.id.ty10, R.id.ty11, R.id.ty12, R.id.ty13, R.id.ty14, R.id.ty15, R.id.ty16, R.id.ty17}; // 음식분류
    Button[] lvBtnArr = new Button[3];
    Button[] nationBtnArr = new Button[7];
    Button[] tyBtnArr = new Button[17];
    String searchBtnText;
    int toggle = 0;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference recipeDBRef = myRef.child("recipeDB");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        LayoutInflater inf = getLayoutInflater();

        searchText = v.findViewById(R.id.searchText);
        classification = v.findViewById(R.id.classification_layout);
        searchList = v.findViewById(R.id.searchlist_layout);
        searchLoading = v.findViewById(R.id.searchLoading_layout);
        //backBtn.setColorFilter(Color.parseColor("#80688A"));
        listView = v.findViewById(R.id.searchlist);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        adapter = new CustomAdapter();

        /*textView = v.findViewById(R.id.searchRT);*/

        // 분류버튼 검색 기능
        // 난이도 버튼
        for (int i = 0; i < 3; i++) {
            final int INDEX;
            INDEX = i;
            lvBtnArr[INDEX] = v.findViewById(lvBtn[INDEX]);
            lvBtnArr[INDEX].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.clear(); // 검색 결과 목록 초기화
                    listView.setAdapter(adapter);
                    searchText.clearFocus();

                    classification.setVisibility(View.INVISIBLE);
                    searchLoading.setVisibility(View.VISIBLE);

                    searchBtnText = String.valueOf(lvBtnArr[INDEX].getText());
                    searchText.setText("난이도 분류 : " + searchBtnText);

                    recipeDBRef.child("recipe_ID").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                try {
                                    recipeData getResult = postSnapshot.getValue(recipeData.class);
                                    if (getResult.LEVEL_NM.equals(searchBtnText)) { // 검색 내용이 포함된 메뉴만 반환
                                        adapter.addItem(getResult.IMG_URL, getResult.RECIPE_NM_KO, getResult.SUMRY, String.valueOf(getResult.RECIPE_ID));
                                    }
                                } catch (Exception e) {
                                }
                            }
                            searchLoading.setVisibility(View.INVISIBLE);
                            searchList.setVisibility(View.VISIBLE);
                            listView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }
            });
        }

        // 유형분류 버튼
        for (int i = 0; i < 7; i++) {
            final int INDEX;
            INDEX = i;
            nationBtnArr[INDEX] = v.findViewById(nationBtn[i]);
            nationBtnArr[INDEX].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.clear(); // 검색 결과 목록 초기화
                    listView.setAdapter(adapter);
                    searchText.clearFocus();

                    classification.setVisibility(View.INVISIBLE);
                    searchLoading.setVisibility(View.VISIBLE);

                    searchBtnText = String.valueOf(nationBtnArr[INDEX].getText());
                    searchText.setText("유형분류 : " + searchBtnText);

                    recipeDBRef.child("recipe_ID").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                try {
                                    recipeData getResult = postSnapshot.getValue(recipeData.class);
                                    if (getResult.NATION_NAME.equals(searchBtnText)) { // 검색 내용이 포함된 메뉴만 반환
                                        adapter.addItem(getResult.IMG_URL, getResult.RECIPE_NM_KO, getResult.SUMRY, String.valueOf(getResult.RECIPE_ID));
                                    }
                                } catch (Exception e) {
                                }
                            }
                            searchLoading.setVisibility(View.INVISIBLE);
                            searchList.setVisibility(View.VISIBLE);
                            listView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                }
            });
        }

        // 음식분류 버튼
        for (int i = 0; i < 17; i++) {
            final int INDEX;
            INDEX = i;
            tyBtnArr[INDEX] = v.findViewById(tyBtn[i]);
            tyBtnArr[INDEX].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.clear(); // 검색 결과 목록 초기화
                    listView.setAdapter(adapter);
                    searchText.clearFocus();

                    classification.setVisibility(View.INVISIBLE);
                    searchLoading.setVisibility(View.VISIBLE);

                    searchBtnText = String.valueOf(tyBtnArr[INDEX].getText());
                    searchText.setText("음식분류 : " + searchBtnText);

                    recipeDBRef.child("recipe_ID").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                try {
                                    recipeData getResult = postSnapshot.getValue(recipeData.class);
                                    if (getResult.TY_NM.equals(searchBtnText)) { // 검색 내용이 포함된 메뉴만 반환
                                        adapter.addItem(getResult.IMG_URL, getResult.RECIPE_NM_KO, getResult.SUMRY, String.valueOf(getResult.RECIPE_ID));
                                    }
                                } catch (Exception e) {
                                }
                            }
                            searchLoading.setVisibility(View.INVISIBLE);
                            searchList.setVisibility(View.VISIBLE);
                            listView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                }
            });
        }


        // 검색창 검색 기능
        // 검색 토글 (메뉴 <> 재료)
        searchToggle = v.findViewById(R.id.searchToggle);

        searchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchToggle.isChecked()){
                    toggle = 0; //메뉴명으로 검색하기
                } else {
                    toggle = 1; //재료명으로 검색하기
                }
            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    listView.setAdapter(adapter);
                    searchText.clearFocus();

                    classification.setVisibility(View.INVISIBLE);
                    searchLoading.setVisibility(View.VISIBLE);

                    if (toggle == 0) { //메뉴명으로 검색하기
                        adapter.clear(); // 검색 결과 목록 초기화
                        recipeDBRef.child("recipe_ID").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    try {
                                        recipeData getResult = postSnapshot.getValue(recipeData.class);
                                        if (getResult.RECIPE_NM_KO.contains(String.valueOf(searchText.getText()))) { // 검색 내용이 포함된 메뉴만 반환
                                            adapter.addItem(getResult.IMG_URL, getResult.RECIPE_NM_KO, getResult.SUMRY, String.valueOf(getResult.RECIPE_ID));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                                searchLoading.setVisibility(View.INVISIBLE);
                                searchList.setVisibility(View.VISIBLE);
                                listView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                    } else { //재료명으로 검색하기
                        adapter.clear(); // 검색 결과 목록 초기화
                        recipeDBRef.child("recipe_ID").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    try {
                                        recipeData getResult = postSnapshot.getValue(recipeData.class);
                                        for (DataSnapshot postSnapshot2 : postSnapshot.child("IRDNT_LIST").getChildren()) { // 재료 검색
                                            try {
                                                ingredientsData getIRDNT = postSnapshot2.getValue(ingredientsData.class);
                                                // equals가 나을지 contains가 나을지?????
                                                if (getIRDNT.IRDNT_NM.equals(String.valueOf(searchText.getText())) && getResult.RECIPE_ID == getIRDNT.RECIPE_ID) { // 검색 내용(재료)이 포함된 메뉴만 반환
                                                    adapter.addItem(getResult.IMG_URL, getResult.RECIPE_NM_KO, getResult.SUMRY, String.valueOf(getResult.RECIPE_ID));
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                                searchLoading.setVisibility(View.INVISIBLE);
                                searchList.setVisibility(View.VISIBLE);
                                listView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                    }
                }

                // 키보드 자동 숨기기
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

                return true;
            }

        });

        // 목록 눌렀을 때 > 레시피 세부 페이지
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // textView.setText();
                //Toast.makeText(getActivity(), adapter.itemList.get(position).getName() + " 선택! ", Toast.LENGTH_SHORT).show();

                kr.ac.hs.recipe.ui.search.ListView item = (kr.ac.hs.recipe.ui.search.ListView) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                intent.putExtra("EXTRA_SELECTED_ITEM",Parcels.wrap(item));
                startActivity(intent);
            }
        });

        // 뒤로가기 버튼 제어
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (searchList.getVisibility() == View.VISIBLE) {
                    // Handle back press
                    classification.setVisibility(View.VISIBLE);
                    searchList.setVisibility(View.INVISIBLE);
                    searchText.getText().clear();
                    searchText.clearFocus();
                } else {
                    // If you want to get default implementation of onBackPressed, use this
                    this.remove();
                    requireActivity().onBackPressed();
                }
            }
        });

        return v;

    }
}