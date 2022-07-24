package edu.kit.fallob.api.request.controller;

import edu.kit.fallob.commands.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityRequestsTests.Config.class)
@WebAppConfiguration
public class SecurityRequestsTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserDetailsService userDetailsService;

    private MockMvc mvc;

    @MockBean
    private FallobCommands fallobCommands;

    @MockBean
    private MallobCommands mallobCommands;

    @MockBean
    private JobAbortCommands jobAbortCommands;

    @MockBean
    private JobResultCommand jobResultCommand;

    @MockBean
    private JobPendingCommmand jobPendingCommmand;

    @MockBean
    private JobDescriptionCommands jobDescriptionCommands;

    @MockBean
    private JobInformationCommands jobInformationCommands;

    @MockBean
    private JobSubmitCommands jobSubmitCommands;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).apply(springSecurity()).build();
    }

    @Test
    public void requestProtectedUrlWithUser() throws Exception {
        this.mvc.perform(get("/").with(user("user")))
                // Ensure we got past Security
                .andExpect(status().isNotFound())
                // Ensure it appears we are authenticated with user
                .andExpect(authenticated().withUsername("user"));
    }

    @Test
    public void requestProtectedUrlWithAdmin() throws Exception {
        this.mvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                // Ensure we got past Security
                .andExpect(status().isNotFound())
                // Ensure it appears we are authenticated with admin
                .andExpect(authenticated().withUsername("admin"));
    }

    @Test
    public void requestProtectedUrlWithUserDetails() throws Exception {
        UserDetails user = this.userDetailsService.loadUserByUsername("user");
        this.mvc.perform(get("/").with(user(user)))
                // Ensure we got past Security
                .andExpect(status().isNotFound())
                // Ensure it appears we are authenticated with user
                .andExpect(authenticated().withAuthenticationPrincipal(user));
    }

    @Test
    public void requestProtectedUrlWithAuthentication() throws Exception {
        Authentication authentication = new TestingAuthenticationToken("test", "notused", "ROLE_USER");
        this.mvc.perform(get("/").with(authentication(authentication)))
                // Ensure we got past Security
                .andExpect(status().isNotFound())
                // Ensure it appears we are authenticated with user
                .andExpect(authenticated().withAuthentication(authentication));
    }

//    @Test
//    public void requestAbortJob() throws Exception {
//        when(jobAbortCommands.abortSingleJob("", 1)).thenReturn(true);
//
//        this.mvc.perform(post("/api/v1/jobs/cancel/single/{jobId}", 1).with(user("admin").roles("ADMIN")))
//
//                // Ensure it appears we are authenticated with user
//                .andExpect(authenticated().withUsername("admin"))
//                .andDo(print()).andExpect(status().isForbidden())
//                .andExpect(content().string(containsString("1")));
//    }

    @EnableWebSecurity
    @EnableWebMvc
    static class Config extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    .authorizeRequests()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                    .and()
                    .formLogin();
            // @formatter:on
        }

        @Autowired
        void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            // @formatter:off
            auth
                    .inMemoryAuthentication()
                    .withUser("user").password("password").roles("USER");
            // @formatter:on
        }

        @Override
        @Bean
        public UserDetailsService userDetailsServiceBean() throws Exception {
            return super.userDetailsServiceBean();
        }

    }

}