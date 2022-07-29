import { React, useState, createContext } from 'react';
import './InfoContextProvider.scss'
export const InfoContext = createContext({});
export const TYPE_INFO = 'info';
export const TYPE_WARNING = 'warning'
export const TYPE_ERROR = 'danger';
export const TYPE_UNRECOVERABLE = 'unrecoverable';
export function InfoContextProvider({ children }) {
    const [text ,setText] = useState('')
    const [type, setType] = useState('');
    const [hasInfo, setHasInfo] = useState(false);
    const [isUnrecoverable, setIsUnrecoverable] = useState(false);

    function handleInformation(text, type) {
        if (type === TYPE_UNRECOVERABLE) {
            setIsUnrecoverable(true);
            setText(text)
            return;
        }
        if (isUnrecoverable) {
            return;
        }
        setText(text);
        setType(type);
        setHasInfo(true);
    }

    function dismissInformation() {
        setText('');
        setHasInfo(false);
    }


	let display;
	if (type === TYPE_UNRECOVERABLE || isUnrecoverable) {
		display = (
			<div className='h-100 w-100 unrecoverableContainer'>
				<div className='w-25 h-25 position-absolute top-50 start-50 translate-middle'>
					<b> An unrecoverable error has occured:</b>
                    <br></br>
					{text}
                <br></br>
					Please reload the page to continue.
                <br></br>
                    <button className='btn btn-primary unrecoverableButton' onClick={() => window.location.reload()}>Reload page</button>
				</div>
			</div>
		);
	} else {
		display = (
			<div className='h-100'>
				{hasInfo && (
					<div
						className={`alert alert-${type} alert-dismissible position-absolute alert w-50 start-50 translate-middle`}
						style={{ top: '90px' }}
						role='alert'
					>
						<div>{text}</div>
						<button
							type='button'
							className='btn-close'
							//data-bs-dismiss='alert'
							aria-label='Close'
							onClick={() => dismissInformation('')}
						></button>
					</div>
				)}
				<InfoContext.Provider
					value={{
                        handleInformation: handleInformation,
                        isUnrecoverable: isUnrecoverable
					}}
				>
					{children}
				</InfoContext.Provider>
			</div>
		);
	}
	return display;
}
