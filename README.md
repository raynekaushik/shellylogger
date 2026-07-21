The Hook: Create a class that implements CommandLineRunner so Spring runs it automatically.

The Configuration: Define the broker, clientId, and topic.

The Device: Build the MqttClient.

The Plan: setCallback(new MqttCallback()) and handle the three overrides (focusing on messageArrived to unpack the bytes into a String).

The Switch: Call connect() and subscribe().
