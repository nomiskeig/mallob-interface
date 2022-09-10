package edu.kit.fallob.commands;

import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.UserDao;
import edu.kit.fallob.dataobjects.NormalUser;
import edu.kit.fallob.dataobjects.User;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

	private static final String USERNAME_REQUIREMENTS = "Username must be between 4 and 25 characters";

	private static final String PASSWORD_TOO_SHORT = "Password must be at least 8 characters";
	private static final int USERNAME_LOWER_BOUND = 4;
	private static final int USERNAME_UPPER_BOUND = 25;
	private static final int PASSWORD_LOWER_BOUND = 8;
	private static final String DUPLICATE_USERNAME = "Username already exists";

	
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
			e.printStackTrace();
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
		if (username.length() < USERNAME_LOWER_BOUND || username.length() > USERNAME_UPPER_BOUND) {
			throw new FallobException(HttpStatus.BAD_REQUEST, USERNAME_REQUIREMENTS);
		}
		if (password.length() < PASSWORD_LOWER_BOUND) {
			throw new FallobException(HttpStatus.BAD_REQUEST, PASSWORD_TOO_SHORT);
		}
		//check if the username is already taken
		User user = userDao.getUserByUsername(username);

		if (user != null) {
			throw new FallobException(HttpStatus.BAD_REQUEST, DUPLICATE_USERNAME);
		}

		String encodedPassword = passwordEncoder.encode(password);
		userDao.save(new NormalUser(username, encodedPassword, email));
		return true;

    }
    
    
    public FallobConfiguration getFallobConfiguration() {
    	return FallobConfiguration.getInstance();
    }
}
