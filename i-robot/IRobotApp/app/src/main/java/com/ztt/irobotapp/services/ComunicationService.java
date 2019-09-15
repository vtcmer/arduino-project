package com.ztt.irobotapp.services;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ComunicationService {

    private ComunicationView comunicationView;
    private Handler bluetoothIn;
    private ConnectedThread connectedThread;

    final int handlerState = 0;                         //used to identify handler message

    public ComunicationService (ComunicationView view){
        this.comunicationView = view;
        this.handleComunication();
    }

    /**
     * Inicia la conexión con el dispositivo remoto
     * @param socket
     */
    public void connect(BluetoothSocket socket){
        connectedThread  = new ConnectedThread(socket);
        connectedThread.start();
    }

    public void write(String message){
        connectedThread.write(message);
    }


    private void handleComunication(){
        bluetoothIn = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {
                    comunicationView.message(msg);
                }
            }
        };
    }


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
                comunicationView.finishProcess();

            }
        }




    }
}
