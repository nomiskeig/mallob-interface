import './DependencyTable.scss'

export function DependencyTable(props) {
	let rows = props.dependencies.map((job, i) => (
		<tr key={i}>
			<td className='dependencyTableRow' align='left'>{job.config.name}</td>
		</tr>
	));

	return (
		<div className='dependencyTable'>
			<table className='table table-sm table-striped'>
				<thead>
					<tr>
						<th className='dependencyTableHeader dependencyTableRow' style={{textAlign: 'left'}} >{'Name'}</th>
					</tr>
				</thead>
				<tbody>{rows}</tbody>
			</table>
		</div>
	);
}
