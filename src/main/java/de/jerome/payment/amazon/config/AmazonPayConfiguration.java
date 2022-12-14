package de.jerome.payment.amazon.config;

import com.amazon.pay.api.AmazonPayClient;
import com.amazon.pay.api.PayConfiguration;
import com.amazon.pay.api.ProxySettings;
import com.amazon.pay.api.exceptions.AmazonPayClientException;
import com.amazon.pay.api.types.Environment;
import com.amazon.pay.api.types.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Slf4j
@Configuration
@ConditionalOnClass(PayConfiguration.class)
public class AmazonPayConfiguration {
  @Value("${payment.amazon.key.public}")
  private String publicKeyId;
  @Value("${payment.amazon.key.private}")
  private String privateKey;
  @Value("${payment.amazon.region}")
  private Region region;
  @Value("${payment.amazon.environment}")
  private Environment environment;
  @Value("${payment.amazon.proxy.enabled")
  private boolean proxyEnabled;
  @Nullable
  @Value("${payment.amazon.proxy.host}")
  private String proxyHost;
  @Nullable
  @Value("${payment.amazon.proxy.port}")
  private int proxyPort;
  @Nullable
  @Value("${payment.amazon.proxy.username}")
  private String proxyUsername;
  @Nullable
  @Value("${payment.amazon.proxy.password}")
  private String proxyPassword;

  @Bean
  @Nullable
  public ProxySettings proxySettings() {
    return this.proxyEnabled ? new ProxySettings()
      .setProxyHost(this.proxyHost)
      .setProxyPort(this.proxyPort)
      .setProxyUser(this.proxyUsername)
      .setProxyPassword(this.proxyPassword != null ? this.proxyPassword.toCharArray() : new char[0]) : null;
  }

  @Bean
  @Nullable
  public PayConfiguration payConfiguration(ProxySettings proxySettings) {
    try {
      var payConfiguration = new PayConfiguration()
        .setPublicKeyId(this.publicKeyId)
        .setPrivateKey(this.privateKey.toCharArray())
        .setRegion(this.region)
        .setEnvironment(this.environment);
      if (proxySettings != null) {
        payConfiguration.setProxySettings(proxySettings);
      }
      return payConfiguration;
    } catch (AmazonPayClientException e) {
      log.error("An error occurred while configuring Amazon Pay", e);
      return null;
    }
  }

  @Bean
  @Nullable
  public AmazonPayClient amazonPayClient(PayConfiguration payConfiguration) {
    try {
      return new AmazonPayClient(payConfiguration);
    } catch (AmazonPayClientException e) {
      log.error("An error occurred while creating the Amazon Pay client", e);
      return null;
    }
  }
}
