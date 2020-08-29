package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ClientApp {

	private static final String CHANNEL = "mychannel";
	private static final String CONTRACT = "imagedb";
	private static final String KAFKA_BOOTSTRAP = "localhost:9092";
	private static final String OUTPUT_TOPIC = "output-topic";
	private static final String REQUEST_DATA_TOPIC = "request-data-topic";
	private static final String RESULT_DATA_TOPIC = "result-data-topic";
	private static final KafkaProducer<String, String> producer = new KafkaProducer<>(getProducerProperties());
	private static final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getConsumerProperties());
	
	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
		try {
			EnrollAdmin.enrollAdmin();
			RegisterUser.registerUser();
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallet.createFileSystemWallet(walletPath);
		Path networkConfigPath = Paths.get(ClassLoader.getSystemResource("connection.yaml").toURI());

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "user").networkConfig(networkConfigPath).discovery(true);

		try (Gateway gateway = builder.connect()) {
			
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("imagedb");
			consumer.subscribe(List.of("output-topic", "request-data-topic"));

			while (true) {
				final ConsumerRecords<String, String> consumerRecords =
						consumer.poll(Duration.ofMillis(1000));
				
				for (ConsumerRecord<String, String> record : consumerRecords) {
					if ("output-topic".equals(record.topic())) {
						contract.submitTransaction("addImage", String.valueOf(System.currentTimeMillis()), record.value());
					}
					
					if ("request-data-topic".equals(record.topic())) {
						final String[] bounds = record.value().split(" ");
						long startTime = Long.parseLong(bounds[0].replaceAll("[^\\d.]", ""));
						long endTime = Long.parseLong(bounds[1].replaceAll("[^\\d.]", ""));
						byte[] result = contract.evaluateTransaction("getImagesBetweenDates", String.valueOf(startTime), String.valueOf(endTime));
						producer.send(new ProducerRecord<>("result-data-topic", new String(result)));
					}
				}
				consumer.commitAsync();
			}
		}
	}
	
	private static Properties getConsumerProperties() {
		Properties consumerProps = new Properties();
		consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP);
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
		consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return consumerProps;
	}
	
	private static Properties getProducerProperties() {
		Properties producerProps = new Properties();
		producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP);
		producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
		producerProps.put(ProducerConfig.RETRIES_CONFIG, 0);
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return producerProps;
	}
}
