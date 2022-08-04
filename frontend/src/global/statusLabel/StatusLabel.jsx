import './StatusLabel.scss';

export const JOB_STATUS_DONE = 'done';
export const JOB_STATUS_INPROGRESS = 'inProgress';
export const JOB_STATUS_CANCELLED = 'cancelled';

export function StatusLabel(props) {
	let color;
	let text;
	switch (props.status) {
		case JOB_STATUS_DONE:
            console.log('done')
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
	}
	return (
		<div className='statusLabel' style={{ backgroundColor: color }}>
			{text}
		</div>
	);
}
