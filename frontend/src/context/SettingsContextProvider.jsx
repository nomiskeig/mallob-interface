import React, { useState, createContext, useEffect } from 'react';
import axios from 'axios';
import { devSettings } from './devDefaults';

export const SettingsContext = createContext({});
export function SettingsContextProvider({ children }) {
	const [settings, setSettings] = useState({ test: 'test' });
	const [isLoaded, setLoaded] = useState(false);
	useEffect(() => {
		async function fetchSettings() {
			await axios
				.get(process.env.REACT_APP_API_BASE_PATH + '/api/v1/system/config')
				.then((res) => {
					setSettings(res.data);
					setLoaded(true);
				})
				.catch((err) => console.log(err.message));
		}
		if (process.env.NODE_ENV === 'development') {
			setSettings(devSettings);
			setLoaded(true);
		} else {
			fetchSettings();
		}
	}, []);

	return (
		<SettingsContext.Provider
			value={{ settings: settings, isLoaded: isLoaded }}
		>
			{children}
		</SettingsContext.Provider>
	);
}
