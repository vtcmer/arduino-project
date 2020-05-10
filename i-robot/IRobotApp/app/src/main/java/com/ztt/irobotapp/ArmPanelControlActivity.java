package com.ztt.irobotapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ztt.irobotapp.services.BluetoothService;
import com.ztt.irobotapp.services.ComunicationService;
import com.ztt.irobotapp.services.ComunicationView;

import java.net.ConnectException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArmPanelControlActivity extends AppCompatActivity implements ComunicationView {


    @BindView(R.id.btnForward)
    Button btnForward;
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.btnCenter)
    Button btnCenter;

    @BindView(R.id.btnUp)
    ImageButton btnUp;
    @BindView(R.id.btnDown)
    ImageButton btnDown;

    @BindView(R.id.btnLeft)
    ImageButton btnLeft;

    @BindView(R.id.btnStop)
    Button btnStop;
    @BindView(R.id.btnRight)
    ImageButton btnRight;

    @BindView(R.id.btnOpen)
    Button btnOpen;
    @BindView(R.id.btnClose)
    Button btnClose;


    private BluetoothService bluetoothService;
    private ComunicationService comunicationService;

    // String for MAC address
    private static String address = null;

    final static Integer SPEED_LEFT_DEFAULT = 200;
    final static Integer SPEED_RIGHT_DEFATUL = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arm_panel_control);

        ButterKnife.bind(this);

        this.bluetoothService = new BluetoothService();
        this.comunicationService = new ComunicationService(this);

        checkBTState();
    }


    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        BluetoothSocket btSocket = null;
        try {
            btSocket = this.bluetoothService.createConnection(address);
        } catch (ConnectException e) {
            Toast.makeText(getBaseContext(), "Error conectando al dispositivo " + address, Toast.LENGTH_SHORT).show();
        }

        if (btSocket != null) {
            this.comunicationService.connect(btSocket);
        } else {
            Toast.makeText(getBaseContext(), "Error creando el socket de comunicación", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            this.bluetoothService.closeConnection();
        } catch (ConnectException e) {
            Log.e("PanelControlActivity", "Error cerrando la conexión al dispositivo " + address, e);
            Toast.makeText(getBaseContext(), "Error cerrando la conexión al dispositivo " + address, Toast.LENGTH_SHORT).show();
        }

    }



    /**
     * Verifica el estado del bluetooth
     */
    private void checkBTState() {

        Boolean enabled = bluetoothService.isEnabled();

        if (!enabled) {
            //Prompt user to turn on Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

        }
    }


    @OnClick({R.id.btnStop, R.id.btnCenter, R.id.btnForward, R.id.btnBack, R.id.btnRight
            , R.id.btnLeft, R.id.btnUp, R.id.btnDown, R.id.btnOpen, R.id.btnClose})
    public void onChangeDirection(View view) {

        this.resetStatusActionsButtons();
        switch (view.getId()) {
            case R.id.btnCenter:
                comunicationService.write("C");    // Send "S" via Bluetooth
                break;
            case R.id.btnStop:
                comunicationService.write("S");    // Send "S" via Bluetooth
                break;
            case R.id.btnForward:
                this.selectAction(this.btnForward);
                comunicationService.write("F");    // Send "F" via Bluetooth
                break;
            case R.id.btnBack:
                this.selectAction(this.btnBack);
                comunicationService.write("B");    // Send "B" via Bluetooth
                break;
            case R.id.btnRight:
                this.selectAction(this.btnRight);
                comunicationService.write("R");    // Send "R" via Bluetooth
                break;
            case R.id.btnLeft:
                this.selectAction(this.btnLeft);
                comunicationService.write("L");    // Send "L" via Bluetooth
                break;
            case R.id.btnUp:
                this.selectAction(this.btnUp);
                comunicationService.write("U");    // Send "L" via Bluetooth
                break;
            case R.id.btnDown:
                this.selectAction(this.btnDown);
                comunicationService.write("D");    // Send "L" via Bluetooth
                break;
            case R.id.btnOpen:
                this.selectAction(this.btnOpen);
                comunicationService.write("O");    // Send "L" via Bluetooth
                break;
            case R.id.btnClose:
                this.selectAction(this.btnClose);
                comunicationService.write("E");    // Send "L" via Bluetooth
                break;
        }
    }



    /**
     * Activa y desactiva los botones
     *
     * @param active
     */
    private void changeStatusActionButtons(boolean active) {

        this.btnForward.setEnabled(active);
        this.btnBack.setEnabled(active);
        this.btnRight.setEnabled(active);
        this.btnLeft.setEnabled(active);
        this.btnCenter.setEnabled(active);

    }

    /**
     * Marca el botón como activo
     *
     * @param imageButton
     */
    private void selectAction(ImageButton imageButton) {
        imageButton.setBackgroundDrawable(getResources().getDrawable(R.color.active));
    }

    private void selectAction(Button button) {
        button.setBackgroundDrawable(getResources().getDrawable(R.color.active));
    }

    /**
     * Resetea el botón de acción al color por defecto
     *
     * @param imageButton
     */
    private void resetAction(ImageButton imageButton) {
        imageButton.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
    }

    /**
     * Establece el valor de colores por defecto de los botones de acción
     */
    private void resetStatusActionsButtons() {

        this.btnForward.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnBack.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnRight.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnLeft.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnUp.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnDown.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnOpen.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnClose.setBackgroundDrawable(getResources().getDrawable(R.color.enabled));
        this.btnCenter.setBackgroundDrawable(getResources().getDrawable(R.color.stop));
    }

    @Override
    public void message(Message msg) {

        String data = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread

        if (data.equals("F")) {
            resetStatusActionsButtons();
            selectAction(btnForward);
        } else if (data.equals("B")) {
            resetStatusActionsButtons();
            selectAction(btnBack);
        } else if (data.equals("R")) {
            resetStatusActionsButtons();
            selectAction(btnRight);
        } else if (data.equals("L")) {
            resetStatusActionsButtons();
            selectAction(btnLeft);
        } else if (data.equals("U")) {
            resetStatusActionsButtons();
            selectAction(btnUp);
        } else if (data.equals("D")) {
            resetStatusActionsButtons();
            selectAction(btnDown);
        } else if (data.equals("O")) {
            resetStatusActionsButtons();
            selectAction(btnOpen);
        } else if (data.equals("E")) {
            resetStatusActionsButtons();
            selectAction(btnClose);
        }

    }

    @Override
    public void finishProcess() {

        Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
        finish();

    }


}
