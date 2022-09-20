import './InputWithLabel.scss';

export function InputWithLabel(props) {
	return (
		<div data-testid={props.dataTestID} className='inputWithLabelContainer d-flex flex-column align-items-start'>
			<label className='inputWithLabelLabel'>{props.labelText}</label>
			<input
                data-testid={'input-' + props.labelText}
				className='form-control'
                type={props.datetime ? 'datetime-local': 'text'}
				disabled={props.disabled ? true : false}
				value={props.value !== undefined ? props.value : ''}
				onChange={(e) => props.onChange(e.target.value)}
			></input>
		</div>
	);
}
