#include <WiFi.h>
#include <PubSubClient.h>
#include "DHTesp.h"
#include <NTPClient.h>

#define WIFI_SSID "Wokwi-GUEST"
#define WIFI_PASSWORD ""
#define WIFI_CHANNEL 6

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 2, 60000);

char* MQTT_SERVER = "broker.emqx.io"; 
int MQTT_PORT = 1883;
char* MQTT_USER = "";
char* MQTT_PASSWORD = "";

int DHT_PIN = 15;
int POT_PIN = 34;
int LED1 = 26;
int LED2 = 27;
int materialID = 1;

DHTesp dhtSensor;
WiFiClient espClient;
PubSubClient client(espClient);

void connectToWiFi() {
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD, WIFI_CHANNEL);
  Serial.print("Під'єднання до WiFi ");
  while (WiFi.status() != WL_CONNECTED) {
    delay(100);
    Serial.print(".");
  }
  Serial.println("Підключено!");
}

void connectToMQTT() {
  while (!client.connected()) {
    Serial.print("Підключення до брокера MQTT...");
    if (client.connect("ESP32Client", MQTT_USER, MQTT_PASSWORD)) {
      Serial.println(" Підключено!");
    } else {
      Serial.print("Не вдалося, код помилки: ");
      Serial.print(client.state());
      Serial.println(" Спроба ще раз через 5 секунд...");
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  dhtSensor.setup(DHT_PIN, DHTesp::DHT22);

  connectToWiFi();

  client.setServer(MQTT_SERVER, MQTT_PORT);
  connectToMQTT();
}

void loop() {

  if (!client.connected()) {
    connectToMQTT();
  }
  client.loop();

  timeClient.update();
  
  unsigned long epochTime = timeClient.getEpochTime();

  time_t rawTime = (time_t)epochTime; 
  struct tm *timeInfo;
  timeInfo = localtime(&rawTime);

  char formattedTime[30];
  strftime(formattedTime, sizeof(formattedTime), "%Y-%m-%dT%H:%M:%S", timeInfo);


  String timestamp = String(formattedTime) + ".000+00:00";
  timestamp.replace(" ", "T");

  TempAndHumidity data = dhtSensor.getTempAndHumidity();
  String temperature = String(data.temperature, 2);
  String humidity = String(data.humidity, 1);
  int potValue = analogRead(POT_PIN);
  float oxygenLevel = map(potValue, 0, 4095, 0, 100);

  Serial.println("Температура: " + temperature + "°C");
  Serial.println("Вологість: " + humidity + "%");
  Serial.println("Рівень кисню: " + String(oxygenLevel, 1) + "%");
  Serial.println("Час: " + timestamp);
  Serial.println("---");

  String jsonData = "{";
  jsonData += "\"temperature\": " + temperature + ", ";
  jsonData += "\"humidity\": " + humidity + ", ";
  jsonData += "\"measurementTime\": \"" + timestamp + "\", ";
  jsonData += "\"materialID\": { \"materialID\": " + String(materialID) + " }, ";
  jsonData += "\"oxygenLevel\": " + String(oxygenLevel, 1);
  jsonData += "}";

  Serial.println("JSON: " + jsonData);

  if (client.publish("storage-conditions", jsonData.c_str(), true)) {
    Serial.println("Дані успішно опубліковано");
  } else {
    Serial.println("Не вдалося опублікувати дані");
  }

  delay(5000);
}
