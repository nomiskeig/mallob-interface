import './JobTable.scss';
import { configParameters } from '../jobPage/Parameters';
import { useState, useEffect } from 'react';
import { DataGrid } from '@mui/x-data-grid';
export function JobTable(props) {
	let [rows, setRows] = useState([]);
	let [columns, setColumns] = useState([]);
	let [selectedIndices, setSelectedIndices] = useState([
		0, 1, 2, 3, 4, 5, 6, 7,
	]);
	let [filterUser, setFilterUser] = useState(false);

	let filteredConfigParams = configParameters.filter((param, index) => {
		if (!selectedIndices.includes(index)) {
			return false;
		}
		return true;
	});
	console.log({ filteredConfigParams });
	useEffect(() => {
		console.log('setting columsn');
		let columns = [{ field: 'id', headerName: 'Job-ID', width: 100 }];
		columns = columns.concat(
			filteredConfigParams.map((param) => {
				return {
					field: param.internalName,
					headerName: param.name,
					width: param.width,
				};
			})
		);
		setColumns(columns);
	}, []);

	useEffect(() => {
		if (!props.jobs) {
			return;
		}
		let jobs = props.jobs;
		let rows = jobs.map((job) => {
			let row = { id: job.jobID };
			filteredConfigParams.forEach((param) => {
				let value = job;
				param.path.forEach((path) => {
					value = value[path];
					if (!value) {
						return;
					}
				});

				console.log(value);

				row[param.internalName] = value ? value : '';
			});
			return row;
		});
		console.log({ rows, columns });
		setRows(rows);
	}, [props.jobs]);

	return (
		<div className='dataGridContainer'>
            <div className='controlBar d-flex flex-row'>
                <span>Show all jobs</span>
            </div>
			<div className='dataGridWrapper'>
				<DataGrid rows={rows} columns={columns} />
			</div>
		</div>
	);
}
