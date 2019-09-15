package com.ztt.irobotapp.services;

import android.os.Message;

public interface ComunicationView {

    /**
     * Recibe un mensaje
     * @param msg
     */
    void message(Message msg);

    /**
     * Finaliza el proceso
     */
    void finishProcess();
}
