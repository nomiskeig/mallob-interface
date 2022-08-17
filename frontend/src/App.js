import React from 'react';
import './App.css';
import './custom.scss';
import { VisualizationPageManager } from './pages/visualization/controller/VisualizationPageManager';
import { JobContextProvider } from './context/JobContextProvider';
import { SettingsContextProvider } from './context/SettingsContextProvider';
import { UserContextProvider } from './context/UserContextProvider';
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
import { LoginPage } from './pages/login/LoginPage';
import { JobPage } from './pages/jobPage/JobPage';
import { JobTablePage } from './pages/jobTable/JobTablePage';
import { SubmitPage } from './pages/submitJob/SubmitPage';
import { JobPageRedirect } from './pages/jobPage/jobPageRedirect';

class App extends React.Component {
	constructor(props) {
		super(props);
		this.vpmref = React.createRef();
	}
	render() {
		return (
			<div className='App'>
				<InfoContextProvider>
					<UserContextProvider>
						<JobContextProvider>
							<SettingsContextProvider>
								<BrowserRouter>
									<Routes>
										<Route path='/login' element={<LoginPage />} />
										<Route
											path='/visualization'
											element={
												<RequireAuth>
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
												</RequireAuth>
											}
										/>
										<Route
											path='/submit'
											element={
												<RequireAuth>
													<div className='heightContainer'>
														<Navbar highlight={PAGE_SUBMIT} />
														<SubmitPage />
													</div>
												</RequireAuth>
											}
										/>
										<Route
											path='/admin'
											element={
												<RequireAuth>
													<div className='heightContainer'>
														<Navbar highlight={PAGE_ADMIN} />
													</div>
												</RequireAuth>
											}
										/>
										<Route
											path='/jobs'
											element={
												<RequireAuth>
													<div className='heightContainer'>
														<Navbar highlight={PAGE_JOBS} />
														<JobTablePage />
													</div>
												</RequireAuth>
											}
										/>
										<Route
											path='/job/:jobID'
											element={
												<RequireAuth>
													<div className='heightContainer'>
														<Navbar highlight={PAGE_JOBS} />
														<JobPage />
													</div>
												</RequireAuth>
											}
										/>
										<Route
											path='/job/:username/:jobname'
											element={
												<RequireAuth>
													<JobPageRedirect></JobPageRedirect>
												</RequireAuth>
											}
										/>
										<Route path='*' element={<NotFoundPage />} />
									</Routes>
								</BrowserRouter>
							</SettingsContextProvider>
						</JobContextProvider>
					</UserContextProvider>
				</InfoContextProvider>
			</div>
		);
	}
}

export default App;
