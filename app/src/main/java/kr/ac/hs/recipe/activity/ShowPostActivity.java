package kr.ac.hs.recipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import kr.ac.hs.recipe.PostInfo;
import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.adapter.HomeAdapter;
import kr.ac.hs.recipe.listener.OnPostListener;

public class ShowPostActivity extends BasicActivity {

    private static final String TAG = "ShowPostActivity";
    private FirebaseFirestore firebaseFirestore;
    private HomeAdapter homeAdapter;
    private ArrayList<PostInfo> postList;
    private boolean updating;
    private boolean topScrolled;
    FirebaseUser user;
    public static boolean showBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        Intent intent = getIntent();
        String selected_item = intent.getStringExtra("selectedItem");

        setToolbarTitle("【리뷰】 " + selected_item);

        firebaseFirestore = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();
        homeAdapter = new HomeAdapter(this, postList);
        homeAdapter.setOnPostListener(onPostListener);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(homeAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if (newState == 1 && firstVisibleItemPosition == 0) {
                    topScrolled = true;
                }

                if (newState == 0 && topScrolled) {
                    postsUpdate(true);
                    topScrolled = false;
                }

                if(firstVisibleItemPosition < 0) {
                    postsUpdate(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalItemCount - 3 <= lastVisibleItemPosition && !updating){
                    postsUpdate(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });
        postsUpdate(false);
    }

    @Override
    public void onPause(){
        super.onPause();
        homeAdapter.playerStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.floatingActionButton) {
                Intent intent = new Intent(v.getContext(), WritePostActivity.class);
                intent.putExtra("selectedId", getIntent().getStringExtra("selectedId"));
                startActivity(intent);
            }
        }
    };

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            postList.remove(postInfo);
            homeAdapter.notifyDataSetChanged();

            Log.e("로그: ","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그: ","수정 성공");
        }
    };

    private void postsUpdate(final boolean clear) {
        showBtn = true;
        updating = true;
        Date date = postList.size() == 0 || clear ? new Date() : postList.get(postList.size() - 1).getCreatedAt();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String recipeId = getIntent().getStringExtra("selectedId");
        CollectionReference collectionReference = firebaseFirestore.collection("posts");
        collectionReference.whereEqualTo("recipeId", recipeId).orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                postList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String username = document.getData().get("publisher").toString();
                                showBtn = username.equals(user.getUid());
                                String profileImg;
                                if(document.getData().get("profileImg") == null) {
                                    profileImg = null;
                                } else profileImg = document.getData().get("profileImg").toString();

                                postList.add(new PostInfo(profileImg,
                                        document.getData().get("profileName").toString(),
                                        (ArrayList<String>) document.getData().get("contents"),
                                        (ArrayList<String>) document.getData().get("formats"),
                                        document.getData().get("publisher").toString(),
                                        new Date(document.getDate("createdAt").getTime()),
                                        document.getId(), document.getData().get("recipeId").toString(), showBtn));
                            }
                            homeAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }


}
