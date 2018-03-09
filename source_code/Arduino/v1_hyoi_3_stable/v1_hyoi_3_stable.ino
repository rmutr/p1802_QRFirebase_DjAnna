#include <Wire.h> 
#include <LiquidCrystal_I2C.h>

#include <DueFlashStorage.h>
DueFlashStorage dueFlashStorage;

#define LCD_LINE_0           0
#define LCD_LINE_1           1

#define PNP_ON               1
#define PNP_OFF              0
#define NPN_ON               0
#define NPN_OFF              1

#define PIN_SERIAL_0_RX      0
#define PIN_SERIAL_0_TX      1
#define PIN_SERIAL_1_RX     19
#define PIN_SERIAL_1_TX     18
#define PIN_SERIAL_2_RX     17
#define PIN_SERIAL_2_TX     16

#define PIN_DI_BC_PROX       3
#define PIN_DO_BC_TRIG       4
#define PIN_DO_TYPE_A        5
#define PIN_DO_TYPE_B        6
#define PIN_DO_TYPE_C        7
#define PIN_DI_PB_MODE       8
#define PIN_DI_PB_SET        9

LiquidCrystal_I2C lcd(0x3F, 16, 2);

#define TX0BUFS       50
#define BC_LEN        20

char tx0buf[TX0BUFS] = {0};

int  rx1found = 0;
int  rx1ix = 0;
char rx1buf[BC_LEN] = {0};

int  rx2found = 0;
int  rx2ix = 0;
char rx2buf[BC_LEN] = {0};

int bcnix = 0;
char bcnbuf[BC_LEN] = {0};

int  bc1ix = 0;
char bc1buf[BC_LEN] = {0};

int  bc2ix = 0;
char bc2buf[BC_LEN] = {0};

int bc3ix = 0;
char bc3buf[BC_LEN] = {0};

int px_val        = 0;
int px_val_old    = 0;
int pb_val        = 0;
int pb_val_old    = 0;
int disp_mode     = 0;
int disp_mode_old = 0;
int count_a       = 0;
int count_b       = 0;
int count_c       = 0;
int count_t       = 0;

void setup()
{

  bc1ix = dueFlashStorage.read(150);
  for (int bix = 0; bix < BC_LEN; bix++) {
    bc1buf[bix] = dueFlashStorage.read(100 + bix);
  }

  bc2ix = dueFlashStorage.read(250);
  for (int bix = 0; bix < BC_LEN; bix++) {
    bc2buf[bix] = dueFlashStorage.read(200 + bix);
  }

  bc3ix = dueFlashStorage.read(350);
  for (int bix = 0; bix < BC_LEN; bix++) {
    bc3buf[bix] = dueFlashStorage.read(300 + bix);
  }

  pinMode(PIN_DI_BC_PROX, INPUT_PULLUP);
  pinMode(PIN_DO_BC_TRIG, OUTPUT);
  pinMode(PIN_DO_TYPE_A,  OUTPUT);
  pinMode(PIN_DO_TYPE_B,  OUTPUT);
  pinMode(PIN_DO_TYPE_C,  OUTPUT);
  pinMode(PIN_DI_PB_MODE, INPUT_PULLUP);
  pinMode(PIN_DI_PB_SET,  INPUT_PULLUP);

  digitalWrite(PIN_DO_BC_TRIG, PNP_OFF);
  digitalWrite(PIN_DO_TYPE_A,  PNP_OFF);
  digitalWrite(PIN_DO_TYPE_B,  PNP_OFF);
  digitalWrite(PIN_DO_TYPE_C,  PNP_OFF);

  Serial.begin(115200, SERIAL_8N1); // 0 Serial Monitor
  Serial1.begin( 9600, SERIAL_7E1); // 1 Barcode Reader
  Serial2.begin( 9600, SERIAL_7E1); // 2 Mobile Phone

  Serial.println("\n\nInE Barcode System Start...     ");
  Serial.println("-------------------------------");
  sprintf(tx0buf, "Barcode Type A : [%02d] : ", bc1ix);
  Serial.print(tx0buf); Serial.println(bc1buf);
  sprintf(tx0buf, "Barcode Type B : [%02d] : ", bc2ix);
  Serial.print(tx0buf); Serial.println(bc2buf);
  sprintf(tx0buf, "Barcode Type C : [%02d] : ", bc3ix);
  Serial.print(tx0buf); Serial.println(bc3buf);
  Serial.println();
  
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, LCD_LINE_0);
  lcd.print("A = 000  B = 000");
  lcd.setCursor(0, LCD_LINE_1);
  lcd.print("C = 000  T = 000");
}

