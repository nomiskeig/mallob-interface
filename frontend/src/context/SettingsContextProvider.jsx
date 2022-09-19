import React, { useState, createContext, useEffect, useContext } from 'react';
import axios from 'axios';
import { InfoContext, TYPE_ERROR } from './InfoContextProvider';
import { UserContext } from './UserContextProvider';
import { useNavigate } from 'react-router-dom';
export const SettingsContext = createContext({});
export function SettingsContextProvider({ children }) {
	const [settings, setSettings] = useState({});
	const [isLoaded, setLoaded] = useState(false);
	const infoContext = useContext(InfoContext);
	const userContext = useContext(UserContext);
	const navigate = useNavigate();
	useEffect(() => {
		async function fetchSettings() {
			await axios({
				method: 'get',
				url: process.env.REACT_APP_API_BASE_PATH + '/api/v1/system/config',
				headers: {
					Authorization: 'Bearer ' + userContext.user.token,
				},
			})
				.then((res) => {
					setSettings(res.data);
					setLoaded(true);
				})
				.catch((err) => {
					if (err.response.status === 403) {
						infoContext.handleInformation(
							'The stored token is invalid.',
							TYPE_ERROR
						);
						userContext.logout();
						navigate('/login');
						return;
					}
					infoContext.handleInformation(
						`Could not load settings.\nReason: ${
							err.response.data.message
								? err.response.data.message
								: err.message
						}`,
						TYPE_ERROR
					);
				});
		}
		if (userContext.user.isLoaded) {
			fetchSettings();
		}
	}, [infoContext, userContext]);

	return (
		<SettingsContext.Provider
			value={{ settings: settings, isLoaded: isLoaded }}
		>
			{children}
		</SettingsContext.Provider>
	);
}
