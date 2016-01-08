package lieying.com.avplayer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import lieying.com.avplayer.view.PreviewView;

public class VideoRecorderActivity extends Activity {
    private final String VIDEO_PATH_NAME = "/mnt/sdcard/VGA_30fps_512vbrate.mp4";

    private PreviewView mPreviewView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_video_recorder);

        // we shall take the video in landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mPreviewView = (PreviewView) findViewById(R.id.take_picture_surfaceview);
        mPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreviewView.switchCamera();
            }
        });
        mPreviewView.setActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecorder();
    }

    private void initRecorder() {
    }

}