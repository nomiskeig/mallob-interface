import React, { useState, createContext, useEffect } from 'react';
import axios from 'axios';

const jwt = require('jsonwebtoken');
export const NO_TOKEN_AVAILABLE = 'noTokenAvailable';
export const LOCAL_STORAGE_TOKEN = 'fallob-token';
export const UserContext = createContext(
	'user context is not correctly connected'
);
export function UserContextProvider({ children }) {
	const [user, setUser] = useState({
		token: 'noTokenAvailable',
		role: '',
		username: '',
	});

	useEffect(() => {
		function tryToLoginUser() {
			let token = localStorage.getItem(LOCAL_STORAGE_TOKEN);
			if (token && user.token === NO_TOKEN_AVAILABLE) {
				login(token);
			}
		}
		tryToLoginUser();
	}, []);
	async function logout() {
		localStorage.removeItem(LOCAL_STORAGE_TOKEN);
		setUser({ token: NO_TOKEN_AVAILABLE, role: '', username: '' });
	}

	function login(token) {
		localStorage.setItem(LOCAL_STORAGE_TOKEN, token);
		let payload = extractPayload(token);
		setUser({ token: token, role: payload.role, username: payload.username });
	}
	function extractPayload(token) {
		let decoded = jwt.decode(token);
		return decoded;
	}

	return (
		<UserContext.Provider value={{ user, login, logout }}>
			{children}
		</UserContext.Provider>
	);
}
