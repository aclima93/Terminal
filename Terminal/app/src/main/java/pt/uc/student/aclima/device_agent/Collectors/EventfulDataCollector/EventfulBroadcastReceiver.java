package pt.uc.student.aclima.device_agent.Collectors.EventfulDataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;

import java.util.Date;

import static android.content.Intent.ACTION_BATTERY_LOW;
import static android.content.Intent.ACTION_BATTERY_OKAY;
import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;

public class EventfulBroadcastReceiver extends BroadcastReceiver {

    public EventfulBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if (Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_DATE_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {

            EventfulIntentService.startActionTimeChange(context, action, new Date(System.currentTimeMillis()).toString());
        }
        else if(ACTION_POWER_CONNECTED.equals(action)
                || ACTION_POWER_DISCONNECTED.equals(action)
                || ACTION_BATTERY_LOW.equals(action)
                || ACTION_BATTERY_OKAY.equals(action)){

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean chargingUSB = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean chargingAC = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            boolean chargingWireless = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                chargingWireless = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
            }

            String chargingMethod;
            if(chargingUSB) {
                chargingMethod = "USB";
            }
            else if(chargingAC) {
                chargingMethod = "AC";
            }
            else if(chargingWireless) {
                chargingMethod = "Wireless";
            }
            else{
                chargingMethod = "None";
            }

            EventfulIntentService.startActionChargingChange(context, action, isCharging, chargingMethod);
        }
        else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

            boolean isConnected;
            String connectedMethod;

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                // connected to the internet
                isConnected = true;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    connectedMethod = "WiFi";
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    connectedMethod = "Mobile";
                }
                else{
                    connectedMethod = "Unknown";
                }
            } else {
                // not connected to the internet
                isConnected = false;
                connectedMethod = "None";
            }

            EventfulIntentService.startActionConnectionChange(context, action, isConnected, connectedMethod);
        }
        else if(Intent.ACTION_SCREEN_ON.equals(action)
                || Intent.ACTION_SCREEN_OFF.equals(action)){
            EventfulIntentService.startActionScreenChange(context, action);
        }
        else if(Intent.ACTION_SHUTDOWN.equals(action)){
            EventfulIntentService.startActionPowerChange(context, action);
        }
    }
}
