import { React, useState, createContext } from 'react';

export const InfoContext = createContext({});

export function InfoContextProvider({ children }) {
	const [error, setError] = useState('');
	const [warning, setWarning] = useState('');
	const [info, setInfo] = useState('');

	function displayWarning(warningText) {
		setWarning(warningText);
	}
	function displayError(errorText) {
		setError(errorText);
	}
	function displayInfo(infoText) {
		setInfo(infoText);
	}

	let shouldDisplay = warning || info;
	let text = warning ? warning : info;
	return (
		<div className='h-100'>
			{shouldDisplay && (
				<div className='alert alert-info alert-dismissible position-absolute alert w-50 start-50 translate-middle' style={{top: '50px'}}role='alert'>
					<div>{text}</div> 
					<button
						type='button'
						className='btn-close'
						//data-bs-dismiss='alert'
						aria-label='Close'
                        onClick={() => setWarning('')}
					></button>
				</div>
			)}
			<InfoContext.Provider
				value={{
					displayWarning: displayWarning,
					displayInfo: displayInfo,
					displayError: displayError,
				}}
			>
				{children}
			</InfoContext.Provider>
		</div>
	);
}
