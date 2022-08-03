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
import {JobPage} from './pages/jobPage/JobPage'

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
												<div className='heightContainer'>
													<Navbar highlight={PAGE_SUBMIT} />
												</div>
											}
										/>
										<Route
											path='/admin'
											element={
												<div className='heightContainer'>
													<Navbar highlight={PAGE_ADMIN} />
												</div>
											}
										/>
										<Route
											path='/jobs'
											element={
												<div className='heightContainer'>
													<Navbar highlight={PAGE_JOBS} />
												</div>
											}
										/>
										<Route
											path='/job/:jobID'
											element={
												<div className='heightContainer'>
													<Navbar highlight={PAGE_JOBS} />
													<JobPage />
												</div>
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
