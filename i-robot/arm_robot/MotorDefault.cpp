#include "Arduino.h"
#include "MotorDefault.h"

MotorDefault::MotorDefault(int pin, int center, int min, int max, int tDelay){
    _pin = pin;
    _centerPosition = center;
    _minPosition = min;
    _maxPosition = max;  
    _tDelay = tDelay; 
}
void MotorDefault::init(){
    _servo.attach(_pin);
    //_servo.write(_maxPosition);
    //_currentPosition = _servo.read();
    updateCurrentPosition();
    Serial.print("Init position:");
    Serial.println(_currentPosition) ;
    //goCenter();
   
}

void MotorDefault::goMax(){

    if (_currentPosition < _maxPosition){
        if ((_currentPosition + 1) <= _maxPosition){
           _servo.write(++_currentPosition);     
        } 
    }
    Serial.print("Current position:");
    Serial.println(_servo.read());
    //updateCurrentPosition();
    delay(_tDelay); 
} 

void MotorDefault::goMin(){

    if (_currentPosition > _minPosition){
        if ((_currentPosition - 1) >= _minPosition){
           _servo.write(--_currentPosition);     
        } 
    }
    Serial.print("Current position:");
    Serial.println(_servo.read());
    //updateCurrentPosition();
    delay(_tDelay); 
}

void MotorDefault::goCenter(){
    if (_currentPosition != _centerPosition){
        if (_currentPosition > _centerPosition){
            for (int i = _currentPosition; i >= _centerPosition; i--){
                _currentPosition = i;
                _servo.write(_currentPosition);
                delay(_tDelay);
            }
        } else {
            for (int i = _currentPosition; i <= _centerPosition; i++){
                 _currentPosition = i;
                _servo.write(_currentPosition);
                delay(_tDelay); 
            }
        }
        _servo.write(_centerPosition);
    }
 
} 

void MotorDefault::updateCurrentPosition(){
    _currentPosition = _servo.read();
}


