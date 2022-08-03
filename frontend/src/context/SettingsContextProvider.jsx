import React, { useState, createContext, useEffect, useContext } from 'react';
import axios from 'axios';
//import { devSettings } from './devDefaults';
import {InfoContext, TYPE_WARNING} from './InfoContextProvider';
export const SettingsContext = createContext({});
export function SettingsContextProvider({ children }) {
	const [settings, setSettings] = useState({ test: 'test' });
	const [isLoaded, setLoaded] = useState(false);
    const infoContext = useContext(InfoContext)
	useEffect(() => {
        console.log('fetching settings')
		async function fetchSettings() {

			await axios
				.get(process.env.REACT_APP_API_BASE_PATH + '/api/v1/system/config')
				.then((res) => {
					setSettings(res.data);
					setLoaded(true);
				})
				.catch(() => infoContext.handleInformation('could not get settings', TYPE_WARNING));
		}
		//if (process.env.NODE_ENV === 'development') {
		//	setSettings(devSettings);
		//	setLoaded(true);
		//} else {
			fetchSettings();
		//}
	}, [infoContext] );

	return (
		<SettingsContext.Provider
			value={{ settings: settings, isLoaded: isLoaded }}
		>
			{children}
		</SettingsContext.Provider>
	);
}
