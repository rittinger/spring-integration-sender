package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.messaging.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {
	@Autowired
        private PubsubOutboundGateway messagingGateway;

        public static void main(String[] args) {
                SpringApplication.run(DemoApplication.class, args);
        }
        @PostMapping("/postMessage")
        public RedirectView postMessage(@RequestParam("message") String message) {
                this.messagingGateway.sendToPubsub(message);
                return new RedirectView("/");
        }
        @MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
        public interface PubsubOutboundGateway {
                void sendToPubsub(String text);
        }
        @Bean
        @ServiceActivator(inputChannel = "pubsubOutputChannel")
        public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
                return new PubSubMessageHandler(pubsubTemplate, "event-stream");
        }
}
