import React, { useState, createContext, useEffect, useContext } from 'react';
import axios from 'axios';
//import { devSettings } from './devDefaults';
import { InfoContext, TYPE_WARNING } from './InfoContextProvider';
import { UserContext } from './UserContextProvider';
export const SettingsContext = createContext({});
export function SettingsContextProvider({ children }) {
	const [settings, setSettings] = useState({ test: 'test' });
	const [isLoaded, setLoaded] = useState(false);
	const infoContext = useContext(InfoContext);
	const userContext = useContext(UserContext);
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
				.catch(() =>
					infoContext.handleInformation('could not get settings', TYPE_WARNING)
				);
		}
		//if (process.env.NODE_ENV === 'development') {
		//	setSettings(devSettings);
		//	setLoaded(true);
		//} else {
		if (userContext.user.isLoaded) {
			fetchSettings();
		}
		//}
	}, [infoContext, userContext]);

	return (
		<SettingsContext.Provider
			value={{ settings: settings, isLoaded: isLoaded }}
		>
			{children}
		</SettingsContext.Provider>
	);
}
