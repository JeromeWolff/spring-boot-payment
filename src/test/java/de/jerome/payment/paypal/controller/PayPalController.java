package de.jerome.payment.paypal.controller;

import com.paypal.api.payments.Links;
import com.paypal.base.rest.PayPalRESTException;
import de.jerome.payment.PaymentMethod;
import de.jerome.payment.paypal.PaypalPaymentIntent;
import de.jerome.payment.paypal.service.PaypalPaymentService;
import de.jerome.payment.util.URLs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class PayPalController {
  private static final String PAYPAL_SUCCESS_URL = "payment/success";
  private static final String PAYPAL_CANCEL_URL = "payment/cancel";
  private final PaypalPaymentService paypalPaymentService;

  @RequestMapping(method = RequestMethod.POST, value = "payment")
  public String pay(HttpServletRequest request) {
    String successURL = "%s%s".formatted(URLs.constructBaseURL(request), PAYPAL_SUCCESS_URL);
    String cancelURL = "%s%s".formatted(URLs.constructBaseURL(request), PAYPAL_CANCEL_URL);
    try {
      var payment = this.paypalPaymentService.createSinglePayment(10.00, "EUR", PaymentMethod.PAYPAL, PaypalPaymentIntent.SALE, "Verwendungszweck", successURL, cancelURL);
      for (Links link : payment.getLinks()) {
        if (link.getRel().equals("approval_url")) {
          return "redirect:%s".formatted(link.getHref());
        }
      }
    } catch (PayPalRESTException ex) {
      log.error(ex.getMessage());
    }
    return "redirect:/";
  }

  @RequestMapping(method = RequestMethod.GET, value = PAYPAL_SUCCESS_URL)
  public String success(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
    try {
      var payment = this.paypalPaymentService.executePayment(paymentId, payerId);
      if (payment.getState().equals("approved")) return "success";
    } catch (PayPalRESTException ex) {
      log.error(ex.getMessage());
    }
    return "redirect:/";
  }

  @RequestMapping(method = RequestMethod.GET, value = PAYPAL_CANCEL_URL)
  public String cancel() {
    return "cancel";
  }
}
