import React from 'react';
import './App.css';
import { VisualizationPageManager } from './pages/visualization/controller/VisualizationPageManager';
import { JobContextProvider, JobContext } from './context/JobContextProvider';
import {
	SettingsContextProvider,
	SettingsContext,
} from './context/SettingsContextProvider';
import {
	UserContextProvider,
	UserContext,
} from './context/UserContextProvider';
import { ErrorHandler } from './global/errorHandler/ErrorHandler';
import { RequireAuth } from './global/RequireAuth';
import {Routes, Route, BrowserRouter } from 'react-router-dom'

class App extends React.Component {
	constructor(props) {
		super(props);
		this.vpmref = React.createRef();
        localStorage.setItem(
			'fallob-token',
			'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYWRtaW4iLCJ1c2VybmFtZSI6InRlc3R1c2VyIn0.IxNQz1xtLmk1vZx3S7SrlwDFR0j9PyZoB29q5_Cbh-s'
		);
	}
	render() {
		return (
			<div className='App'>
				<ErrorHandler>
					<JobContextProvider>
						<SettingsContextProvider>
							<UserContextProvider>
                                <BrowserRouter>
								<Routes>
									<Route path='/login' element={<div>Login page</div>} />
									<Route
										path='/visualization'
										element={
											<UserContext.Consumer>
												{(userContext) => (
													<SettingsContext.Consumer>
														{(settingsContext) => (
															<JobContext.Consumer>
																{(jobContext) => (
																	<RequireAuth>
																		<VisualizationPageManager
																			vpmref={this.vpmref}
																			ref={this.vpmref}
																			context={{
																				userContext: userContext,
																				settingsContext: settingsContext,
																				jobContext: jobContext,
																			}}
																		></VisualizationPageManager>
																	</RequireAuth>
																)}
															</JobContext.Consumer>
														)}
													</SettingsContext.Consumer>
												)}
											</UserContext.Consumer>
										}
									/>
								</Routes>
                                </BrowserRouter>
							</UserContextProvider>
						</SettingsContextProvider>
					</JobContextProvider>
				</ErrorHandler>
			</div>
		);
	}
}

export default App;
