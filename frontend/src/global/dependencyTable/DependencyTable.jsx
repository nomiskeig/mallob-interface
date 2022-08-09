import './DependencyTable.scss';
import React, {useState} from 'react'

export function DependencyTable(props) {
    let [selectedIDs, setSelectedIDs] = useState([])

	let rows = props.dependencies.map((job, i) => (
		<tr key={i}>
			<td
				className='dependencyTableCell dependencyTableFirstColumn'
				align='left'
			>
				{job.config.name}
			</td>
			{props.input && (
				<td className='dependencyTableCell dependencyTableLastColumn'>
					<input className='form-check-input' type='checkbox' value={selectedIDs.includes(job.jobID)} onClick={() => {
                        let newSelectedIDs = selectedIDs;
                        console.log(job)
                        if (newSelectedIDs.includes(job.jobID)) {
                            let index = newSelectedIDs.findIndex((jobID) => jobID === job.jobID);
                            newSelectedIDs.splice(index, 1);
                        } else {
                            newSelectedIDs.push(job.jobID);
                        }
                        setSelectedIDs(newSelectedIDs);
                        props.onChange(newSelectedIDs);

                    }}></input>
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
							className='dependencyTableHeader dependencyTableCell dependencyTableFirstColumn'
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
