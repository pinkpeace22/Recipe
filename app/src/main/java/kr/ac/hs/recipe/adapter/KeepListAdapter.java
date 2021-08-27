package kr.ac.hs.recipe.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.*;

import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.activity.MainActivity;
import kr.ac.hs.recipe.ui.search.CustomAdapter;
import kr.ac.hs.recipe.ui.search.ListView;

public class KeepListAdapter extends BaseAdapter {
    public ArrayList<ListView> keepedItem = new ArrayList<ListView>() ;

    public KeepListAdapter() {
    }

    @Override
    public int getCount() {
        return keepedItem.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        final int checkBoxPosition = position;
        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.keeplist_layout, parent, false);

            holder = new ViewHolder();
            holder.keepImageView = v.findViewById(R.id.keeplist_img);
            holder.keepNameView = v.findViewById(R.id.keeplist_name) ;
            holder.keepAboutView = v.findViewById(R.id.keeplist_about) ;
            holder.keeplistView = v.findViewById(R.id.keeplist_btn);

            v.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListView listItem = keepedItem.get(position);

        //Bitmap img = urlToBitmap(listItem.getBImg());
        //holder.imageView.setImageBitmap(img);
        holder.keepImageView.setImageBitmap(listItem.getBImg());
        holder.keepNameView.setText(listItem.getName());
        holder.keepAboutView.setText(listItem.getAbout());

        holder.keeplistView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                ListView checkViewItem = (ListView) getItem(checkBoxPosition);

                if (checkViewItem.isChecked) {
                    keepedItem.get(checkBoxPosition).isChecked = false;

                    CustomAdapter.keepList.remove(String.valueOf(keepedItem.get(checkBoxPosition).getSeq()));
                    while (CustomAdapter.keeping.remove(String.valueOf(keepedItem.get(checkBoxPosition).getSeq()))) {
                    }
                } else {
                    keepedItem.get(checkBoxPosition).isChecked = true;
                    //Toast.makeText(v.getContext(), itemList.get(checkBoxPosition).getSeq() + " 즐겨찾기! ", Toast.LENGTH_SHORT).show();

                    CustomAdapter.keeping.add(keepedItem.get(checkBoxPosition).getSeq());
                    for (String item : CustomAdapter.keeping) {
                        if (!CustomAdapter.keepList.contains(item))
                            CustomAdapter.keepList.add(item);
                    }
                }
                ((MainActivity) MainActivity.mContext).saveKeepListToFile();
                notifyDataSetChanged();
            }
        });

        if (CustomAdapter.keepList.contains(keepedItem.get(position).getSeq())) {
            holder.keeplistView.setChecked(true);
            keepedItem.get(checkBoxPosition).isChecked = true;
        } else {
            holder.keeplistView.setChecked(false);
            keepedItem.get(checkBoxPosition).isChecked = false;
        }

        return v;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return keepedItem.get(position) ;
    }

    public void addItem(String url, String name, String about,String seq) {
        ListView item = new ListView();

        Bitmap img = urlToBitmap(url); // url > bitmap
        //item.setBImg(url);
        item.setBImg(img);
        item.setName(name);
        item.setAbout(about);
        item.setSeq(seq);

        keepedItem.add(item);
    }

    //URL을 받아 Bitmap 파일로 전환
    public Bitmap urlToBitmap(final String Url){
        final Bitmap[] bitmap = new Bitmap[1];
        Thread mThread = new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL(Url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

        try{
            //멀티쓰레드 작업 중 서브스레드의 결과를 메인스레드에 적용시켜야 할 필요가 있는 경우
            //메인스레드의 필요한 작업이 종요할 때 까지 대기하도록 하는 메서드
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap[0];
    }

    public void clear() {
        keepedItem.clear();
    }

    public class ViewHolder
    {
        public ImageView keepImageView;
        public TextView keepNameView, keepAboutView;
        public CheckBox keeplistView;
    }
}
