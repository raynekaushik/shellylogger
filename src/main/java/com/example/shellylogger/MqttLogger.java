package com.example.shellylogger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MqttLogger implements CommandLineRunner {
    private final ApplicationEventPublisher eventPublisher;
    public MqttLogger(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting MqttLogger");
        String broker = "tcp://localhost:1883";
        String clientID = "shellylogger";
        String topic = "test";
        MqttClient client = new MqttClient(broker, clientID);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {}
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            String payload = new String(mqttMessage.getPayload());
            System.out.println(payload);
                eventPublisher.publishEvent(payload);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}

        });
                client.connect();
        client.subscribe(topic);

    }
}