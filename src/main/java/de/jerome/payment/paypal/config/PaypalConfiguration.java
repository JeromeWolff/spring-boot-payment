package de.jerome.payment.paypal.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(APIContext.class)
public class PaypalConfiguration {
  @Value("${payment.paypal.client.app}")
  private String clientId;
  @Value("${payment.paypal.client.secret}")
  private String clientSecret;
  @Value("${payment.paypal.mode}")
  private String mode;

  @Bean
  public APIContext apiContext() {
    return new APIContext(this.clientId, this.clientSecret, this.mode);
  }
}
