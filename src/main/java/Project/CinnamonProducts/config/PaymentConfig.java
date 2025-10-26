package Project.CinnamonProducts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "payment.gateway")
    public PaymentGatewayProperties paymentGatewayProperties() {
        return new PaymentGatewayProperties();
    }
    
    public static class PaymentGatewayProperties {
        private String name = "MockPaymentGateway";
        private String baseUrl = "http://localhost:8080/api/payment";
        private int timeoutSeconds = 30;
        private int retryAttempts = 3;
        private double successRate = 85.0;
        private boolean enableLogging = true;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
        
        public int getRetryAttempts() { return retryAttempts; }
        public void setRetryAttempts(int retryAttempts) { this.retryAttempts = retryAttempts; }
        
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
        
        public boolean isEnableLogging() { return enableLogging; }
        public void setEnableLogging(boolean enableLogging) { this.enableLogging = enableLogging; }
    }
}
