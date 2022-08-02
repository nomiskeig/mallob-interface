package edu.kit.fallob.commands;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.springConfig.FallobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Slf4j
public class FallobCommands implements UserDetailsService {

    public boolean register(String email, String username, String password) throws FallobException {
        return false;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // JPA Beispiel fur die Implementation

//        User user = userRepo.findByUsername(username);

//        if(user == null || !user.isVerified()) {
//            throw new UsernameNotFoundException("User not found in the database or is not verified");
//        } else {
//            List <SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getUserType));
//
//            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
//        }
        return null;
    }

    public FallobConfiguration getFallobConfiguration() {
        return null;
    }
}
