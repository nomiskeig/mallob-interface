import React, { useState, createContext } from 'react';

const jwt = require('jsonwebtoken');
export const NO_TOKEN_AVAILABLE = 'noTokenAvailable';
export const LOCAL_STORAGE_TOKEN = 'fallob-token';
export const ROLE_ADMIN = 'admin';
export const ROLE_USER = 'user';
export const UserContext = createContext(
	'user context is not correctly connected'
);
export function UserContextProvider({ children }) {
    let token = localStorage.getItem(LOCAL_STORAGE_TOKEN);
    const payload = extractPayload(token);
    


	const [user, setUser] = useState({
        token: token ? token : NO_TOKEN_AVAILABLE,
		role: token ? payload.role : '',
		username: token ? payload.username : '',
		isLoaded: token ? true : false,
        isVerified: token ? payload.verified : false,
	});

	async function logout() {
		localStorage.removeItem(LOCAL_STORAGE_TOKEN);
		setUser({
			token: NO_TOKEN_AVAILABLE,
			role: '',
			username: '',
			isLoaded: false,
            isVerified: false
		});
	}



	function login(token) {
		localStorage.setItem(LOCAL_STORAGE_TOKEN, token);
		let payload = extractPayload(token);
		setUser({
			token: token,
			role: payload.role,
			username: payload.username,
			isLoaded: true,
            isVerified: payload.verified
		});
	}
	function extractPayload(token) {
		let decoded = jwt.decode(token);
        console.log('decoded', decoded)


        if (!decoded) {
            return null;
        }

        let payload = {
            username: decoded.sub
        }
        let authorities = decoded.authorities;
        authorities = authorities.substring(1, authorities.length -1);
        authorities = authorities.split(', ')
        if (authorities[1] === 'Verified') {
            payload['verified'] = true;
            decoded.verified = true;
        } else {
            decoded.verified = false;
            payload['verified'] = false;
        }
        if (authorities[0] === 'NORMAL_USER') {
            payload['role'] = ROLE_USER;
        } else {
            payload['role'] = ROLE_ADMIN

        }
        console.log(payload)

		return payload;
	}

	return (
		<UserContext.Provider value={{ user, login, logout }}>
			{children}
		</UserContext.Provider>
	);
}
