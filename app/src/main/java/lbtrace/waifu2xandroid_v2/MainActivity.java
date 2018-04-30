package lbtrace.waifu2xandroid_v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PICK_IMAGE_CODE = 1;
    private ImageView mImageView;
    private Button mPickBtn;
    private Button mProcessBtn;
    private Bitmap oriBitmap;
    private ProgressBar mScaleProgressBar;

    private boolean mIsScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.scale_image);
        mPickBtn = (Button) findViewById(R.id.pick_btn);
        mProcessBtn = (Button) findViewById(R.id.process_btn);
        mScaleProgressBar = (ProgressBar) findViewById(R.id.scale_process_pb);
        mScaleProgressBar.setVisibility(View.GONE);

        mPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_CODE);
            }
        });

        mProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "ori Bitmap size: (" + oriBitmap.getHeight() + "," + oriBitmap.getWidth() + ")");
                new ImageScaleTask(getAssets(), new ViewExecuteCallback()).execute(oriBitmap);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        oriBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        mImageView.setImageBitmap(oriBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    class ViewExecuteCallback implements ImageScaleTask.ExecuteCallback {
        @Override
        public void onPreExecute() {
            mPickBtn.setClickable(false);
            mImageView.setVisibility(View.GONE);
            mScaleProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
            mImageView.setVisibility(View.VISIBLE);
            oriBitmap = bitmap;
            mPickBtn.setClickable(true);
            mScaleProgressBar.setVisibility(View.GONE);
        }
    }
}
