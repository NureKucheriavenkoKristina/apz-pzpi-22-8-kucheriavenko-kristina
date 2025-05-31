#include <WiFi.h>
#include <PubSubClient.h>
#include "DHTesp.h"
#include <NTPClient.h>

#define WIFI_SSID "Wokwi-GUEST"
#define WIFI_PASSWORD ""
#define WIFI_CHANNEL 6

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 2, 60000);

const char* MQTT_SERVER = "broker.emqx.io"; 
const int MQTT_PORT = 1883;
const char* MQTT_USER = "";
const char* MQTT_PASSWORD = "";

const int DHT_PIN = 15;
const int POT_PIN = 34;
const int LED1 = 26;
const int LED2 = 27;
const int materialID = 1;

DHTesp dhtSensor;
WiFiClient espClient;
PubSubClient client(espClient);

void connectToWiFi() {
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD, WIFI_CHANNEL);
  Serial.print("Connecting to WiFi ");
  while (WiFi.status() != WL_CONNECTED) {
    delay(100);
    Serial.print(".");
  }
  Serial.println(" Connected!");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void connectToMQTT() {
  while (!client.connected()) {
    Serial.print("Connecting to MQTT broker...");
    if (client.connect("ESP32Client", MQTT_USER, MQTT_PASSWORD)) {
      Serial.println(" Connected!");
    } else {
      Serial.print(" Failed, rc=");
      Serial.print(client.state());
      Serial.println(" Trying again in 5 seconds...");
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

  Serial.println("Temp: " + temperature + "°C");
  Serial.println("Humidity: " + humidity + "%");
  Serial.println("Oxygen Level: " + String(oxygenLevel, 1) + "%");
  Serial.println("Time: " + timestamp);
  Serial.println("---");

  String jsonData = "{";
  jsonData += "\"temperature\": " + temperature + ", ";
  jsonData += "\"humidity\": " + humidity + ", ";
  jsonData += "\"measurementTime\": \"" + timestamp + "\", ";
  jsonData += "\"materialID\": { \"materialID\": " + String(materialID) + " }, ";
  jsonData += "\"oxygenLevel\": " + String(oxygenLevel, 1);
  jsonData += "}";

  Serial.println("JSON Data: " + jsonData);

  if (client.publish("storage-conditions", jsonData.c_str(), true)) {
    Serial.println("Data published successfully with retain");
  } else {
    Serial.println("Failed to publish data");
  }

  delay(5000);
}
