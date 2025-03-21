#include <WiFi.h>
#include <WebServer.h>

// Dane logowania do WiFi
const char* ssid = "2.4G-vnet-404C76";
const char* password = "24767D404C78";

// Serwer HTTP
WebServer server(80);
const int ledPin = 2;  // Pin LED
bool blinkLed = false; // Flaga kontrolująca miganie

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Łączenie z WiFi...");
  }

  Serial.println("Połączono z WiFi!");
  Serial.print("Adres IP: ");
  Serial.println(WiFi.localIP());

  pinMode(ledPin, OUTPUT);

  // Endpoint do włączania diody
  server.on("/turn_on", []() {
    digitalWrite(ledPin, HIGH);
    blinkLed = false;  // Wyłącz miganie
    server.send(200, "text/plain", "Dioda wlaczona");
  });

  // Endpoint do wyłączania diody
  server.on("/turn_off", []() {
    digitalWrite(ledPin, LOW);
    blinkLed = false;  // Wyłącz miganie
    server.send(200, "text/plain", "Dioda wylaczona");
  });

  // Endpoint do migania diodą w pętli
  server.on("/blink", []() {
    blinkLed = true;  // Ustaw miganie
    server.send(200, "text/plain", "Dioda miga");
  });

  server.begin();
}

void loop() {
  server.handleClient();  // Obsługa żądań HTTP

  if (blinkLed) {
    digitalWrite(ledPin, HIGH);
    delay(500);
    digitalWrite(ledPin, LOW);
    delay(500);
  }
}