void loop()
{
  rx1found = 0;
  rx2found = 0;

  Update_Bc_Prox();
  Update_Pb();
  Update_Bc_Serial();
  Update_Mobile_Serial();

  if (px_val_old != px_val) {
    px_val_old = px_val;

    if (px_val == 0) {
      Serial.println("<-- Prox. = OFF");
    } else {
      Serial.println("<-- Prox. = ON");
      Serial.println("--> Send request scanning to mobile.");
      Serial2.print("-rr");
    }
  }

  if (pb_val_old != pb_val) {
    pb_val_old = pb_val;

    if (pb_val == 1) {
      
      if (disp_mode < 3) {
        disp_mode++;
      } else {
        disp_mode = 0;
      }

      sprintf(tx0buf, "--> Change mode to [ Mode-%d ]", disp_mode);
      Serial.println(tx0buf);
      delay(20);
    }

    if (pb_val == 2) {
      
      if (bcnix > 0) {

        switch (disp_mode) {
          default:
          case 0:
            break;
  
          case 1:
            bc1ix = bcnix;
            for (int bix = 0; bix < BC_LEN; bix++) {
              bc1buf[bix] = bcnbuf[bix];
            }
            sprintf(tx0buf, "--> Setup Barcode Type A : [%02d] : ", bc1ix);
            Serial.print(tx0buf); Serial.println(bc1buf);
            disp_mode = 0;

            dueFlashStorage.write(150, bc1ix);
            for (int bix = 0; bix < BC_LEN; bix++) {
              dueFlashStorage.write(100 + bix, bc1buf[bix]);
            }

            break;
  
          case 2:
            bc2ix = bcnix;
            for (int bix = 0; bix < BC_LEN; bix++) {
              bc2buf[bix] = bcnbuf[bix];
            }
            sprintf(tx0buf, "--> Setup Barcode Type B : [%02d] : ", bc2ix);
            Serial.print(tx0buf); Serial.println(bc2buf);
            disp_mode = 0;

            dueFlashStorage.write(250, bc2ix);
            for (int bix = 0; bix < BC_LEN; bix++) {
              dueFlashStorage.write(200 + bix, bc2buf[bix]);
            }

            break;
  
          case 3:
            bc3ix = bcnix;
            for (int bix = 0; bix < BC_LEN; bix++) {
              bc3buf[bix] = bcnbuf[bix];
            }
            sprintf(tx0buf, "--> Setup Barcode Type C : [%02d] : ", bc3ix);
            Serial.print(tx0buf); Serial.println(bc3buf);
            disp_mode = 0;

            dueFlashStorage.write(350, bc3ix);
            for (int bix = 0; bix < BC_LEN; bix++) {
              dueFlashStorage.write(300 + bix, bc3buf[bix]);
            }

            break;
        }
        
      }

      Serial.println("-------------------------------");
      sprintf(tx0buf, "Barcode Type A : [%02d] : ", bc1ix);
      Serial.print(tx0buf); Serial.println(bc1buf);
      sprintf(tx0buf, "Barcode Type B : [%02d] : ", bc2ix);
      Serial.print(tx0buf); Serial.println(bc2buf);
      sprintf(tx0buf, "Barcode Type C : [%02d] : ", bc3ix);
      Serial.print(tx0buf); Serial.println(bc3buf);
      Serial.println();
      delay(100);
    }

    bcnix = 0; for (int bix = 0; bix < BC_LEN; bix++) { bcnbuf[bix] = 0; }

    if (pb_val == 3) {
      Serial.println("--> Clear all setting memory.");

      disp_mode = -1;
      count_a = 0;
      count_b = 0;
      count_c = 0;
      count_t = 0;

      bc1ix = 0; bc2ix = 0; bc3ix = 0;
      for (int bix = 0; bix < 100; bix++) {
        bc1buf[bix] = 0; dueFlashStorage.write(100 + bix, 0);
        bc2buf[bix] = 0; dueFlashStorage.write(200 + bix, 0);
        bc3buf[bix] = 0; dueFlashStorage.write(300 + bix, 0);
      }
      
      Serial.println("--> Clear all setting memory successed.");
    }
  }

  if (disp_mode_old != disp_mode) {
    disp_mode_old = disp_mode;

    switch (disp_mode) {
      default:
      case 0:
        lcd.clear();
        lcd.setCursor(0, LCD_LINE_0);
        sprintf(tx0buf, "A = %03d  B = %03d", count_a, count_b);
        lcd.print(tx0buf);
        lcd.setCursor(0, LCD_LINE_1);
        sprintf(tx0buf, "C = %03d  T = %03d", count_c, count_t);
        lcd.print(tx0buf);
        delay(100);
        break;
  
      case 1:
        lcd.clear();
        lcd.setCursor(0, LCD_LINE_0);
        lcd.print("Barcode Type A");
        lcd.setCursor(0, LCD_LINE_1);
        lcd.print(bc1buf);
        delay(100);
        break;
  
      case 2:
        lcd.clear();
        lcd.setCursor(0, LCD_LINE_0);
        lcd.print("Barcode Type B");
        lcd.setCursor(0, LCD_LINE_1);
        lcd.print(bc2buf);
        delay(100);
        break;
  
      case 3:
        lcd.clear();
        lcd.setCursor(0, LCD_LINE_0);
        lcd.print("Barcode Type C");
        lcd.setCursor(0, LCD_LINE_1);
        lcd.print(bc3buf);
        delay(100);
        break;
    }
  }

  switch (disp_mode) {
    default:
      disp_mode = 0;
    case 0:
      if (rx1found == 1) {
        lcd.clear();
        lcd.print("Barcode form Ch1");
        lcd.setCursor(0, LCD_LINE_1);
        lcd.print(rx1buf);

        {
          int berror = 0;
      
          berror = 0;
          if (bc1ix == 0) { berror++; }
          if (rx1ix != bc1ix) { berror++; }
          for (int bix = 0; bix < rx1ix; bix++) {
            if (rx1buf[bix] != bc1buf[bix]) { berror++; }
          }
          if (berror == 0) {
            Serial.println("--> Found Barcode Type A");
            digitalWrite(PIN_DO_TYPE_A, PNP_ON);
            count_a++; count_t++;
            sprintf(tx0buf, "--> Quantity of Type A = %03d Total = %03d", count_a, count_t);
            Serial.println(tx0buf);
          }
      
          berror = 0;
          if (bc2ix == 0) { berror++; }
          if (rx1ix != bc2ix) { berror++; }
          for (int bix = 0; bix < rx1ix; bix++) {
            if (rx1buf[bix] != bc2buf[bix]) { berror++; }
          }
          if (berror == 0) {
            Serial.println("--> Found Barcode Type B");
            digitalWrite(PIN_DO_TYPE_B, PNP_ON);
            count_b++; count_t++;
            sprintf(tx0buf, "--> Quantity of Type B = %03d Total = %03d", count_b, count_t);
            Serial.println(tx0buf);
          }

          berror = 0;
          if (bc3ix == 0) { berror++; }
          if (rx1ix != bc3ix) { berror++; }
          for (int bix = 0; bix < rx1ix; bix++) {
            if (rx1buf[bix] != bc3buf[bix]) { berror++; }
          }
          if (berror == 0) {
            Serial.println("--> Found Barcode Type C");
            digitalWrite(PIN_DO_TYPE_C, PNP_ON);
            count_c++; count_t++;
            sprintf(tx0buf, "--> Quantity of Type C = %03d Total = %03d", count_c, count_t);
            Serial.println(tx0buf);
          }
        }
        delay(1000);
      }

      if (rx2found == 1) {
        lcd.clear();
        lcd.print("Barcode form Ch2");
        lcd.setCursor(0, LCD_LINE_1);
        lcd.print(rx2buf);

        {
          int berror = 0;
          
          berror = 0;
          if (bc1ix == 0) { berror++; }
          if (rx2ix != bc1ix) { berror++; }
          for (int bix = 0; bix < rx2ix; bix++) {
            if (rx2buf[bix] != bc1buf[bix]) { berror++; }
          }
          if (berror == 0) {
            Serial.println("--> Found Barcode Type A");
            digitalWrite(PIN_DO_TYPE_A, PNP_ON);
            count_a++; count_t++;
            sprintf(tx0buf, "--> Quantity of Type A = %03d Total = %03d", count_a, count_t);
            Serial.println(tx0buf);
          }
      
          berror = 0;
          if (bc2ix == 0) { berror++; }
          if (rx2ix != bc2ix) { berror++; }
          for (int bix = 0; bix < rx2ix; bix++) {
            if (rx2buf[bix] != bc2buf[bix]) { berror++; }
          }
          if (berror == 0) {
            Serial.println("--> Found Barcode Type B");
            digitalWrite(PIN_DO_TYPE_B, PNP_ON);
            count_b++; count_t++;
            sprintf(tx0buf, "--> Quantity of Type A = %03d Total = %03d", count_b, count_t);
            Serial.println(tx0buf);
          }
      
          berror = 0;
          if (bc3ix == 0) { berror++; }
          if (rx2ix != bc3ix) { berror++; }
          for (int bix = 0; bix < rx2ix; bix++) {
            if (rx2buf[bix] != bc3buf[bix]) { berror++; }
          }
          if (berror == 0) {
            Serial.println("--> Found Barcode Type C");
            digitalWrite(PIN_DO_TYPE_C, PNP_ON);
            count_c++; count_t++;
            sprintf(tx0buf, "--> Quantity of Type A = %03d Total = %03d", count_c, count_t);
            Serial.println(tx0buf);
          }
        }
        delay(1000);
      }
    
      if ((rx1found == 1) || (rx2found == 1)) {
        lcd.clear();
        lcd.setCursor(0, LCD_LINE_0);
        sprintf(tx0buf, "A = %03d  B = %03d", count_a, count_b);
        lcd.print(tx0buf);
        lcd.setCursor(0, LCD_LINE_1);
        sprintf(tx0buf, "C = %03d  T = %03d", count_c, count_t);
        lcd.print(tx0buf);
      }
      break;

    case 1:
    case 2:
    case 3:
      if (rx1found == 1) {
        bcnix = rx1ix;
        for (int bix = 0; bix < BC_LEN; bix++) {
          bcnbuf[bix] = rx1buf[bix];
        }
      }
      
      if (rx2found == 1) {
        bcnix = rx2ix;
        for (int bix = 0; bix < BC_LEN; bix++) { 
          bcnbuf[bix] = rx2buf[bix];
        }
      }

      if ((rx1found == 1) || (rx2found == 1)) {
        lcd.setCursor(0, LCD_LINE_1);
        lcd.print("                ");
        lcd.setCursor(0, LCD_LINE_1);
        lcd.print(bcnbuf);
      }
      break;
  }

  if ((rx1found == 1) || (rx2found == 1)) {
    digitalWrite(PIN_DO_BC_TRIG, PNP_OFF);
    digitalWrite(PIN_DO_TYPE_A,  PNP_OFF);
    digitalWrite(PIN_DO_TYPE_B,  PNP_OFF);
    digitalWrite(PIN_DO_TYPE_C,  PNP_OFF);
    delay(1000);  

    for (int bix = 0; bix < BC_LEN; bix++) {
      rx1buf[bix] = 0;
      rx2buf[bix] = 0;
    }
  }

  delay(100);  
}





