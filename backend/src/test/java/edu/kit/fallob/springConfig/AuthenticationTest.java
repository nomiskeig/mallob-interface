package edu.kit.fallob.springConfig;

import edu.kit.fallob.dataobjects.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        long thirtyDayValidity = 5 * 60 * 60 * 1000;
        // asserts the token will expire at the calculated date in ms, accounting for an error of +-10s. (because of the time the system needs for calculations)
        Assertions.assertEquals(jwtTokenUtil.getExpirationDateFromToken(token).getTime() / 10000, (currentTime + thirtyDayValidity) / 10000);
        Assertions.assertEquals(jwtTokenUtil.getAuthorities(token), authorities.toString());
        Assertions.assertEquals(jwtTokenUtil.getUsernameFromToken(token), username);
    }
}
