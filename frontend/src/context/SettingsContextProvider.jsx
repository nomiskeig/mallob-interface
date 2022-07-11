import React, { useState, createContext, useEffect } from 'react';
import axios from 'axios';

export const SettingsContext = createContext({});
export function SettingsContextProvider({ children }) {
	const [settings, setSettings] = useState({test: 'test'});
	useEffect(() => {
		async function fetchSettings() {
			await axios
				.get(process.env.API_BASE_PATH + '/api/v1/system/config')
				.then((res) => (
					setSettings(res.data))
				)
				.catch((err) => console.log(err.message));
		}
		fetchSettings();
	}, []);

	return (
		<SettingsContext.Provider value={{settings}}>
			{children}
		</SettingsContext.Provider>
	);
}
