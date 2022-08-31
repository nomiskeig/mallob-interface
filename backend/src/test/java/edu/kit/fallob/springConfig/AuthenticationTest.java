package edu.kit.fallob.springConfig;

import edu.kit.fallob.commands.*;
import edu.kit.fallob.dataobjects.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthenticationTest {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private FallobCommands fallobCommands;

    @MockBean
    private MallobCommands mallobCommands;

    @MockBean
    private JobAbortCommands jobAbortCommands;

    @MockBean
    private JobResultCommand jobResultCommand;

    @MockBean
    private JobPendingCommand jobPendingCommand;

    @MockBean
    private JobDescriptionCommands jobDescriptionCommands;

    @MockBean
    private JobInformationCommands jobInformationCommands;

    @MockBean
    private JobSubmitCommands jobSubmitCommands;

    @MockBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockBean
    private AuthenticationManager authenticationManager;


    @Test
    public void JwtTokenTest() {
        String username = "kalo";
        String password = "password";
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(UserType.NORMAL_USER);
        String token = jwtTokenUtil.generateToken(new User(username, password, authorities));

        long currentTime = System.currentTimeMillis();
        Assertions.assertNotNull(token);
        // 30 days converted in ms
        long thirtyDayValidity = 5 * 60 * 60 * 100000;
        // asserts the token will expire at the calculated date in ms, accounting for an error of +-10s. (because of the time the system needs for calculations)
        Assertions.assertEquals(jwtTokenUtil.getExpirationDateFromToken(token).getTime() / 100000, (currentTime + thirtyDayValidity) / 100000);
        Assertions.assertEquals(jwtTokenUtil.getAuthorities(token), authorities.toString());
        Assertions.assertEquals(jwtTokenUtil.getUsernameFromToken(token), username);
    }
}
