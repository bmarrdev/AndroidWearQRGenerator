package com.hookedonplay.androidbycode.androidwearqrgenerator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.WriterException;

/**
 * Created by HookedOnPlay on 20150206
 * <p/>
 * Generate and display QR codes correctly on round and square Android Wear devices
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private String mEncodeString;
    private TextView mTextDesc;
    private ImageView mImageQR;
    private ProgressBar mProgress;
    private Bitmap mBitmapQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextDesc = (TextView) stub.findViewById(R.id.textDesc);
                mImageQR = (ImageView) stub.findViewById(R.id.imageQR);
                mProgress = (ProgressBar) stub.findViewById(R.id.progressGenerate);
            }
        });
    }

    public void onClickGenerateRound(View view) {
        mEncodeString = getString(R.string.string_to_encode);
        mTextDesc.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        new AsyncGenerateQRCode().execute(GenerateQR.MARGIN_NONE);
    }

    public void onClickGenerateSquare(View view) {
        mEncodeString = getString(R.string.string_to_encode);
        mTextDesc.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        new AsyncGenerateQRCode().execute(GenerateQR.MARGIN_AUTOMATIC);
    }

    /**
     * AsyncTask to generate QR Code image
     */
    private class AsyncGenerateQRCode extends AsyncTask<Integer, Void, Integer> {

        /**
         * Background thread function to generate image
         *
         * @param params margin to use in creating QR Code
         * @return non zero for success
         *
         * Note that is margin is not in pixels.  See the zxing api for details about the margin
         * for QR code generation
         */
        @Override
        protected Integer doInBackground(Integer... params) {
            if (params.length != 1) {
                throw new IllegalArgumentException("Must pass QR Code margin value as argument");
            }

            try {
                final int colorQR = Color.BLACK;
                final int colorBackQR = Color.WHITE;
                final int marginSize = params[0];
                final int width = 400;
                final int height = 400;

                mBitmapQR = GenerateQR.generateBitmap(mEncodeString, width, height,
                        marginSize, colorQR, colorBackQR);
            }
            catch (IllegalArgumentException iae) {
                Log.e(TAG, "Invalid arguments for encoding QR");
                iae.printStackTrace();
                return 0;
            }
            catch (WriterException we) {
                Log.e(TAG, "QR Writer unable to generate code");
                we.printStackTrace();
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            mProgress.setVisibility(View.GONE);
            if (result != 0) {
                mImageQR.setImageBitmap(mBitmapQR);
                mImageQR.setVisibility(View.VISIBLE);
            }else {
                mTextDesc.setText(getString(R.string.encode_error));
                mTextDesc.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}