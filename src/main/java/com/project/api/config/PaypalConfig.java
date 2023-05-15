package com.project.api.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

@Configuration
public class PaypalConfig {
	private String clientId = "AapC_ijQ9g_QTHoGObndq2X1BM1a7W3hMCmfOwT1e9iDpLaw4Dw_KfmhOROzaBQ3ZgC146kK-Ng49zS2";
	private String clientSecret = "EAevRX_LMpRYJdiWNrtEpySKyyUnZHXICXGL63scRv5VWDZMitsDVAbGZCmYV_rqAe9UGxI1MYso2eHH";
	private String mode = "sandbox";
	@Bean
	public Map<String, String> paypalSdkConfig() {
		Map<String, String> sdkConfig = new HashMap<>();
		sdkConfig.put("mode", mode);
		return sdkConfig;
	}

	@Bean
	public OAuthTokenCredential authTokenCredential() {
		return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
	}

	@Bean
	public APIContext apiContext() throws PayPalRESTException {
		APIContext apiContext = new APIContext(authTokenCredential().getAccessToken());
		apiContext.setConfigurationMap(paypalSdkConfig());
		return apiContext;
	}
}
