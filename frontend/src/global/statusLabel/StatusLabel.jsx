import './StatusLabel.scss';

export const JOB_STATUS_DONE = 'done';
export const JOB_STATUS_INPROGRESS = 'inProgress';
export const JOB_STATUS_CANCELLED = 'cancelled';

export function StatusLabel(props) {
	let color;
	let text;
    if (props.status === null) {return null}
	switch (props.status) {
		case JOB_STATUS_DONE:
			color = '#198754';
			text = 'done';
			break;
		case JOB_STATUS_CANCELLED:
			color = '#dc3545';
			text = 'cancelled';
			break;
		case JOB_STATUS_INPROGRESS:
			color = '#0d6efd';
			text = 'in progress';
			break;
        default:
            color = 'red';
            text= 'undefined'
	}
	return (
		<div className='statusLabel' style={{ backgroundColor: color }}>
            <div className='statusLabelText'>
			{text}
            </div>
		</div>
	);
}
