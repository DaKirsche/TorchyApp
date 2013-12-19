package eu.dakirsche.torchy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class TorchyMainScreen extends Activity
{
    private int red, green, blue, color;
    private ColorSeekbarChangeListener listener_red, listener_green, listener_blue;
    private ImageView previewArea;
    private TextView lbl_red, lbl_green, lbl_blue;
    private SeekBar seek_red, seek_green, seek_blue;
    private boolean led_light_avaiable = false;
    private boolean led_active = false;
    protected PowerManager.WakeLock mWakeLock;
    protected static Camera cam = null;

    @Override
    protected void onDestroy(){
       if (this.led_active) this.toggleLedFlash();
        this.mWakeLock.release();
        if (this.cam != null)
            this.cam.release();
        super.onDestroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Keep screen on
        final PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DisableScreenOff");
        this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        this.mWakeLock.acquire();

        //Checking if LED light avaiable
        this.led_light_avaiable = this.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!this.led_light_avaiable){
            ((TextView) findViewById(R.id.textView4)).setText(R.string.led_not_avaiable);
            ((ToggleButton) findViewById(R.id.button1)).setVisibility(View.INVISIBLE);
        }
        else {
            (
                (Button) findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleLedFlash();
                    }
                });
        }

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenTorch();
            }
        });
        ((Button) findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopApp();
            }
        });

        this.seek_red = (SeekBar) findViewById(R.id.seekBar);
        this.seek_green = (SeekBar) findViewById(R.id.seekBar1);
        this.seek_blue = (SeekBar) findViewById(R.id.seekBar2);

        this.lbl_red = (TextView) findViewById(R.id.color_red);
        this.lbl_green = (TextView) findViewById(R.id.color_green);
        this.lbl_blue = (TextView) findViewById(R.id.color_blue);

        this.previewArea = (ImageView) findViewById(R.id.imageView);

        ColorSeekbarChangeListener listener_red = new ColorSeekbarChangeListener(this);
        ColorSeekbarChangeListener listener_green = new ColorSeekbarChangeListener(this);
        ColorSeekbarChangeListener listener_blue = new ColorSeekbarChangeListener(this);

        seek_red.setOnSeekBarChangeListener(listener_red);
        seek_green.setOnSeekBarChangeListener(listener_green);
        seek_blue.setOnSeekBarChangeListener(listener_blue);

    }
    protected void stopApp(){
       // this.finish();
    }
    protected void setScreenTorch(){
        this.onColorHasChanged();
        Intent intent = new Intent(TorchyMainScreen.this, ScreenTorch.class);

        intent.putExtra("TORCHCOLOR", this.color);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
    protected void toggleLedFlash(){
        try {
            if (this.cam == null)
                this.cam = Camera.open();
              if (!this.led_active){
                  Parameters p = this.cam.getParameters();
                  p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                  this.cam.setParameters(p);
                  this.cam.startPreview();
                  this.led_active = true;
              }
              else {
                  this.cam.stopPreview();
                  this.cam.release();
                  this.cam = null;
                  this.led_active = false;
              }
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(), R.string.cannot_access_led, Toast.LENGTH_LONG).show();
            Log.e("Torchy", "Fehler bei LED: " + (this.led_active ? "OFF" : "ON"), e);
        }

    }

    public void onColorHasChanged(){
        this.red = (int) Math.ceil(this.seek_red.getProgress() * 2.55);
        this.green = (int) Math.ceil(this.seek_green.getProgress() * 2.55);
        this.blue = (int) Math.ceil(this.seek_blue.getProgress() * 2.55);

        this.lbl_red.setText(this.red + "");
        this.lbl_green.setText(this.green + "");
        this.lbl_blue.setText(this.blue + "");


        int previewColor = Color.argb(255, this.red, this.green, this.blue);
        this.color = previewColor;
        this.previewArea.setBackgroundColor(previewColor);
    }
}
