package com.gskwin.authcenter;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.gskwin.authcenter.domain.oauth.AuthenticationIdGenerator;
import com.gskwin.authcenter.domain.oauth.DefaultAuthenticationIdGenerator;
import com.gskwin.authcenter.domain.shared.BeanProvider;

@EnableTransactionManagement
@SpringBootApplication
public class Springboot2OauthCenterApplication {

	public static void main(String[] args) {
		ApplicationContext app = SpringApplication.run(Springboot2OauthCenterApplication.class, args);
		BeanProvider.initialize(app);
	}

	@Bean
	public AuthenticationIdGenerator getAuthenticationIdGenerator() {
		return new DefaultAuthenticationIdGenerator();
	}
	
	@Bean
	public OAuthIssuer getOAuthIssuer() {
		return new OAuthIssuerImpl(new MD5Generator());
	}
}
