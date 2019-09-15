package com.ztt.irobotapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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

public class PanelControlActivity extends AppCompatActivity implements ComunicationView {


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
    @BindView(R.id.txtSeekBarLeft)
    TextView txtSeekBarLeft;
    @BindView(R.id.seekBarLeft)
    SeekBar seekBarLeft;
    @BindView(R.id.txtSeekBarRigth)
    TextView txtSeekBarRigth;
    @BindView(R.id.seekBarRight)
    SeekBar seekBarRight;

    private BluetoothService bluetoothService;
    private ComunicationService comunicationService;

    // String for MAC address
    private static String address = null;

    final static Integer SPEED_LEFT_DEFAULT = 200;
    final static Integer SPEED_RIGHT_DEFATUL = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_control);
        ButterKnife.bind(this);

        this.bluetoothService = new BluetoothService();
        this.comunicationService = new ComunicationService(this);

        this.onSeekBarChange();

        checkBTState();

    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        BluetoothSocket btSocket = null;
        try {
            btSocket =   this.bluetoothService.createConnection(address);
        } catch (ConnectException e) {
            Toast.makeText(getBaseContext(), "Error conectando al dispositivo "+address, Toast.LENGTH_SHORT).show();
        }

        if (btSocket != null){
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
            Log.e("PanelControlActivity", "Error cerrando la conexión al dispositivo "+address,e);
            Toast.makeText(getBaseContext(), "Error cerrando la conexión al dispositivo "+address, Toast.LENGTH_SHORT).show();
        }

    }


    @OnClick({R.id.btnAutomatic, R.id.btnManual, R.id.btnModeStop})
    public void onChangeMode(View view) {
        switch (view.getId()) {
            case R.id.btnManual:
                this.changeMode(false);
                comunicationService.write("1");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Manual", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAutomatic:
                this.changeMode(true);
                comunicationService.write("2");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Automático", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnModeStop:
                comunicationService.write("3");
                Toast.makeText(getBaseContext(), "Stop", Toast.LENGTH_SHORT).show();
                break;
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



    @OnClick({R.id.btnStop, R.id.btnForward, R.id.btnBack, R.id.btnRight, R.id.btnLeft})
    public void onChangeDirection(View view) {

        this.resetStatusActionsButtons();
        switch (view.getId()) {
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
        }
    }

    /**
     * Cambio entre modo manual y automático
     *
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
     *
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
     *
     * @param imageButton
     */
    private void selectAction(ImageButton imageButton) {
        imageButton.setBackgroundDrawable(getResources().getDrawable(R.color.active));
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
        this.btnStop.setBackgroundDrawable(getResources().getDrawable(R.color.stop));
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
        }

    }

    @Override
    public void finishProcess() {

        Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
        finish();

    }




    void onSeekBarChange(){

        this.seekBarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            Integer speed = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

                speed = SPEED_LEFT_DEFAULT + progressValue;

                txtSeekBarLeft.setText("IZ: " + speed);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtSeekBarLeft.setText("IZ: " + speed );
                comunicationService.write("L-"+speed);
                // TODO ENVIAR DATO

            }
        });


        this.seekBarRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            Integer speed = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                speed = SPEED_RIGHT_DEFATUL + progressValue;
                txtSeekBarRigth.setText("DE: " + speed );

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                txtSeekBarRigth.setText("DE: ");
                comunicationService.write("R-"+speed);

            }
        });

    }


}
