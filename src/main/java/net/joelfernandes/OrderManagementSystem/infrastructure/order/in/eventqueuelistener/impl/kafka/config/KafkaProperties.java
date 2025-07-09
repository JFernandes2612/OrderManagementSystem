package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class KafkaProperties {

    public static final String BASIC_AUTH_USER_INFO = "basic.auth.user.info";

    @Value("${ordman.kafka.serviceaccount.namespace}")
    private String ordmanServiceaccountNamespace;

    @Value("${ordman.kafka.sasl.mechanism}")
    private String saslMechanism;

    @Value("${ordman.kafka.sasl.jaas.config}")
    private String saslJaasConfig;

    @Value("${ordman.kafka.basic.auth.credentials.source}")
    private String basicAuthCredentialsSource;

    @Value("${ordman.kafka.basic.auth.user.info}")
    private String basicAuthUserInfo;

    @Value("${ordman.kafka.security.protocol}")
    private String securityProtocol;

    @Value("${ordman.kafka.consumer.bootstrap-servers-config}")
    private String bootstrapServersConfig;

    @Value("${ordman.kafka.consumer.schema-registry-url-config}")
    private String schemaRegistryUrlConfig;

    @Value("${ordman.kafka.consumer.group-id}")
    private String groupId;

    @Value("${ordman.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${ordman.kafka.consumer.key-deserializer-class-config}")
    private String keyDeserializerClassConfig;

    @Value("${ordman.kafka.consumer.value-deserialize-class-config}")
    private String valueDeserializeClassConfig;

    @Value("${ordman.kafka.consumer.specific.avro.reader}")
    private String avroReader;

    public Map<String, Object> getKafkaConsumerProperties() {
        Map<String, Object> props = getBaseProps();

        addSecurityProps(props);

        log.info("bootstrap_servers_config = {}", bootstrapServersConfig);
        log.info("GROUP_ID_CONFIG = {}.{}", ordmanServiceaccountNamespace, groupId);
        log.info("AUTO_OFFSET_RESET_CONFIG = {}", autoOffsetReset);
        log.info("KEY_DESERIALIZER_CLASS_CONFIG = {}", keyDeserializerClassConfig);
        log.info("VALUE_DESERIALIZER_CLASS_CONFIG = {}", valueDeserializeClassConfig);
        log.info("SCHEMA_REGISTRY_URL_CONFIG = {}", schemaRegistryUrlConfig);
        log.info("SPECIFIC_AVRO_READER_CONFIG = {}", avroReader);
        log.info("SECURITY_PROTOCOL_CONFIG = {}", securityProtocol);
        log.info("SASL_MECHANISM = {}", saslMechanism);
        log.info("SASL_JAAS_CONFIG = {}", saslJaasConfig);
        log.info("BASIC_AUTH_CREDENTIALS_SOURCE = {}", basicAuthCredentialsSource);
        log.info("BASIC_AUTH_USER_INFO = {}", basicAuthUserInfo);

        return props;
    }

    private Map<String, Object> getBaseProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ordmanServiceaccountNamespace + "." + groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClassConfig);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializeClassConfig);
        props.put(
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrlConfig);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, avroReader);
        return props;
    }

    private void addSecurityProps(Map<String, Object> props) {
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        props.put(
                AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE,
                basicAuthCredentialsSource);
        props.put(BASIC_AUTH_USER_INFO, basicAuthUserInfo);
    }
}
