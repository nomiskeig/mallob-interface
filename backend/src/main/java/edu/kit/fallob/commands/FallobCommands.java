package edu.kit.fallob.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service @Slf4j
public class FallobCommands implements UserDetailsService {
	
	private DaoFactory daoFactory;
	private UserDao userDao;
	
	
	public FallobCommands() {
		daoFactory = new DaoFactory();
		userDao = daoFactory.getUserDao(); 
	}
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // JPA Beispiel fur die Implementation

//        User user = userRepo.findByUsername(username);
//        if(user == null) {
//            log.error("User not found in the database");
//            throw new UsernameNotFoundException("User not found in the database");
//        } else {
//            log.info("User found in the database: {}", username);
//            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            user.getRoles().forEach(role -> {
//                authorities.add(new SimpleGrantedAuthority(role.getName()));
//            });
//            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
//        }
    	User user = userDao.getUserByUsername(username);
    	List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    	authorities.add(new SimpleGrantedAuthority(user.toString()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
    
    
    public void register(String username, String password, String email) {
    	
    }
    
    
    public FallobConfiguration getFallobConfiguration() {
    	return FallobConfiguration.getInstance();
    }
}
