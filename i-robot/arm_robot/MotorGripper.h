#ifndef MotorGripper_h
#define MotorGripper_h
#include "Arduino.h"
#include <Servo.h>

class MotorGripper{

    public:
        MotorGripper(int pin, int min, int max);
        void init();
        void open();
        void close();
    private:
        int _pin; // Pin al que se conecta el servo
        Servo _servo; // -- Servo motor
        int _currentPosition;

        int _minPosition;
        int _maxPosition;
};



#endif
