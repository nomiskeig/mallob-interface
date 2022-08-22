package edu.kit.fallob.commands;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.NormalUser;
import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FallobCommands implements UserDetailsService {
	
	private DaoFactory daoFactory;
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private static final String DUPLICATE_USERNAME = "Username already exists";

	private static final String DUPLICATE_EMAIL = "Email already exists";
	
	
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
		User user;

		try {
			user = userDao.getUserByUsername(username);
		} catch (FallobException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}

		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    	authorities.add(new SimpleGrantedAuthority(user.getUserType().getAuthority()));
		String isVerified = "Not verified";
		if (user.isVerified()) {
			isVerified = "Verified";
		}
		authorities.add(new SimpleGrantedAuthority(isVerified));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
    
    
    public boolean register(String username, String password, String email) throws FallobException {

		String encodedPassword = passwordEncoder.encode(password);
		userDao.save(new NormalUser(username, encodedPassword, email));
		return true;

    }
    
    
    public FallobConfiguration getFallobConfiguration() {
    	return FallobConfiguration.getInstance();
    }
}
