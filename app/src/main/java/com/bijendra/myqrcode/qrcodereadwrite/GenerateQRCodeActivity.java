package com.bijendra.myqrcode.qrcodereadwrite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateQRCodeActivity extends AppCompatActivity {

    private static final String TAG ="GenerateQRCodeActivity" ;
    ImageView mQrCodeImageview;
    String QRcode;
    public final static int WIDTH=500;
    private EditText mQrCodeText;
   // Bitmap bitmap ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr_code);
        mQrCodeImageview=(ImageView) findViewById(R.id.img_qr_code_image);
        mQrCodeText=(EditText) findViewById(R.id.et_qr_code_text);
    }
    public void gotoQrCodeOperation(View view)
    {

        if(!TextUtils.isEmpty(mQrCodeText.getText().toString()))
          qRCodeGenerator();
    }

    private void qRCodeGenerator()
    {
        String qrCodeData=mQrCodeText.getText().toString();
        mQrCodeText.setText(null);
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        QRGEncoder qrgEncoder = new QRGEncoder(qrCodeData, null, QRGContents.Type.TEXT, WIDTH);
        try {

             Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            mQrCodeImageview.setImageBitmap(bitmap);
            saveQRCodeImage(bitmap);
          } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
    }
    private void saveQRCodeImage(Bitmap bitmap )
    {
        OutputStream stream = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File file = new File(path, timeStamp+".jpg");
        try{
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }
        finally {
            try {
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(file!=null) {
            Uri uri = Uri.parse("file://" + file.getAbsolutePath());
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType("image*//*");
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "Share image File"));
        }
    }

}
