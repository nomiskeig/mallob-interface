export function DependencyTable(props) {
	let rows = props.dependencies.map((job, i) => (
		<tr key={i}>
			<td align='left'>{job.config.name}</td>
		</tr>
	));

	return (
		<div className='dependencyTable'>
			<table className='table table-sm table-striped'>
				<thead>
					<tr>
						<th style={{textAlign: 'left'}} >{'Name'}</th>
					</tr>
				</thead>
				<tbody>{rows}</tbody>
			</table>
		</div>
	);
}
