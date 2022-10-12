package de.jerome.payment.amazon.service;

import com.amazon.pay.api.AmazonPayClient;
import com.amazon.pay.api.PayConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnClass(PayConfiguration.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class AmazonPayService {
  private final AmazonPayClient amazonPayClient;
}
