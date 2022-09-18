import { useContext, useEffect, useState } from 'react';
import { UserContext } from '../../context/UserContextProvider';
import './AdminPage.scss';
//import { Button } from '../../global/buttons/Button';
import axios from 'axios';
import format from 'date-fns/format'

export function AdminPage(props) {
	let userContext = useContext(UserContext);
	let [warnings, setWarnings] = useState([]);

	//contact API to get Warnings
	useEffect(() => {
		axios({
			method: 'get',
			url: process.env.REACT_APP_API_BASE_PATH + '/api/v1/system/mallobInfo',
			headers: {
				Authorization: 'Bearer ' + userContext.user.token,
			},
		})
			.then((res) => {
				setWarnings(res.data.warnings);
			})
			.catch((res) => {
				setWarnings([...warnings]);
			});
	}, []);

	/**
	 * This function has to actually gather the warning.
	 * @param {*} i
	 * @returns all warnings from API as array or null, if zero warning have been fetched (warnings is empty)
	 */
	function getWarnings() {
		if (warnings.length === 0) {
			return null;
		}
		return warnings;
	}

	//---------------------------------------functions necessary for warnings-container

	function createWarning(warning, i) {
		let time;
		try {
			time = new Date(warning.timestamp);
            time = format(time, "MM.dd.yyyy 'at' HH:mm:ss.SSS")
		} catch (e) {
			time = '';
		}
		return (
			<li
				className={`warningsElement ${
					i % 2 === 1 ? 'warningsElementOne' : 'warningsElementTwo'
				}`}
			>
				{time} {warning.message}
			</li>
		);
	}

	function getIndividualWarnings() {
		const items = [];
		const localWarnings = getWarnings();
		if (localWarnings == null) {
			return createWarning({ message: 'No warnings available', timestamp: "" }, 0);
		}
		for (let i = 0; i < localWarnings.length; i++) {
			items.push(createWarning(localWarnings[i], i));
		}
		return items;
	}

	function getWarningsAsList() {
		return <ul className='navbar'>{getIndividualWarnings()}</ul>;
	}

	function getWarningsElement() {
		return <nav className='navbar'>{getWarningsAsList()}</nav>;
	}
	/*
	//----------------------------------------------------------functions for other containers (windows - e.g. mallob start-button) on the site

	function startMallob() {
		//not yet implemented
	}

	function stopMallob() {
		//not yet implemented
	}

	function restartMallob() {
		//not yet implemented
	}

	function getMallobButtons() {
		return (
			<div>
				<div>
					<Button
						onClick={startMallob}
						color='#A5C9CA'
						text='Start Mallob'
						className='mallobButton'
					></Button>
				</div>
				<div>
					<Button
						onClick={stopMallob}
						text='Stop Mallob'
						className='mallobButton'
					></Button>
				</div>
				<div>
					<Button
						onClick={restartMallob}
						text='Restart Mallob'
						className='mallobButton'
					></Button>
				</div>
			</div>
		);
	}
    */

	function getContainerTitle(title) {
		return (
			<div className='diagnosticsTitle'>
				<h3>{title}</h3>
				<hr className='separator'></hr>
			</div>
		);
	}

	function getDiagnosticsContainer(title, content, specialClassName) {
		if (specialClassName == null) {
			//return ordinary diagnostics container
			return (
				<div className='diagnosticsContainer'>
					{getContainerTitle(title)}
					{content}
				</div>
			);
		}
		return (
			<div className={specialClassName}>
				{getContainerTitle(title)}
				{content}
			</div>
		);
	}

	return (
		<div className='py-5 registerPage'>
			<div className='pageContent'>
				{/**
				 * This adds the buttons to start, stop or restart mallob to the admin-site. Do not use until button-functions are implemented.
				 * {getDiagnosticsContainer("Mallob", getMallobButtons(), "mallobButtons")} */}
				{getDiagnosticsContainer('Warnings', getWarningsElement(), 'warnings')}
			</div>
		</div>
	);
}
