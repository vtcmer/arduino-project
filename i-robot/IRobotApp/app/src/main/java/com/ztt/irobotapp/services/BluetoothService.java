package com.ztt.irobotapp.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BluetoothService {


    private BluetoothAdapter mBtAdapter;
    private BluetoothSocket btSocket = null;


    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public BluetoothService(){

        mBtAdapter=BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null){
            throw new RuntimeException("No se ha podido crear una instancia BluetoothAdapter");
        }

    }



    /**
     * Listado de dispositivos disponibles
     * @return
     */
    public List<BluetoothDevice> getDevicesAvailables(){
        List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>( mBtAdapter.getBondedDevices());
        return devices;
    }

    /**
     * Recuperación de un dispositivo bluetooth
     * @param address
     * @return
     */
    public BluetoothDevice getDevice(String address){
        return mBtAdapter.getRemoteDevice(address);

    }

    /**
     * Creando la conexión con el dispositivo
     * @param address
     * @throws ConnectException
     */
    public BluetoothSocket createConnection(String address) throws ConnectException {
        BluetoothDevice device = this.getDevice(address);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        } catch (IOException e) {
            throw new ConnectException("Error createRfcommSocketToServiceRecord "+device.getName());
        }

        try {
            btSocket.connect();
        } catch (IOException e) {
            if (btSocket != null){
                try {
                    btSocket.close();
                } catch (IOException e1) {
                    throw new ConnectException("Error cerrando la conexión con el dispositivo"+device.getName());
                }
            }
            throw new ConnectException("Error conectando al dispositivo "+device.getName());
        }

        return btSocket;

    }

    /**
     * Cerrando la conexión abierta
     * @throws ConnectException
     */
    public void closeConnection() throws ConnectException {
        if (btSocket != null){
            try {
                btSocket.close();
            } catch (IOException e1) {
                throw new ConnectException("Error cerrando la conexión con el dispositivo");
            }
        }
    }


    /**
     * Verifica si está acitvo el bluetooth
     * @return
     */
    public Boolean isEnabled(){

        boolean isOk = false;

        if (mBtAdapter.isEnabled()) {
            isOk = true;
        }

        return isOk;

    }



}
