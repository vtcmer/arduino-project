#include "Arduino.h"
#include "MotorGripper.h"

MotorGripper::MotorGripper(int pin, int min, int max){
    _pin = pin;
    _minPosition = min;
    _maxPosition = max;   
}
void MotorGripper::init(){
    _servo.attach(_pin);
    //_currentPosition = _servo.read();
   // Serial.print("Init position:");
   // Serial.println(_currentPosition) ;
    /*
    for (int i = _currentPosition; i >= _minPosition; i--){
        _servo.write(i--);
        delay(25);
    }*/
    _servo.write(90);   
    //_currentPosition = _minPosition; 
   
}

void MotorGripper::open(){
    for (int i = _currentPosition; i <= _maxPosition; i++){
        _currentPosition = i;
        _servo.write(_currentPosition);
        delay(15); 
    }
    //_servo.write(_maxPosition);    
} 

void MotorGripper::close(){
    for (int i = _currentPosition; i >= _minPosition; i--){
        _currentPosition = i;
        _servo.write(_currentPosition);
        delay(15);
    }
    //_servo.write(_minPosition);        
}   
