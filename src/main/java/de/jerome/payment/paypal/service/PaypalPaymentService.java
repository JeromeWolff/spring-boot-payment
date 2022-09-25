package de.jerome.payment.paypal.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import de.jerome.payment.PaymentMethod;
import de.jerome.payment.paypal.PaypalPaymentIntent;
import de.jerome.payment.paypal.config.PaypalConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@ConditionalOnClass(PaypalConfiguration.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class PaypalPaymentService {
  private final APIContext apiContext;

  public Payment createSinglePayment(double total, String currency, PaymentMethod method, PaypalPaymentIntent intent, String description, String successURL, String cancelURL) throws PayPalRESTException {
    var amount = this.createAmount(total, currency);
    var payer = this.createPayer(method);
    var transaction = this.createTransaction(amount, description);
    var redirectURLs = this.createRedirectUrls(successURL, cancelURL);
    return this.createSinglePayment(payer, transaction, intent, redirectURLs);
  }

  public Payment createSinglePayment(Payer payer, Transaction transaction, PaypalPaymentIntent intent, RedirectUrls redirectUrls) throws PayPalRESTException {
    var transactionList = this.createSingletonTransactionList(transaction);
    return this.createPayment(payer, transactionList, intent, redirectUrls);
  }

  public Payment createPayment(Payer payer, List<Transaction> transactions, PaypalPaymentIntent intent, RedirectUrls redirectUrls) throws PayPalRESTException {
    var payment = new Payment()
      .setPayer(payer)
      .setTransactions(transactions)
      .setIntent(intent.name())
      .setRedirectUrls(redirectUrls);
    return payment.create(this.apiContext);
  }

  private Amount createAmount(double total, String currency) {
    return new Amount()
      .setCurrency(currency)
      .setTotal(Double.toString(total));
  }

  private Transaction createTransaction(Amount amount, String description) {
    return (Transaction) new Transaction()
      .setAmount(amount)
      .setDescription(description);
  }

  private List<Transaction> createTransactionList(Transaction... transactions) {
    return Arrays.asList(transactions);
  }

  private List<Transaction> createSingletonTransactionList(Transaction transaction) {
    return Collections.singletonList(transaction);
  }

  private Payer createPayer(PaymentMethod method) {
    Assert.isTrue(method == PaymentMethod.PAYPAL, "Invalid payment method");
    return new Payer()
      .setPaymentMethod(method.name());
  }

  private RedirectUrls createRedirectUrls(String successURL, String cancelURL) {
    return new RedirectUrls()
      .setReturnUrl(successURL)
      .setCancelUrl(cancelURL);
  }

  public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
    var payment = new Payment()
      .setId(paymentId);
    var paymentExecution = new PaymentExecution()
      .setPayerId(payerId);
    return payment.execute(this.apiContext, paymentExecution);
  }
}
