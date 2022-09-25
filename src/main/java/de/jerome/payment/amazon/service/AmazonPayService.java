package de.jerome.payment.amazon.service;

import com.amazon.pay.api.AmazonPayClient;
import de.jerome.payment.amazon.config.AmazonPayConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnClass(AmazonPayConfiguration.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class AmazonPayService {
  private final AmazonPayClient amazonPayClient;
}
