package com.ztt.irobotapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    Handler bluetoothIn;
    final int handlerState = 0;                         //used to identify handler message

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @BindView(R.id.btnManual)
    Button btnManual;
    @BindView(R.id.btnAutomatic)
    Button btnAutomatic;
    @BindView(R.id.btnForward)
    ImageButton btnForward;
    @BindView(R.id.btnLeft)
    ImageButton btnLeft;
    @BindView(R.id.btnStop)
    Button btnStop;
    @BindView(R.id.btnRight)
    ImageButton btnRight;
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedThread mConnectedThread;

    private StringBuilder recDataString = new StringBuilder();
    // String for MAC address
    private static String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        bluetoothIn = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {

                    String data = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread

                    if (data.equals("F")){
                        resetStatusActionsButtons();
                        selectAction(btnForward);
                    } else if (data.equals("B")){
                        resetStatusActionsButtons();
                        selectAction(btnBack);
                    } else if (data.equals("R")){
                        resetStatusActionsButtons();
                        selectAction(btnRight);
                    } else if (data.equals("L")){
                        resetStatusActionsButtons();
                        selectAction(btnLeft);
                    }

                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        //Log.i("ramiro", "adress : " + address);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }


    @OnClick({R.id.btnAutomatic, R.id.btnManual})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnManual:
                this.changeMode(false);
                mConnectedThread.write("1");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Manual", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAutomatic:
                this.changeMode(true);
                mConnectedThread.write("2");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Automático", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @OnClick(R.id.btnManual)
    public void onViewClicked() {
    }

    @OnClick({R.id.btnStop, R.id.btnForward, R.id.btnBack, R.id.btnRight, R.id.btnLeft})
    public void onChangeDirection(View view) {

        this.resetStatusActionsButtons();
        switch (view.getId()) {
            case R.id.btnStop:
                mConnectedThread.write("S");    // Send "S" via Bluetooth
                break;
            case R.id.btnForward:
                this.selectAction(this.btnForward);
                mConnectedThread.write("F");    // Send "F" via Bluetooth
                break;
            case R.id.btnBack:
                this.selectAction(this.btnBack);
                mConnectedThread.write("B");    // Send "B" via Bluetooth
                break;
            case R.id.btnRight:
                this.selectAction(this.btnRight);
                mConnectedThread.write("R");    // Send "R" via Bluetooth
                break;
            case R.id.btnLeft:
                this.selectAction(this.btnLeft);
                mConnectedThread.write("L");    // Send "L" via Bluetooth
                break;
        }
    }

    /**
     * Cambio entre modo manual y automático
     * @param automatic
     */
    void changeMode(boolean automatic) {
        if (automatic) {
            this.btnAutomatic.setBackgroundDrawable(getResources().getDrawable(R.color.active));
            this.btnManual.setBackgroundDrawable(getResources().getDrawable(R.color.disabled));
            this.changeStatusActionButtons(false);
        } else {
            this.btnManual.setBackgroundDrawable(getResources().getDrawable(R.color.active));
            this.btnAutomatic.setBackgroundDrawable(getResources().getDrawable(R.color.disabled));
            this.changeStatusActionButtons(true);
        }

        this.resetStatusActionsButtons();
    }

    /**
     * Activa y desactiva los botones
     * @param active
     */
    private void changeStatusActionButtons(boolean active) {

        this.btnForward.setEnabled(active);
        this.btnBack.setEnabled(active);
        this.btnRight.setEnabled(active);
        this.btnLeft.setEnabled(active);
        this.btnStop.setEnabled(active);

    }

    /**
     * Marca el botón como activo
     * @param imageButton
     */
    private void selectAction(ImageButton imageButton){
        imageButton.setBackgroundDrawable(getResources().getDrawable(R.color.active));
    }

    /**
     * Resetea el botón de acción al color por defecto
     * @param imageButton
     */
    private void resetAction(ImageButton imageButton){
        imageButton.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
    }

    /**
     * Establece el valor de colores por defecto de los botones de acción
     */
    private void resetStatusActionsButtons(){

        this.btnForward.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnBack.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnRight.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnLeft.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnStop.setBackgroundDrawable(getResources().getDrawable(R.color.stop));
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }


}
