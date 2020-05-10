#ifndef MotorDefault_h
#define MotorDefault_h
#include "Arduino.h"
#include <Servo.h>

class MotorDefault{

    public:
        MotorDefault(int pin, int center, int min, int max, int tDelay);
        void init();
        void goMax();
        void goMin();
        void goCenter();
        void updateCurrentPosition();
    private:
        int _pin; // Pin al que se conecta el servo
        Servo _servo; // -- Servo motor
        int _currentPosition; // -- Posición Actual
        int _centerPosition; // -- Posición centro
        int _minPosition; // -- Posición minima
        int _maxPosition; // -- Posición máxima
        int _tDelay;
};



#endif
