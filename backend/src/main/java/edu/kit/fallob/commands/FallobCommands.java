package edu.kit.fallob.commands;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.springConfig.FallobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class FallobCommands implements UserDetailsService {
	
	private DaoFactory daoFactory;
	private UserDao userDao;
	
	
	public FallobCommands() throws FallobException {
		// TODO Until the data base is fully implemented, we catch the error so the program could be started - should we remove try-catch after that?
		try {
			daoFactory = new DaoFactory();
			userDao = daoFactory.getUserDao();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // JPA Beispiel fur die Implementation

//        User user = userRepo.findByUsername(username);

//        if (user == null || !user.isVerified()) {
//            throw new UsernameNotFoundException("User not found in the database or is not verified");
//        } else {
//            List <SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getUserType));
//
//            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
//        }
		User user = null;
		try {
			user = userDao.getUserByUsername(username);
		} catch (FallobException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    	authorities.add(new SimpleGrantedAuthority(user.toString()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
    
    
    public boolean register(String username, String password, String email) throws FallobException {
    	return false;
    }
    
    
    public FallobConfiguration getFallobConfiguration() {
    	return FallobConfiguration.getInstance();
    }
}
