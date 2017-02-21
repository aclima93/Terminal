package pt.uc.student.aclima.device_agent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by aclima on 20/02/2017.
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
                //start the DeviceAgent Activity on bootup
                Intent newActivityIntent = new Intent(context, DeviceAgentActivity.class);
                newActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newActivityIntent);
            }
        }
    }

}
