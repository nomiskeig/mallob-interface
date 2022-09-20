import React, { createContext } from 'react';

import { SettingsContext } from './SettingsContextProvider';

import { UserContext } from './UserContextProvider';
import { JobContext } from './JobContextProvider';
import { InfoContext } from './InfoContextProvider';


export const AllContext = createContext({allContext: 'allContext'});
export function AllContextProvider({ children }) {
	return (
		<UserContext.Consumer>
			{(userContext) => (
				<SettingsContext.Consumer>
					{(settingsContext) => (
						<JobContext.Consumer>
							{(jobContext) => (
								<InfoContext.Consumer>
									{(infoContext) => (
										<AllContext.Provider
											value={{
												userContext: userContext,
												settingsContext: settingsContext,
												jobContext: jobContext,
                                                infoContext: infoContext
											}}
										>
											{children}
										</AllContext.Provider>
									)}
								</InfoContext.Consumer>
							)}
						</JobContext.Consumer>
					)}
				</SettingsContext.Consumer>
			)}
		</UserContext.Consumer>
	);
}
