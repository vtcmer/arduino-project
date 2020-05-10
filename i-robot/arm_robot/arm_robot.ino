#include "MotorDefault.h"
#include "MotorGripper.h"
#include <Servo.h>
#include <SoftwareSerial.h>


// -- Configuración BLE

SoftwareSerial EEBlue(10, 11); // RX | TX


MotorDefault sBase(8,90,0,180,60);
MotorDefault sUpDown(7,90,0,140,25);
MotorDefault sFrontBack(9,90,30,160,25);

MotorGripper sGripper(6,90,180);



String currentAction = "0"; // 0 stop movimientos

/**
 * Lectura del comando enviado desde el BLE
 */
void readCommand(){

  if (EEBlue.available() > 0){
    String input = EEBlue.readString();
    currentAction = input;
    Serial.print("-----CHANGE ACTION-------");
    Serial.println(currentAction);
    
  }

  //Serial.print("Action:");
  //Serial.println(currentAction);
  executeCommand(currentAction);
  
}

/**
 * Ejecutar comando
 */
void executeCommand(String action){

  if (action == "O"){
    // --Open
    Serial.println("open");
    sGripper.open();
    currentAction = "S";
  } else if (action == "E"){
    // -- Cerrar
    Serial.println("close");
    sGripper.close();
    currentAction = "S";
  } else if (action == "F"){
    // -- Front Movements
    sFrontBack.goMax();
  }else if (action == "B"){
    // -- Back Movement
    sFrontBack.goMin();
  }else if (action == "L"){
    // -- Left Movement
    sBase.goMax();    
  }else if (action == "R"){
    // -- Right Movement
     sBase.goMin();
  }else if (action == "U"){
    // -- UP Movement
    sUpDown.goMax();
    
  }else if (action == "D"){
    // -- Down Movement
    sUpDown.goMin();
  }else if (action == "C"){
    // -- Stop Movement
    sBase.goCenter();
    sUpDown.goCenter();
    sFrontBack.goCenter();
    //sGripper.close();
    //sGripper.goCenter();
  }
  
  
}

void setup() {

  Serial.begin(9600);

  // -- Inicialización del BLE
  EEBlue.begin(9600); 
  EEBlue.setTimeout(50);
  

  sBase.init();
  sUpDown.init();
  sFrontBack.init();
  sGripper.init();
  
  // Esperamos 1 segundo
  delay(1000);


}

void loop() {

  readCommand();
  
 
}

  
