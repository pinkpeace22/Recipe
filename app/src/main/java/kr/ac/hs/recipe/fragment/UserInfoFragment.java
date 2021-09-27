package kr.ac.hs.recipe.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.activity.LoginActivity;
import kr.ac.hs.recipe.activity.MainActivity;
import kr.ac.hs.recipe.activity.MyPostActivity;
import kr.ac.hs.recipe.activity.SignUpActivity;

import static kr.ac.hs.recipe.Util.showToast;

public class UserInfoFragment extends Fragment {
    private static final String TAG = "UserInfoFragment";
    Button logout, delete, myComment;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        final ImageView profileImageView = view.findViewById(R.id.profileImageView);
        final TextView emailTextView = view.findViewById(R.id.emailTextView);
        final TextView nameTextView = view.findViewById(R.id.nameTextView);
        logout = view.findViewById(R.id.logout);
        delete = view.findViewById(R.id.delete);
        myComment = view.findViewById(R.id.myComment);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user = FirebaseAuth.getInstance().getCurrentUser();
        emailTextView.setText(user.getEmail());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            if (document.getData().get("photoUrl") != null) {
                                Glide.with(getActivity()).load(document.getData().get("photoUrl")).centerCrop().override(500).into(profileImageView);
                            } else
                                profileImageView.setImageResource(R.drawable.ic_baseline_person_24);
                            nameTextView.setText(document.getData().get("name").toString());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        myComment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                ((MainActivity) MainActivity.mContext).myStartActivity(MyPostActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                ((MainActivity) MainActivity.mContext).myStartActivity(MainActivity.class);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.delete();
                Log.d(TAG, "사용자의 계정이 삭제 되었습니다.");
            }
        });

        //setHasOptionsMenu(true);
        return view;
    }

/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
       //super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.to_mycomment, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.action_myComment:
                ((MainActivity) MainActivity.mContext).myStartActivity(MyPostActivity.class);
            break;
        }
        return  super.onOptionsItemSelected(item);
    }
*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
