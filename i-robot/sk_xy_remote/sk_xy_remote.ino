/*
   -- New project --
   
   This source code of graphical user interface 
   has been generated automatically by RemoteXY editor.
   To compile this code using RemoteXY library 2.3.5 or later version 
   download by link http://remotexy.com/en/library/
   To connect using RemoteXY mobile app by link http://remotexy.com/en/download/                   
     - for ANDROID 4.3.1 or later version;
     - for iOS 1.3.5 or later version;
    
   This source code is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.    
*/



#include <Servo.h>
#include "SR04.h"


//////////////////////////////////////////////
//        RemoteXY include library          //
//////////////////////////////////////////////

// RemoteXY select connection mode and include library 
#define REMOTEXY_MODE__SOFTSERIAL
#include <SoftwareSerial.h>

#include <RemoteXY.h>

// RemoteXY connection settings 
#define REMOTEXY_SERIAL_RX 2
#define REMOTEXY_SERIAL_TX 3
#define REMOTEXY_SERIAL_SPEED 9600


// RemoteXY configurate  
#pragma pack(push, 1)
uint8_t RemoteXY_CONF[] =
  { 255,1,0,0,0,11,0,8,13,0,
  3,133,17,26,63,14,2,26 };
  
// this structure defines all the variables of your control interface 
struct {

    // input variable
  uint8_t direction; // =0 if select position A, =1 if position B, =2 if position C, ... 

    // other variable
  uint8_t connect_flag;  // =1 if wire connected, else =0 

} RemoteXY;
#pragma pack(pop)

/////////////////////////////////////////////
//           END RemoteXY include          //
/////////////////////////////////////////////



#define TRIG_PIN 8
#define ECHO_PIN 7
SR04 sr04 = SR04(ECHO_PIN,TRIG_PIN);
Servo servo;  // create servo object to control a servo

// -- Variables motores
int ENG1 = 6;
int IN1 = 13;
int IN2 = 12;

int ENG2 =5;
int IN3 =11;
int IN4 =10;

int SPEED = 205;

// -- Varibles de lógica
bool automatic = true; // -- Se de forma autónoma
bool checkDirection = true; // -- Indica que tiene que checkear la dirección en la que se mueve
bool isStopped = true; // -- Valor boolean que indica que está parado
long minLimit = 30;

bool showLog = false;

void setup() 
{
  RemoteXY_Init (); 
  
  
  // TODO you setup code

  servo.attach(9);  
  servo.write(60);

  // -- Configuración del motor
  pinMode(ENG1, OUTPUT);
  pinMode(ENG2, OUTPUT);
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);
  
}

void loop() 
{ 
  RemoteXY_Handler ();
  
  
  // TODO you loop code
  // use the RemoteXY structure for data transfer
  if (RemoteXY.direction == 0){
    
  } else if (RemoteXY.direction == 1){
    straightOn();
  } else if (RemoteXY.direction == 2){
    moveStop();
  } else if (RemoteXY.direction == 3){

  } else if (RemoteXY.direction == 4){

  } else {
    
  }


}



/**
 * Movimiento de frente
 */
void straightOn(){

  moveWheelLeftForward(SPEED);
  moveWheelRightForward((SPEED + 45));
  
}


void moveStop(){
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  analogWrite(ENG1, 0);

  
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
  analogWrite(ENG2, 0);
}

void moveWheelLeftForward(int speed){

  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
  analogWrite(ENG2, speed);
  
}


void moveWheelRightForward(int speed){

  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  analogWrite(ENG1, speed);

  
  
}
