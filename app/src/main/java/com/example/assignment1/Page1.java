package com.example.assignment1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.assignment1.R;

import java.io.IOException;
import java.util.ArrayList;

public class Page1 extends AppCompatActivity  implements SensorEventListener {

    private int GLOBAL_ENTRY=0;
    private float heart_bpm=0;
    private float respRate=0;

    private final int REQUEST_VIDEO_CAPTURE = 1;
    private CameraManager cameraManager;
    private String getCameraID;
    Uri videoUri=null;
    private static String[] symptoms_items = new String[]{"Nausea", "Headache", "SoreThroat","Diarrhea","Fever","MuscleAche","LossOfSmellOrTaste","Cough","ShortnessOfBreath","FeelingTired"};
    private float[] symptom_values=new float[symptoms_items.length];
    private SensorManager sensorManager;
    private Sensor sensor;
    private ArrayList<Float> x_arr,y_arr,z_arr;

    Handler handler = new Handler();
    public float[] convertArray(ArrayList<Float> a){
        float [] res= new float[a.size()];
        for (int i = 0; i < a.size(); i++)
            res[i]=a.get(i);
        return res;
    }


    // DB variables
    DBhelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1);
        db=new DBhelper(this);

        // Pressing symptoms -> next page
        // Symptoms button -----------------------------------------------------------------
        Button b1 = (Button) findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int1 = new Intent(getApplicationContext(), Page2.class);
                startActivity(int1);
            }
        });

        // Heart-rate button -----------------------------------------------------------------------
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            getCameraID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        Button b3 = (Button) findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ViewView Code
                Toast.makeText(view.getContext(), "Heart rate measure, put finger on camera with Flash on", Toast.LENGTH_LONG).show();
                Intent takeFingerPrintVideo = new Intent("android.media.action.VIDEO_CAPTURE");
                takeFingerPrintVideo.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                startActivityForResult(takeFingerPrintVideo, REQUEST_VIDEO_CAPTURE);
                Log.i("VIDEO_CAPTURE", "Started with video capture code");
                //Video view code end

            }
        });

        // Respiratory rate code -------------------------------------------------------------------
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        x_arr=new ArrayList<>();
        y_arr=new ArrayList<>();
        z_arr=new ArrayList<>();
        TextView tv3=(TextView) findViewById(R.id.textView3);
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                sensorManager.unregisterListener(Page1.this);
                float[] xf=convertArray(x_arr);
                float[] yf=convertArray(y_arr);
                float[] zf=convertArray(z_arr);
                ArrayList<Float> ma_arrlist=new ArrayList<>();
                MovingAverage MA=new MovingAverage(5);
                Log.i("PEAK","Len of z_arr "+z_arr.size());
                for(int i=0;i<x_arr.size();i++){
                    float scale =(float) Math.pow(10,4);
                    MA.pushValue(zf[i]);
                    float temp_ma=MA.getValue();
                    Log.i("MA","avg : "+temp_ma);
                    ma_arrlist.add(Math.round(temp_ma*scale)/scale);

                }
                int cross_count=0;
                float total_avg=(float)0.0;
                for(int i=0;i<ma_arrlist.size();i++){
                    total_avg=total_avg+ma_arrlist.get(i);
                }
                total_avg=total_avg/ma_arrlist.size();
                for(int i=0;i<ma_arrlist.size();i++){
                    if (ma_arrlist.get(i)>total_avg)
                        cross_count=cross_count+1;
                }
                float peaks=PeakDetection.peaksTroughs(convertArray(ma_arrlist),ma_arrlist.size()).size();
                Log.i("PEAKS"," "+peaks+" cross count :"+cross_count);
                respRate=peaks*(60/45);
                tv3.setText("Respiratory rate per minute "+respRate );
            }
        };
          // respiratory rate button
        Button b4 = (Button) findViewById(R.id.button4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorManager.registerListener(Page1.this, sensor, 200000);
                Toast.makeText(Page1.this,"Please keep phone on chest",Toast.LENGTH_LONG);
                tv3.setText("Respiratory rate in process  ");
                handler.postDelayed(runnableCode, 46000);
            }
        });


        // DB push Upload Signs button  and Also data base view button
        Button b2=(Button) findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                symptom_values = intent.getFloatArrayExtra("symp_list");
                for(int i =0;i<symptom_values.length;i++)
                    Log.i("DATALOG","values of symptom_values : "+symptom_values[i]);
                Boolean status=db.insertuserdata(heart_bpm,respRate,symptom_values[0],symptom_values[1],symptom_values[2],symptom_values[3],symptom_values[4],symptom_values[5],symptom_values[6],symptom_values[7],symptom_values[8],symptom_values[9]);
                Log.i("DATALOG","status of push is : "+status);
                Toast.makeText(Page1.this,"Entire symtoms being pushed to DB",Toast.LENGTH_LONG);
            }
        });


    }



    // HEART RATE For storing the video and its relevant function ---------------------
    private String getRealPathFromURI(Uri contentURI) {
        String filePath;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            filePath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(idx);
            cursor.close();
        }
        return filePath;
    }
    Handler handler2=new Handler();
    Runnable runnableCode2=new Runnable() {
        @Override
        public void run() {
            TextView tv=(TextView) findViewById(R.id.textView2);
            tv.setText("In progress, do not close app.");
            ArrayList<Float> red_arr=new ArrayList<>();
            MovingAverage redMA= new MovingAverage(3);
            ArrayList<Float> red_ma_vals=new ArrayList<>();
            try {
                ArrayList<Bitmap> barr=getFrames(videoUri);
                for(int i=0;i<barr.size();i++){
                    Bitmap bmp=barr.get(i);
                    int red=getRedData(bmp);
                    Log.i("VIDEO_CAPTURE","Red intensity :"+red);
                    // code from here onward
                    red_arr.add((float)red );
                    redMA.pushValue((float) red);
                    red_ma_vals.add(redMA.getValue());
                }
                int peaks_red=PeakDetection.peaksTroughs(convertArray(red_arr),red_arr.size()).size()+1;
                heart_bpm=peaks_red*60/44;
                Log.i("VIDEO_CAPTURE","Peaks :  "+peaks_red);
                Log.i("VIDEO_CAPTURE","Beats per minute "+heart_bpm);
                tv.setText("Heart Rate: "+heart_bpm+" bpm");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("NewApi")
    private ArrayList<Bitmap> getFrames(Uri videoUri) throws IOException {
        ArrayList<Bitmap> bArray = new ArrayList<Bitmap>();
        bArray.clear();
        MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
        Log.i("VIDEO_CAPTURE","Before loading vid data with path: "+getRealPathFromURI(videoUri));
        mRetriever.setDataSource(Page1.this, videoUri);
        Log.i("VIDEO_CAPTURE","Completed setting data source with path: "+getRealPathFromURI(videoUri));
        for(int i=0;i<=44*29;i+=10) // for incrementing 1s use 1000
        {
            bArray.add(mRetriever.getFrameAtIndex(i));
        }
        Log.i("VIDEO_CAPTURE","Data ready in bitmap arrays");
        if (mRetriever != null){
            mRetriever.release();
        }
        return bArray;
    }
    public int getRedData(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[height * width];
        bmp.getPixels(pixels, 0, width, width / 2, height / 2, width / 20, height / 20);
        int sum = 0;
        for (int i = 0; i < height * width; i++) {
            int red = (pixels[i] >> 16) & 0xFF;
            sum = sum + red;
        }
        return sum;
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent intent){
        VideoView vV=(VideoView) findViewById(R.id.videoView);
        super.onActivityResult( requestCode,  resultCode,  intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = intent.getData();
            vV.setVideoURI(videoUri);
            vV.start();
            handler2.postDelayed(runnableCode2,500); // to run code in parallel
        }
    }

    // Sensor implements
    @Override
    public void onSensorChanged(SensorEvent event){
        Log.i("ACC","X: "+event.values[0]+" Y: "+event.values[1]+" Z: "+event.values[2] );
        x_arr.add(event.values[0]);
        y_arr.add(event.values[1]);
        z_arr.add(event.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    //  To switch on/off torchlight
    protected void toggleTorchLight(boolean y){
        try {
            // true sets the torch in ON mode
            cameraManager.setTorchMode(getCameraID, y);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
