import './InputWithLabel.scss'

export function InputWithLabel(props) {
	return (
		<div className='inputWithLabelContainer d-flex flex-column align-items-start'>
			<label className='form-label' >{props.labelText}</label>
			<input  className='form-control' value={props.value} onChange={props.onChange}></input>
		</div>
	);
}
