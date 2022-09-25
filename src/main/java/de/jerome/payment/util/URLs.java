package de.jerome.payment.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class URLs {
  public static String constructBaseURL(HttpServletRequest request) {
    var scheme = request.getScheme();
    var serverName = request.getServerName();
    var serverPort = request.getServerPort();
    var contextPath = request.getContextPath();
    var urlBuilder = new StringBuilder().append(scheme).append("://").append(serverName);
    if (serverPort != 80 && serverPort != 443) {
      urlBuilder.append(":").append(serverPort);
    }
    urlBuilder.append(contextPath);
    if (!urlBuilder.toString().endsWith("/")) urlBuilder.append("/");
    return urlBuilder.toString();
  }
}
