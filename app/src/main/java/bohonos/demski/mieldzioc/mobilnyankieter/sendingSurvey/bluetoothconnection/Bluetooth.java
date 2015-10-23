package bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.bluetoothconnection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Dominik Demski on 2015-10-19.
 */
public class Bluetooth {
    public static boolean isDeviceSupportingBluetooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return bluetoothAdapter != null;
    }

    public static void requestBluetoothToBeEnabledIfItIsNot(Activity activity){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(activity, enableBtIntent, 1, null);
        }
    }

    public static List<BluetoothDevice> getPairedDevices(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return new ArrayList<>(mBluetoothAdapter.getBondedDevices());
    }
}
