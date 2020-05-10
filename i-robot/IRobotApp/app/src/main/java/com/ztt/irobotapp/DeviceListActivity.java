package com.ztt.irobotapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ztt.irobotapp.services.BluetoothService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // Debugging for LOGCAT
    private static final String TAG = "DeviceListActivity";


    // EXTRA string to send on to mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    @BindView(R.id.connecting)
    TextView connecting;
    @BindView(R.id.paired_devices)
    ListView pairedListDevices;
    @BindView(R.id.title_paired_devices)
    TextView titlePairedDevices;


    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    private BluetoothService bluetoothService;
    private List<BluetoothDevice> devicesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        ButterKnife.bind(this);

        try {
            bluetoothService = new BluetoothService();
        } catch (RuntimeException r) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        checkBTState();

        connecting.setTextSize(40);
        connecting.setText(" ");

        // Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        pairedListDevices.setAdapter(mPairedDevicesArrayAdapter);
        pairedListDevices.setOnItemClickListener(this);

        devicesList = bluetoothService.getDevicesAvailables();


        // Add previosuly paired devices to the array
        if (devicesList.size() > 0) {
            titlePairedDevices.setVisibility(View.VISIBLE);//make title viewable
            for (BluetoothDevice device : devicesList) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }



    /**
     * Verifica el estado de la conexión con bluetooth
     */
    private void checkBTState() {

        Boolean enabled = bluetoothService.isEnabled();

        if (!enabled) {
            //Prompt user to turn on Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        connecting.setText("Conectando...");
        BluetoothDevice device = devicesList.get(position);

        Intent i = new Intent(DeviceListActivity.this, ArmPanelControlActivity.class);
        i.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());
        startActivity(i);

    }
}
