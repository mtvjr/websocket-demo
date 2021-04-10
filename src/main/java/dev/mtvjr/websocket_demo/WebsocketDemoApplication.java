package dev.mtvjr.websocket_demo;

import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.ContainerInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;

@SpringBootApplication
public class WebsocketDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketDemoApplication.class, args);
	}

	@Bean
    public ServletRegistrationBean atmosphereServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new AtmosphereServlet(), "/chat/*");

		// Heartbeat Monitor Configuration
		registration.addInitParameter(
				"org.atmosphere.interceptor.HeartBeatInterceptor.clientHeartFrequencyInSeconds",
				"10"
		);

		// We want this to be created as early as possible, before EmbeddedAtmosphereInitializer
		registration.setLoadOnStartup(0);
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registration;
	}

	@Configuration
	public static class WebConfig implements WebMvcConfigurer {
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("/").setViewName("chatroom");
		}
	}

	private static class EmbeddedAtmosphereInitializer extends ContainerInitializer implements ServletContextInitializer {
		@Override
		public void onStartup(ServletContext servletContext) throws ServletException {
			onStartup(Collections.emptySet(), servletContext);
		}
	}

	@Bean
	public EmbeddedAtmosphereInitializer atmosphereInitializer() {
		return new EmbeddedAtmosphereInitializer();
	}

}
