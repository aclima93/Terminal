package pt.uc.student.aclima.device_agent.Collectors.EventfulDataCollector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.util.Date;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;

import static android.content.Intent.ACTION_BATTERY_LOW;
import static android.content.Intent.ACTION_BATTERY_OKAY;
import static android.content.Intent.ACTION_DATE_CHANGED;
import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;
import static android.content.Intent.ACTION_TIMEZONE_CHANGED;
import static android.content.Intent.ACTION_TIME_CHANGED;
import static pt.uc.student.aclima.device_agent.Database.Entries.Measurement.DELIMITER;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * Collected Data:
 * - Base station connection/disconnection
 * - Charging State
 * - Network (WiFi/3G) connection/disconnection
 * - Time and Time Zone changes
 * - Power On/Off
 */
public class EventfulIntentService extends IntentService {

    private static final String EXTRA_TIME_CHANGE_PARAM = "pt.uc.student.aclima.device_agent.Collectors.EventfulIntentService.extra.TIME_CHANGE_PARAM";

    private static final String EXTRA_CHARGING_CHANGE_PARAM1 = "pt.uc.student.aclima.device_agent.Collectors.EventfulIntentService.extra.CHARGING_CHANGE_PARAM1";
    private static final String EXTRA_CHARGING_CHANGE_PARAM2 = "pt.uc.student.aclima.device_agent.Collectors.EventfulIntentService.extra.CHARGING_CHANGE_PARAM2";

    private static final String EXTRA_CONNECTION_CHANGE_PARAM1 = "pt.uc.student.aclima.device_agent.Collectors.EventfulIntentService.extra.CONNECTION_CHANGE_PARAM1";
    private static final String EXTRA_CONNECTION_CHANGE_PARAM2 = "pt.uc.student.aclima.device_agent.Collectors.EventfulIntentService.extra.CONNECTION_CHANGE_PARAM2";

