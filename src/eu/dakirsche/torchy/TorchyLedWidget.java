package eu.dakirsche.torchy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Abgeleitete AppWidgetProvider Klasse als Aktivitätshandler der einzelnen Widgets
 * Dient als Basisklasse der einzelnen speziellen Widgets und wird von den einzelnen Widgetklassen extended
 */
public class TorchyLedWidget extends AppWidgetProvider {

    protected static Camera cam = null;
    public static String TOGGLE_LED_ACTION = "eu.dakirsche.torchy.LED_TOGGLE_WIDGET";
    public static String TOGGLE_LED_ACTION_ENABLE = "eu.dakirsche.torchy.LED_TOGGLE_WIDGET_ON";
    public static String TOGGLE_LED_ACTION_DISABLE = "eu.dakirsche.torchy.LED_TOGGLE_WIDGET_OFF";

    public TorchyLedWidget(){
        super();
    }
    /**
     * Aktualisierung des Widgets per Widget-Update Interval
     * @param context Anwendungscontext
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetIds  WidgetIds des WidgetsTyps
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        ComponentName thisWidget = new ComponentName(context,
                TorchyLedWidget.class);

         Log.d("TorchyWidget", "Update Torchy LED Widgets");
        // Get all ids
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            this.updateAppWidget(context, appWidgetManager, widgetId);
        } //for allWidgetIds


        Log.d("TorchyWidget", "Update Torchy LED Widgets completed");
    }

    /**
     * Wird aufgerufen, wenn das erste Widgets diesen Typs platziert wurde
     * @param context  Anwendungscontext
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

    }

    /**
     * Wird aufgerufen, wenn das letzte Widget diesen Typs entfernt wurde
     * @param context Anwendungscontext
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

    }

    /**
     * Wird aufgerufen, wenn der Widgetprovider aktiviert wird (per Timer oder WidgetUpdate)
     * @param context Anwendungscontext
     * @param intent Aufrufender Befehl
     */
    @Override
    public void onReceive(Context context, Intent intent) {
             super.onReceive(context, intent);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            Log.d("TorchyWidget", "Widget Command Received: " + intent.getAction());


            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

            if (TOGGLE_LED_ACTION_ENABLE.equals(intent.getAction()) || TOGGLE_LED_ACTION_DISABLE.equals(intent.getAction())) {
                boolean ledOn = false;
                if (TOGGLE_LED_ACTION_ENABLE.equals(intent.getAction()))
                    ledOn = true;

                Log.d("TorchyWidget", "LED should be " + (ledOn ? "Enabled" : "Disabled"));

                for (int appWidgetID: ids) {
                    updateAppWidgetByCustomAction(context, appWidgetManager, appWidgetID, ledOn);
                }
            }
            else onUpdate(context, appWidgetManager, ids);
    }
    /**
     * Aktuaisiert das Widget
     * @param context Anwendungscontext
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetID ID des zu aktualisierenden Widgets
     */
    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetID) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout_torchy_led);

        int currentStatePicture = this.getCurrentStateImgResId();

        remoteViews.setImageViewResource(R.id.torchy_led_widget_btn, currentStatePicture);

        // Beim Klick öffnet die MainActivity
        // Intent intent = new Intent(context, TorchyLEDControler.class);

        PendingIntent pendingIntent = this.getWidgetPendingIntent(context, (currentStatePicture == R.drawable.torchy_widget_off ? true : false));
        remoteViews.setOnClickPendingIntent(R.id.widget_torchy_led, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
    }

    /**
     * Liefert den OnClickPendingIntent zurück
     * @param context Anwendungskontext
     * @param nextPerform Nächste Aktion (LED an / aus)
     * @return PendingIntent
     */
    private PendingIntent getWidgetPendingIntent(Context context, boolean nextPerform){
        Intent intent;

        if (nextPerform)
            intent = new Intent(TOGGLE_LED_ACTION_ENABLE);
        else
            intent = new Intent(TOGGLE_LED_ACTION_DISABLE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
        /*Intent intent;
        intent = new Intent(context, TorchyLedWidget.class);
        if (nextPerform)
            intent.setAction(TOGGLE_LED_ACTION_ENABLE);
        else
            intent.setAction(TOGGLE_LED_ACTION_DISABLE);
        //intent = new Intent(TOGGLE_LED_ACTION);
      //  intent.putExtra("TORCHYPERFORM", nextPerform);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;             */
    }
    /**
     * Aktuaisiert das Widget
     * @param context Anwendungscontext
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetID ID des zu aktualisierenden Widgets
     */
    private void updateAppWidgetByCustomAction(Context context, AppWidgetManager appWidgetManager, int appWidgetID, boolean ledOn) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout_torchy_led);


        int currentStatePicture = R.drawable.torchy_widget_off;

        try {
            if (this.cam == null)
                this.cam = Camera.open();
            Camera.Parameters p = this.cam.getParameters();
            if (ledOn) {
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                currentStatePicture = R.drawable.torchy_widget_on;
                this.cam.setParameters(p);
                this.cam.startPreview();
            }
            else {
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                this.cam.setParameters(p);
                this.cam.startPreview();
            }


        }
        catch (Exception e){
              if (this.cam != null){
                  this.cam.stopPreview();
                  this.cam.release();
                  this.cam = null;
              }
        }

        remoteViews.setImageViewResource(R.id.torchy_led_widget_btn, currentStatePicture);

        PendingIntent pendingIntent = this.getWidgetPendingIntent(context, (currentStatePicture == R.drawable.torchy_widget_off ? true : false));

        remoteViews.setOnClickPendingIntent(R.id.widget_torchy_led, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
    }

    protected int getCurrentStateImgResId(){
        try {
            Log.d("TorchyWidgetLED", "Torchy WidgetUpdate");
            if (this.cam == null)
                this.cam = Camera.open();
            Camera.Parameters p = this.cam.getParameters();
            Log.d("TorchyWidgetLED", "Torchy GetFlashMode");
            String currentState = p.getFlashMode();//Camera.Parameters.FLASH_MODE_TORCH;

            Log.d("TorchyWidgetLED", "Torchy " + currentState);
            if (currentState == Camera.Parameters.FLASH_MODE_TORCH)
                return R.drawable.torchy_widget_on;

            return R.drawable.torchy_widget_off;
        }
        catch (Exception e){
            Log.e("TorchyWidgetLED", "Torchy Error occured: ", e);
            return R.drawable.torchy_widget_off;
        }
    }
}
