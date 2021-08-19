/*
package kr.ac.hs.recipe.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import kr.ac.hs.recipe.R;


public class Result extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_test);

        TextView textView = findViewById(R.id.searchRT);
        ImageView imageView = findViewById(R.id.searchIMG);

        Intent intent = getIntent();
        imageView.setImageBitmap(intent.getParcelableExtra("mainImg"));
        textView.append(intent.getStringExtra("name"));
        textView.append("\n\n");
        textView.append(intent.getStringExtra("about"));
    }
}
*/
