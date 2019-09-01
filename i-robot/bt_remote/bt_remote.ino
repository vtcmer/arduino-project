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
#include <SoftwareSerial.h>

SoftwareSerial EEBlue(2, 3); // RX | TX


/////////////////////////////////////////
// Configuración del Servo//////////////
///////////////////////////////////////
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

int SPEED = 200;

// -- Varibles de lógica
bool automatic = false; 
// -- Indica si el movimiento es en automático con manual

long minLimit = 30;

bool showLog = false;

char direction = 'S';
char lastDirection = 'S';

void setup() {

  
  servo.attach(9);  
  servo.write(60);
   

  Serial.begin(9600);
  EEBlue.begin(9600); 

  // -- Configuración del motor
  pinMode(ENG1, OUTPUT);
  pinMode(ENG2, OUTPUT);
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);
  
}

/**
 * Lectura de comendos que vienen de android
 */
void readCommand(){

  if (EEBlue.available() > 0){
    char input = EEBlue.read();
    
    if (input == '1'){
      // -- Cambio a Manual
      direction = 'S';
      lastDirection = 'S';
      automatic = false;
      moveStop();
      centerPosition();
      delay(500);
    Serial.println("Cambio Manual");
    } else if (input == '2'){
      direction = 'S';
      lastDirection = 'S';
      automatic = true;
      moveStop();
      centerPosition();
      delay(500);
      Serial.println("Cambio Automático");
    } else {
      direction = input;
      Serial.print("Dirección");
      Serial.println(direction);
       
    }
    
  } 
  
}

/**
 * Enviar datos a la aplicación android
 */
void sendData(){

  if (direction != lastDirection){
    EEBlue.print(direction);
    EEBlue.println();
    lastDirection = direction;
  }
  
}

void loop() {

  readCommand();
 
  if (automatic){
    handleAutomatic();
  } else {
    handleManual();
  }
 

  
  

}




/**
 * Movimiento manual del robot
 */
void handleAutomatic(){
  bool isForward = checkForward();
  if (isForward){
    Serial.println("de frente");
    direction = 'F';
    sendData();
    moveForward();
  } else {
     moveStop();
     direction = 'B';
     sendData();
     int position = 4;
     while (position == 4){
        moveBack();
        delay(350);
        moveStop();
        position = findDireccion();
     }  

     if (position == 2){
        direction = 'R';
        sendData();
        turnRight(500);    
     } else if (position == 3){
        direction = 'L';
        sendData();
        turnLeft(500);
     }
  }
  
}

/**
 * Búsqueda de la dirección
 * Valores devueltos:
 *  2 - Derecha ->
 *  3 - Izquierda  <-
 *  4 - Atrás
 */
int findDireccion(){


  int result = 4;

  bool isRight = checkRight();
  long dRight = getDistancia();
    
  bool isLeft = checkLeft();
  long dLeft = getDistancia();

  if (isRight && isLeft){
    long diff = dRight - dLeft;
    if (diff >= 0){
      result = 2;
    } else {
      result = 3;
    }
    
  } else if (isRight && !isLeft){
    result = 2;
  } else if (!isRight && isLeft){
    result = 3;
  } 

  centerPosition();  

  return result;
  
}

/**
 * Pone el sensor ultrasonico en la posición central
 */
void centerPosition(){
  servo.write(60);  // centro   
}

/**
 * Comprueba si existen obstaculos cercanos delante
 */
boolean checkForward(){
  return !isLimit();
}

/**
 * Comprueba si existen obstáculos ceranos a la derecha
 */
boolean checkRight(){
  servo.write(0);
  return !isLimit();
}

/**
 * Comprueba si existen obstaculos cercanos a la izquierda
 */
boolean checkLeft(){
  servo.write(130);  // mirar a la izquierda
  return !isLimit();
}

/**
 * Devuelve la distancia más al objeto más cercano identificado
 */
long getDistancia(){
  long distancia = sr04.Distance();
  delay(350);
  if (showLog){
    Serial.print("Distancia: ");
    Serial.print(distancia);
  }
  return distancia;
}


/**
 * Verifica si está en el límite
 */
bool isLimit(){
  bool result = false;
  long d = 0;
 
  do {
    d = getDistancia();
  } while (d >= 2000);

   if (d <= minLimit){
    result = true;
  }

  return result;
}



/**
 * Manejo manual del robot
 */
void handleManual(){

  // RemoteXY_Handler ();
  movementManual();
  
}

/**
 * Movimiento manual del robot
 */
void movementManual(){
  if (direction == 'S'){
    moveStop();
    //findDireccion();
  } else if (direction == 'F'){
    moveForward();
  } else if (direction == 'B'){
    moveBack();
  } else if (direction == 'L'){
    turnLeft(500);
    direction = 'S';
  } else if (direction == 'R'){
    turnRight(500);
    direction = 'S';
  } else {
    moveStop();
  }

}



/**
 * Movimiento hacia adelante
 */
void moveForward(){

  moveWheelLeftForward(SPEED);
  moveWheelRightForward((SPEED + 50));
  
}

/**
 * Movimiento hacia atrás
 */
void moveBack(){
  moveWheelLeftBack(SPEED);
  moveWheelRightBack((SPEED + 50));
}

void turnLeft(long time){
  moveWheelRightForward(SPEED);
  moveWheelLeftBack(SPEED);
  delay(time);
}

void turnRight(long time){
  moveWheelLeftForward(SPEED);
  moveWheelRightBack(SPEED);
  delay(time);
}


void moveStop(){
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  analogWrite(ENG1, 0);

  
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
  analogWrite(ENG2, 0);
}



/**
 * Mover la rueda izquierda hacia adelante
 */
void moveWheelLeftForward(int speed){

  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
  analogWrite(ENG2, speed);
}

/**
 * Mover la rueda derecha hacia adelante
 */
void moveWheelRightForward(int speed){

  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  analogWrite(ENG1, speed);
}


/**
 * Mover la rueda izquierda hacia adelante
 */
void moveWheelLeftBack(int speed){

  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);
  analogWrite(ENG2, speed);
}

/**
 * Mover la rueda derecha hacia adelante
 */
void moveWheelRightBack(int speed){

  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  analogWrite(ENG1, speed);
}
