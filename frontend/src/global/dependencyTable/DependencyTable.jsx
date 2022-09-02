import './DependencyTable.scss';
export function DependencyTable(props) {

	let rows = props.dependencies.map((job, i) => (
		<tr key={i}>
			<td
				className={`dependencyTableCell ${props.input ? 'dependencyTableFirstColumn' : ''}`}
				align='left'
			>
				{job.config.name}
			</td>
			{props.input && (
				<td className='dependencyTableCell dependencyTableLastColumn'>
					<input
                        data-testid={'dependencyTableCheckbox-' + job.jobID}
						className='form-check-input'
						type='checkbox'
						checked={props.selectedIDs.includes(job.jobID)}
						onChange={() => {
							let newSelectedIDs = props.selectedIDs;
							if (newSelectedIDs.includes(job.jobID)) {
								let index = newSelectedIDs.findIndex(
									(jobID) => jobID === job.jobID
								);
								newSelectedIDs.splice(index, 1);
							} else {
								newSelectedIDs.push(job.jobID);
							}
							props.onChange(newSelectedIDs);
						}}
					></input>
				</td>
			)}
		</tr>
	));

	return (
		<div className='dependencyTable'>
			<table className='table table-sm table-striped'>
				<thead>
					<tr>
						<th
							className={`dependencyTableHeader dependencyTableCell ${
								props.input ? 'dependencyTableFirstColumn' : ''
							}`}
							style={{ textAlign: 'left' }}
						>
							{'Name'}
						</th>
						{props.input && (
							<th className='dependencyTableHeader dependencyTableCell dependencyTableLastColumn'>
								{'Dependencies'}
							</th>
						)}
					</tr>
				</thead>
				<tbody>{rows}</tbody>
			</table>
		</div>
	);
}
