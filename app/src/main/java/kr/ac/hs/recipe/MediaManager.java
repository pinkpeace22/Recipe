package kr.ac.hs.recipe;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MediaManager {

    private static MediaPlayer mPlayer = null;
    private static MediaManager sInstance;
    private Context mContext;

    private MediaPlayer.OnCompletionListener mMediaCompletionListener = mp -> {
        //한곡이 끝나고 다음곡에 대해서 처리해주세요.
    };
    private String path;

    public MediaManager(Context context) {
        mContext = context;
    }

    public static MediaManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MediaManager(context);
        }
        return sInstance;
    }


    //mp3 재생 정지
    public void doStop() {

        L.e("mPlayer = " + mPlayer);
        try {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
                mPlayer.release();
                mPlayer = null;
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        } finally {
            try {
                if (mPlayer != null) {
                    mPlayer.release();
                    mPlayer = null;
                }
            } catch (Exception e5) {
                e5.printStackTrace();
            }
        }
    }

    //앱의 asset폴더의 파일 플레이시 사용
    public void doPlayWithAsset(String path) throws IOException {

        this.path = path;

        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
        }

        L.e("path = " + path);
        //////////////////////// 내부 asset 폴더 음원 재생시

        AssetFileDescriptor afd = mContext.getAssets().openFd(path);
        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        mPlayer.setOnCompletionListener(mMediaCompletionListener);
        if (mPlayer != null) {
            mPlayer.prepare();
            mPlayer.start();
        } else {
            L.e("MediaPlayer object creation failed....");
        }

    }

    //mp3의 전체 시간 가져오기
    public int getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return -1;
    }

    //현재 플레이 중인 음원의 포지션(시간) 가져오기
    public int getCurrentPosition() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                return mPlayer.getCurrentPosition();
            } else {
                return 0;
            }
        } else {

            return 0;
        }
    }

    public boolean doPlayWithSdcard(String path) throws IOException {

        this.path = path;


        String sdcardStat = Environment.getExternalStorageState();
        L.e("path = " + path);
        if (!sdcardStat.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mContext, "단말의 sdcard 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        File file = new File(path);
        L.e("file.exists() = " + file.exists());
        L.e("::::::path : " + file.getAbsolutePath());
        if (!file.exists()) {
            Toast.makeText(mContext, "해당 경로에 파일이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
        }

        //미디어 플레이를 위한 객체 생성 및 path설정
        L.e("path = " + path);
        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(path);
        //mp3 플레이 종료시 호출되는 callback 리스너 등록
        mPlayer.setOnCompletionListener(mMediaCompletionListener);
        if (mPlayer != null) {
            mPlayer.prepare();
            mPlayer.start();
        } else {
            L.e("MediaPlayer object creation failed....");
        }
        return true;
    }


    public void setSeekTo(int position) {
        if (mPlayer != null) {
            mPlayer.seekTo(position);
        }
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }
}
