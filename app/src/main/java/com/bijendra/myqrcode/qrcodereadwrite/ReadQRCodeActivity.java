package com.bijendra.myqrcode.qrcodereadwrite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;

public class ReadQRCodeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView mQrCodeImageview;
    TextView mTvQrCodeData;
    SurfaceView     mSvCameraView;
    BarcodeDetector barcodeDetector ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_qr_code);
        mQrCodeImageview=(ImageView) findViewById(R.id.img_qr_code_image);
        mTvQrCodeData=(TextView) findViewById(R.id.tv_qr_code_data);
        mSvCameraView=(SurfaceView) findViewById(R.id.sv_camera_view);

    }
    public void gotoPickQrImage(View view)
    {
        mSvCameraView.setVisibility(View.GONE);
        mQrCodeImageview.setVisibility(View.GONE);
        mTvQrCodeData.setText(null);

        if(view.getTag().toString().equalsIgnoreCase("0"))//pick saved image
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
           startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

        }
         else  if(view.getTag().toString().equalsIgnoreCase("1"))//Read QR code from camera
        {
            readFromCamera();
        }
    }

    private void readFromCamera()
    {
        mSvCameraView.setVisibility(View.VISIBLE);
        barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        final CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
        mSvCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(mSvCameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    mTvQrCodeData.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            mTvQrCodeData.setText(    // Update the TextView
                                    barcodes.valueAt(0).displayValue
                            );
                        }
                    });
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                mQrCodeImageview.setImageBitmap(bitmap);
                mQrCodeImageview.setVisibility(View.VISIBLE);
                readQrCode(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void readQrCode(Bitmap bitmap)
    {
        //Create a Barcode Detector
         barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();
        //Read the QR Code
        Frame myFrame = new Frame.Builder()
                .setBitmap(bitmap)
                .build();

        SparseArray<Barcode> barcodes = barcodeDetector.detect(myFrame);
        // Check if at least one barcode was detected
        if(barcodes.size() != 0) {
            // Print the QR code's message
            String value=barcodes.valueAt(0).displayValue;
            mTvQrCodeData.setText(value);
        }

    }
}
