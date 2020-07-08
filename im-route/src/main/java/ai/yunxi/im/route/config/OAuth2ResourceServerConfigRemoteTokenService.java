//package ai.yunxi.im.route.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
//
//
///**
// * 用于权限校验
// */
//@Configuration
//@EnableResourceServer
//public class OAuth2ResourceServerConfigRemoteTokenService extends ResourceServerConfigurerAdapter {
//
//    @Override
//    public void configure(final HttpSecurity http) throws Exception {
//        // @formatter:off
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                .and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS).permitAll()
//                .antMatchers("/**").authenticated();
////                .antMatchers(HttpMethod.OPTIONS).permitAll();
////                .anyRequest().permitAll();
//        // @formatter:on
//    }
//
//    @Primary
//    @Bean
//    public RemoteTokenServices tokenServices() {
//        final RemoteTokenServices tokenService = new RemoteTokenServices();
//        //blog项目
//        tokenService.setCheckTokenEndpointUrl("http://localhost:8888/users/check_token");
//        tokenService.setClientId("fooClientIdPassword");
//        tokenService.setClientSecret("secret");
//        return tokenService;
//    }
//
//}