void Update_Bc_Prox() {
  px_val = !digitalRead(PIN_DI_BC_PROX);
  digitalWrite(PIN_DO_BC_TRIG, px_val);
}

void Update_Pb() {
  pb_val = 0;
  if (digitalRead(PIN_DI_PB_MODE) == 0) { pb_val += 1; }
  if (digitalRead(PIN_DI_PB_SET)  == 0) { pb_val += 2; }
}

void Update_Bc_Serial() {
  if (Serial1.available() > 0) {
    
    rx1ix = 0;
    while (Serial1.available() > 0) {
      char bbyte = Serial1.read();
      if ((bbyte != 10) && (bbyte != 13)) {
        if (rx1ix < BC_LEN) {
          rx1buf[rx1ix] = bbyte;
          rx1ix++;
        }
      }
    }

    delay(20);
    while (Serial1.available() > 0) {
      char bbyte = Serial1.read();
      if ((bbyte != 10) && (bbyte != 13)) {
        if (rx1ix < BC_LEN) {
          rx1buf[rx1ix] = bbyte;
          rx1ix++;
        }
      }
    }

    Serial.print("<-- TX1 : ");
    Serial.println(rx1buf);
    rx1found = 1;
  }
}

void Update_Mobile_Serial() {
  if (Serial2.available() > 0) {
    
    rx2ix = 0;
    while (Serial2.available() > 0) {
      char bbyte = Serial2.read();
      if ((bbyte != 10) && (bbyte != 13)) {
        if (rx2ix < BC_LEN) {
          rx2buf[rx2ix] = bbyte;
          rx2ix++;
        }
      }
    }

    delay(20);
    while (Serial2.available() > 0) {
      char bbyte = Serial2.read();
      if ((bbyte != 10) && (bbyte != 13)) {
        if (rx2ix < BC_LEN) {
          rx2buf[rx2ix] = bbyte;
          rx2ix++;
        }
      }
    }

    Serial.print("<-- TX2 : ");
    Serial.println(rx2buf);
    rx2found = 1;
  }
}

