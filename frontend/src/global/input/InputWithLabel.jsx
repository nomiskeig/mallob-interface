import './InputWithLabel.scss';

export function InputWithLabel(props) {
	return (
		<div className='inputWithLabelContainer d-flex flex-column align-items-start'>
			<label className='form-label'>{props.labelText}</label>
			<input
				className='form-control'
				disabled={props.disabled ? true : false}
				value={props.value}
				onChange={(e) => props.onChange(e.target.value)}
			></input>
		</div>
	);
}
