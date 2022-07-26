import React from 'react';
import './App.css';
import './custom.scss';
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
import { InfoContextProvider } from './context/InfoContextProvider';
import { AllContext, AllContextProvider } from './context/AllContextProvider';
import { RequireAuth } from './global/RequireAuth';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import {
	Navbar,
	PAGE_JOBS,
	PAGE_VIZ,
	PAGE_SUBMIT,
	PAGE_ADMIN,
} from './global/navbar/Navbar';
import { NotFoundPage } from './pages/notFound/NotFoundPage';

class App extends React.Component {
	constructor(props) {
		super(props);
		this.vpmref = React.createRef();
		// TODO: reenable this
		//if (process.env.NODE_ENV === 'development') {
		localStorage.setItem(
			'fallob-token',
			'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYWRtaW4iLCJ1c2VybmFtZSI6InRlc3R1c2VyIn0.IxNQz1xtLmk1vZx3S7SrlwDFR0j9PyZoB29q5_Cbh-s'
		);
		//}
	}
	render() {
		return (
			<div className='App'>
				<InfoContextProvider>
					<JobContextProvider>
						<SettingsContextProvider>
							<UserContextProvider>
								<BrowserRouter>
									<Routes>
										<Route path='/login' element={<div>Login page</div>} />
										<Route
											path='/visualization'
											element={
												<AllContextProvider>
													<Navbar highlight={PAGE_VIZ} />
													<AllContext.Consumer>
														{(context) => (
															<RequireAuth>
																{context.settingsContext.isLoaded && (
																	<VisualizationPageManager
																		vpmref={this.vpmref}
																		ref={this.vpmref}
																		context={context}
																	></VisualizationPageManager>
																)}
															</RequireAuth>
														)}
													</AllContext.Consumer>
												</AllContextProvider>
											}
										/>
										<Route
											path='/submit'
											element={
												<div>
													<Navbar highlight={PAGE_SUBMIT} />
												</div>
											}
										/>
										<Route
											path='/jobs'
											element={
												<div>
													<Navbar highlight={PAGE_JOBS} />
												</div>
											}
										/>
										}
										<Route path='*' element={<NotFoundPage />} />
									</Routes>
								</BrowserRouter>
							</UserContextProvider>
						</SettingsContextProvider>
					</JobContextProvider>
				</InfoContextProvider>
			</div>
		);
	}
}

export default App;
