package cn.lanyj.keeper;

import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.lanyj.keeper.auth.AuthenticationInterceptor;
import cn.lanyj.keeper.auth.AuthenticationTokenArgumentResolver;

@Configuration
@EnableWebMvc
@EnableScheduling
public class WebConfig implements WebMvcConfigurer {
	private static final String[] RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/", "classpath:/resources/",
			"classpath:/static/", "classpath:/public/" };

	private Logger log = LoggerFactory.getLogger(WebConfig.class);

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		jsonConverter.setDefaultCharset(Charset.forName("UTF-8"));
		converters.add(jsonConverter);
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.ALL);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		log.debug("Register CORS configuration");
		registry.addMapping("/**")
			.allowedMethods("*")
			.allowedHeaders("*")
			.allowedOrigins("*")
			.allowCredentials(true)
			.maxAge(3600);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(RESOURCE_LOCATIONS);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		WebMvcConfigurer.super.addInterceptors(registry);
		registry.addInterceptor(new AuthenticationInterceptor());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
		resolvers.add(new AuthenticationTokenArgumentResolver());
	}
	
}
