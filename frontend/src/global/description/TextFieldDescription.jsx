import { useState } from 'react';

import './TextFieldDescritption.scss'
export function TextFieldDescription(props) {
	let [descriptions, setDescriptions] = useState(['']);
	let [currentDescription, setCurrentDescription] = useState(0);

	function setSpecificDescription(index, newValue) {
		let newDescriptions = [...descriptions];
		newDescriptions[index] = newValue;
		setDescriptions(newDescriptions);
        props.setDescriptions(newDescriptions)
	}
    function addDescription() {
        let newDescriptions = [...descriptions];
        newDescriptions.push('');
        setCurrentDescription(newDescriptions.length -1);
        setDescriptions(newDescriptions);
        
    }
	let textFieldPickerButtons = descriptions.map((description, index) => {
		return (
			<button
				className={`btn ${
					index === currentDescription
						? 'btn-primary disabled'
						: 'btn-secondary'
				}`}
				onClick={() => setCurrentDescription(index)}
			>
				{index + 1}
			</button>
		);
	});
	return (
		<div className='textFieldDescriptionContainer d-flex flex-column'>
			<div className='textFieldPickerButtons d-flex flex-wrap'>
				{textFieldPickerButtons}
				<button className='btn btn-secondary' onClick={() => addDescription()}>
					<svg
						xmlns='http://www.w3.org/2000/svg'
						width='16'
						height='16'
						fill='black'
						className='bi bi-plus'
						viewBox='3 3 10 10'
					>
						<path d='M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z' />
					</svg>
				</button>
			</div>
			<textarea
                className='textFieldTextArea'
				value={descriptions[currentDescription]}
				onChange={(e) =>
					setSpecificDescription(currentDescription, e.target.value)
				}
			></textarea>
		</div>
	);
}