    public EventfulIntentService() {
        super("EventfulIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionTimeChange(Context context, String action, String time) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_TIME_CHANGE_PARAM, time);
        context.startService(intent);
    }

    public static void startActionChargingChange(Context context, String action, boolean isCharging, String chargingMethod) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_CHARGING_CHANGE_PARAM1, isCharging);
        intent.putExtra(EXTRA_CHARGING_CHANGE_PARAM2, chargingMethod);
        context.startService(intent);
    }

    public static void startActionConnectionChange(Context context, String action, boolean isConnected, String connectedMethod) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_CONNECTION_CHANGE_PARAM1, isConnected);
        intent.putExtra(EXTRA_CONNECTION_CHANGE_PARAM2, connectedMethod);
        context.startService(intent);
    }

    public static void startActionScreenChange(Context context, String action) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void startActionPowerChange(Context context, String action) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_TIME_CHANGED.equals(action)) {
                String time = intent.getStringExtra(EXTRA_TIME_CHANGE_PARAM);
                handleActionTimeChange("TIME_CHANGED", time);
            }
            else if (ACTION_DATE_CHANGED.equals(action)) {
                String time = intent.getStringExtra(EXTRA_TIME_CHANGE_PARAM);
                handleActionTimeChange("DATE_CHANGED", time);
            }
            else if (ACTION_TIMEZONE_CHANGED.equals(action)) {
                String time = intent.getStringExtra(EXTRA_TIME_CHANGE_PARAM);
                handleActionTimeChange("TIMEZONE_CHANGED", time);
            }
            else if(ACTION_POWER_CONNECTED.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_CHARGING_CHANGE_PARAM1, false);
                String chargingMethod = intent.getStringExtra(EXTRA_CHARGING_CHANGE_PARAM2);
                handleActionChargingChange("POWER_CONNECTED", isCharging, chargingMethod);
            }
            else if(ACTION_POWER_DISCONNECTED.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_CHARGING_CHANGE_PARAM1, false);
                String chargingMethod = intent.getStringExtra(EXTRA_CHARGING_CHANGE_PARAM2);
                handleActionChargingChange("POWER_DISCONNECTED", isCharging, chargingMethod);
            }
            else if(ACTION_BATTERY_LOW.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_CHARGING_CHANGE_PARAM1, false);
                String chargingMethod = intent.getStringExtra(EXTRA_CHARGING_CHANGE_PARAM2);
                handleActionChargingChange("BATTERY_LOW", isCharging, chargingMethod);
            }
            else if(ACTION_BATTERY_OKAY.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_CHARGING_CHANGE_PARAM1, false);
                String chargingMethod = intent.getStringExtra(EXTRA_CHARGING_CHANGE_PARAM2);
                handleActionChargingChange("BATTERY_OKAY", isCharging, chargingMethod);
            }
            else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
                boolean isConnected = intent.getBooleanExtra(EXTRA_CONNECTION_CHANGE_PARAM1, false);
                String connectedMethod = intent.getStringExtra(EXTRA_CONNECTION_CHANGE_PARAM2);
                handleActionConnectionChange(isConnected, connectedMethod);
            }
            else if(Intent.ACTION_SCREEN_ON.equals(action)
                    || Intent.ACTION_SCREEN_OFF.equals(action)){
                handleActionScreenChange(action);
            }
            else if(Intent.ACTION_SHUTDOWN.equals(action)){
                handleActionPowerChange(action);
            }

        }
    }

    private void handleActionTimeChange(String event, String newTime) {
        Log.d("TimeChange", "TimeChange service called for event " + event);

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {
            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "TimeChange" + DELIMITER + event, newTime, "time", timestamp);
            if (!success) {
                Log.e("TimeChange", "TimeChange" + DELIMITER + event + "service failed to add row.");
            }
        }
    }

    private void handleActionChargingChange(String event, boolean isCharging, String chargingMethod) {
        Log.d("ChargingChange", "ChargingChange service called for event " + event);

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            String entryValue = "isCharging" + DELIMITER + isCharging + "\n"+
                    "chargingMethod" + DELIMITER + chargingMethod;

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "ChargingChange" + DELIMITER + event, entryValue, "", timestamp);
            if (!success) {
                Log.e("ChargingChange", "ChargingChange" + DELIMITER + event + "service failed to add row.");
            }
        }
    }


    private void handleActionConnectionChange(boolean isConnected, String connectedMethod) {
        Log.d("ConnectionChange", "ConnectionChange service called");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            String entryValue = "isConnected" + DELIMITER + isConnected + "\n"+
                    "connectedMethod" + DELIMITER + connectedMethod;

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "ConnectionChange", entryValue, "", timestamp);
            if (!success) {
                Log.e("ConnectionChange", "ConnectionChange service failed to add row.");
            }
        }
    }

    private void handleActionScreenChange(String action) {
        Log.d("ScreenChange", "ScreenChange service called");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "ScreenChange", action, "", timestamp);
            if (!success) {
                Log.e("ScreenChange", "ScreenChange service failed to add row.");
            }
        }
    }

    private void handleActionPowerChange(String action) {
        Log.d("PowerChange", "PowerChange service called");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "PowerChange", action, "", timestamp);
            if (!success) {
                Log.e("PowerChange", "PowerChange service failed to add row.");
            }
        }
    }

    public static void handleCurrentBaseStationChange(Context context, CellLocation location){
        Log.d("BaseStationChange", "BaseStationChange" + DELIMITER + "Current service called");

        Date timestamp = new Date();

        if(context != null) {

            String unitsOfMeasurement = "";
            String measurement = "";

            if (location instanceof CdmaCellLocation) {

                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) location;

                unitsOfMeasurement = "BaseStationId" + DELIMITER + "NetworkId" + DELIMITER + "SystemId" +
                        DELIMITER + "Latitude" + DELIMITER + "Longitude" + DELIMITER + "NetworkType";

                measurement += cdmaCellLocation.getBaseStationId() + DELIMITER;
                measurement += cdmaCellLocation.getNetworkId() + DELIMITER;
                measurement += cdmaCellLocation.getSystemId() + DELIMITER;
                measurement += cdmaCellLocation.getBaseStationLatitude() + DELIMITER;
                measurement += cdmaCellLocation.getBaseStationLongitude() + DELIMITER;
                measurement += "CDMA" + DELIMITER + "\n";

            } else if (location instanceof GsmCellLocation) {

                unitsOfMeasurement = "CID" + DELIMITER + "LAC" + DELIMITER + "PSC" +
                        DELIMITER + "NetworkType";

                GsmCellLocation gsmCellLocation = (GsmCellLocation) location;

                measurement += gsmCellLocation.getCid() + DELIMITER;
                measurement += gsmCellLocation.getLac() + DELIMITER;
                measurement += gsmCellLocation.getPsc() + DELIMITER;
                measurement += "GSM" + DELIMITER + "\n";

            }

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "BaseStationChange" + DELIMITER + "Current", measurement, unitsOfMeasurement, timestamp);
            if (!success) {
                Log.e("BaseStationChange", "BaseStationChange" + DELIMITER + "Current service failed to add row.");
            }

        }

    }

    public static void handleNeighbourBaseStationChange(Context context){
        Log.d("BaseStationChange", "BaseStationChange" + DELIMITER + "Neighbours service called");

        Date timestamp = new Date();

        if(context != null) {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // also listen for properties of the neighboring cell towers
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                for(CellInfo cellInfo : telephonyManager.getAllCellInfo()) {

                    String unitsOfMeasurement = "";
                    String measurement = "";

                    /*
                     * CellInfo is a superclass for the various types of cell tower technologies
                     *
                     * <b>CDMA</b> (Code Division Multiple Access) is a "spread spectrum" for cellular networks
                     * enabling many more wireless users to share airwaves than alternative technologies.
                     *
                     * <b>GSM</b> (Global System for Mobile Communications) is a wireless technology to
                     * describe protocols for cellular networks used by mobile phones with over 80%
                     * market share globally.
                     *
                     * <b>LTE</b> (Long Term Evolution) is a wireless broadband technology for
                     * communication of high speed data for mobile phones.
                     */

                    if( cellInfo instanceof CellInfoGsm){

                        unitsOfMeasurement = "CID" + DELIMITER +"LAC" + DELIMITER +"PSC" +
                                DELIMITER + "Latitude" + DELIMITER + "Longitude" +
                                DELIMITER + "NetworkType" + DELIMITER +"RSSI dBm";

                        CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                        measurement += cellInfoGsm.getCellIdentity().getCid() + DELIMITER;
                        measurement += cellInfoGsm.getCellIdentity().getLac() + DELIMITER;
                        measurement += cellInfoGsm.getCellIdentity().getPsc() + DELIMITER;
                        measurement += "GSM" + DELIMITER;
                        measurement += cellInfoGsm.getCellSignalStrength().getDbm() + "\n";
                    }
                    else if( cellInfo instanceof CellInfoCdma){

                        unitsOfMeasurement = "BasestationId" + DELIMITER +"NetworkId" + DELIMITER +"SystemId" +
                                DELIMITER + "NetworkType" + DELIMITER +"RSSI dBm";

                        CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
                        measurement += cellInfoCdma.getCellIdentity().getBasestationId() + DELIMITER;
                        measurement += cellInfoCdma.getCellIdentity().getNetworkId() + DELIMITER;
                        measurement += cellInfoCdma.getCellIdentity().getSystemId() + DELIMITER;
                        measurement += cellInfoCdma.getCellIdentity().getLatitude() + DELIMITER;
                        measurement += cellInfoCdma.getCellIdentity().getLongitude() + DELIMITER;
                        measurement += "CDMA" + DELIMITER;
                        measurement += cellInfoCdma.getCellSignalStrength().getDbm() + "\n";

                    }
                    else if( cellInfo instanceof CellInfoLte){

                        unitsOfMeasurement = "CI" + DELIMITER +"PCI" + DELIMITER +"TAC" +
                                DELIMITER + "NetworkType" + DELIMITER +"RSSI dBm";

                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                        measurement += cellInfoLte.getCellIdentity().getCi() + DELIMITER;
                        measurement += cellInfoLte.getCellIdentity().getPci() + DELIMITER;
                        measurement += cellInfoLte.getCellIdentity().getTac() + DELIMITER;
                        measurement += "LTE" + DELIMITER;
                        measurement += cellInfoLte.getCellSignalStrength().getDbm() + "\n";
                    }
                    else if( cellInfo instanceof CellInfoWcdma){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

                            unitsOfMeasurement = "CID" + DELIMITER +"LAC" + DELIMITER +"PSC" +
                                    DELIMITER + "NetworkType" + DELIMITER +"RSSI dBm";

                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                            measurement += cellInfoWcdma.getCellIdentity().getCid() + DELIMITER;
                            measurement += cellInfoWcdma.getCellIdentity().getLac() + DELIMITER;
                            measurement += cellInfoWcdma.getCellIdentity().getPsc() + DELIMITER;
                            measurement += "WCDMA" + DELIMITER;
                            measurement += cellInfoWcdma.getCellSignalStrength().getDbm() + "\n";

                        }
                    }

                    boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                            "BaseStationChange" + DELIMITER + "Neighbours", measurement, unitsOfMeasurement, timestamp);
                    if (!success) {
                        Log.e("BaseStationChange", "BaseStationChange" + DELIMITER + "Neighbours service failed to add row.");
                    }

                }
            }
            else {
                // older devices will use this older method of obtaining neighboring information

                String unitsOfMeasurement = "CID" + DELIMITER +"LAC" + DELIMITER +"PSC" +
                        DELIMITER + "NetworkType" + DELIMITER +"RSSI";

                for (NeighboringCellInfo neighboringCellInfo : telephonyManager.getNeighboringCellInfo()) {

                    String measurement = "";
                    measurement += neighboringCellInfo.getCid() + DELIMITER;
                    measurement += neighboringCellInfo.getLac() + DELIMITER;
                    measurement += neighboringCellInfo.getPsc() + DELIMITER;
                    measurement += neighboringCellInfo.getNetworkType() + DELIMITER;
                    measurement += neighboringCellInfo.getRssi() + "\n";

                    boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                            "BaseStationChange" + DELIMITER + "Neighbours", measurement, unitsOfMeasurement, timestamp);
                    if (!success) {
                        Log.e("BaseStationChange", "BaseStationChange" + DELIMITER + "Neighbours service failed to add row.");
                    }

                }

            }

        }

    }

    public static void handleCurrentBaseStationSignalStrengthChange(Context context, SignalStrength signalStrength) {
        Log.d("BaseStationChange", "BaseStationChange" + DELIMITER + "Current Signal Strength service called");

        Date timestamp = new Date();

        if(context != null) {

            String measurement = "";
            String unitsOfMeasurement = "";

            if (signalStrength.isGsm()) {
                measurement = signalStrength.getGsmSignalStrength() + "";
                unitsOfMeasurement = "GSM SignalStrength";
            } else {
                measurement = signalStrength.getCdmaDbm() + "";
                unitsOfMeasurement = "CDMA Dbm";
            }

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "BaseStationChange" + DELIMITER + "Current", measurement, unitsOfMeasurement, timestamp);
            if (!success) {
                Log.e("BaseStationChange", "BaseStationChange service failed to add row.");
            }
        }
    }

}
