package eu.dakirsche.torchy;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

public class ScreenTorch extends Activity
{
    private int brightnessOnStartup = 0;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("ScreenTorch", "ScreenTorch startet");
        setContentView(R.layout.screentorch);

        int color = Color.argb(255, 255, 255, 255); //White as default color

        if (getIntent() != null && getIntent().getExtras() != null){
            color = getIntent().getExtras().getInt("TORCHCOLOR", color);
        }
        Log.d("ScreenTorch", "COLOR: " + color);
        ((TextView) findViewById(R.id.textViewgoback)).setBackgroundColor(color);
        ((TextView) findViewById(R.id.textViewgoback)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        /* Helligkeit hochregeln */
        try {
            ContentResolver cr = getContentResolver();
            int brightness = Settings.System.getInt(cr,Settings.System.SCREEN_BRIGHTNESS);
            this.brightnessOnStartup = brightness;
            Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = 1f;
            getWindow().setAttributes(lp);
        }
        catch (Settings.SettingNotFoundException e){
            Toast.makeText(getApplicationContext(), R.string.cannot_raise_brightness, Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onDestroy(){
        /* Helligkeit zurückstellen */
        if (this.brightnessOnStartup > 0) {
            try {
                ContentResolver cr = getContentResolver();
                int brightness = this.brightnessOnStartup;
                Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = brightness / 255.0f;
                getWindow().setAttributes(lp);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), R.string.cannot_reset_brightness, Toast.LENGTH_LONG).show();
            }
        }
        super.onDestroy();
    }
    protected void goBack(){
        Intent main = new Intent(ScreenTorch.this, TorchyMainScreen.class);
        startActivity(main);
        this.finish();
    }
}
