import './JobTable.scss';
import { configParameters } from '../jobPage/Parameters';
import React, { useState, useEffect } from 'react';
import { DataGrid } from '@mui/x-data-grid';
import { DropdownComponent } from '../../global/dropdown/DropdownComponent';
import { ROLE_ADMIN } from '../../context/UserContextProvider';
export function JobTable(props) {
	let [rows, setRows] = useState([]);
	let [columns, setColumns] = useState([]);
	let [selectedIndices, setSelectedIndices] = useState([]);
	let [filterUser, setFilterUser] = useState(false);

	let isAdmin = props.user.role == ROLE_ADMIN;

	let filteredConfigParams = configParameters.filter((param) => {
		if (!selectedIndices.includes(param.index)) {
			return false;
		}
		return true;
	});
	let possibleParams = configParameters.filter((param) => {
		if (selectedIndices.includes(param.index)) {
			return false;
		}
		return true;
	});
	let paramDropdownItems = possibleParams.map((param) => {
		return {
			onClick: () => {
				let newIndices = selectedIndices;
				newIndices.push(param.index);
				setSelectedIndices([...selectedIndices, param.index]);
			},
			name: param.name,
		};
	});
	function getHeaderButtons(index, name, internalName) {
		return (
			<div className='d-flex flex-row align-items-center  headerRender'>
				<div className='me-auto'>{name}</div>
				{index && (
					<React.Fragment>
						<div disabled className='removeButton'>
							<svg
								onClick={() =>
									setSelectedIndices(
										selectedIndices.filter((indexFromArray) => {
											if (index === indexFromArray) {
												return false;
											}
											return true;
										})
									)
								}
								xmlns='http://www.w3.org/2000/svg'
								width='16'
								height='16'
								fill='currentColor'
								className='bi bi-x-circle removeButtonSVG'
								viewBox='0 0 16 16'
							>
								<path d='M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z' />
								<path d='M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z' />
							</svg>
						</div>
						<div className='tableHeaderSpacer'></div>
					</React.Fragment>
				)}
			</div>
		);
	}
	console.log({ filteredConfigParams });
	useEffect(() => {
		let columns = [
			{
				field: 'id',
				width: 100,
				renderHeader: () => {
					return getHeaderButtons(null, 'Job-ID', 'id');
				},
			},
		];
		columns = columns.concat(
			filteredConfigParams.map((param) => {
				return {
					field: param.internalName,
					width: param.width,
					renderHeader: (params) => {
						return getHeaderButtons(
							param.index,
							param.name,
							param.internalName
						);
					},
				};
			})
		);
		columns.push({
			field: 'status',
			headerName: 'Status',
			width: 200,
		});

		setColumns(columns);
	}, [selectedIndices]);

	useEffect(() => {
		// calculate rows
		if (!props.jobs) {
			return;
		}
		let jobs = props.jobs;
		if (filterUser) {
			jobs = jobs.filter((job) => {
				if (job.user === props.user.username) {
					return true;
				}
				return false;
			});
		}
		let rows = jobs.map((job) => {
			let row = { id: job.jobID, status: job.status };
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
	}, [props.jobs, selectedIndices, filterUser]);

	return (
		<div className='dataGridContainer'>
			<div className='controlBar d-flex flex-row align-content-end'>
				<DropdownComponent
					title={'Pick shown attributes'}
					items={paramDropdownItems}
				></DropdownComponent>
				{isAdmin && (
					<React.Fragment>
						<div className='showJobsLabel'>Show all jobs</div>
						<input
							className='form-check-input'
							type='checkbox'
							onClick={() => setFilterUser(!filterUser)}
							checked={!filterUser}
							onChange={() => setFilterUser(!filterUser)}
						></input>
					</React.Fragment>
				)}
			</div>
			<div className='dataGridWrapper'>
				<DataGrid
					rows={rows}
					columns={columns}
					disableColumnMenu={true}
					disableExtendRowFullWidth={true}
				/>
			</div>
		</div>
	);
}
