import './JobTable.scss';
import { configParameters } from '../jobPage/Parameters';
import React, { useState } from 'react';
import { DataGrid } from '@mui/x-data-grid';
import { DropdownComponent } from '../../global/dropdown/DropdownComponent';
import { ROLE_ADMIN } from '../../context/UserContextProvider';
import { StatusLabel } from '../../global/statusLabel/StatusLabel';
export function JobTable(props) {
	let [selectedIndices, setSelectedIndices] = useState([]);
	let [selectedJobs, setSelectedJobs] = useState([]);
	let [filterUser, setFilterUser] = useState(false);

	let isAdmin = props.user.role === ROLE_ADMIN;

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
	function toggleSelectedJob(jobID) {
		let newSelectedJobs = [...selectedJobs];
		if (selectedJobs.includes(jobID)) {
			// remove job
			let index = selectedJobs.indexOf(jobID);
			newSelectedJobs.splice(index, 1);
			setSelectedJobs(newSelectedJobs);
		} else {
			newSelectedJobs.push(jobID);
			setSelectedJobs(newSelectedJobs);
		}
	}
	function getHeaderButtons(index, name, internalName) {
		return (
			<div className='d-flex flex-row align-items-center  headerRender'>
				<div className='me-auto'>{name}</div>
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
			</div>
		);
	}
	// create columns
	let columns = [
		{
			field: 'id',
			width: 100,
			headerName: 'Job-ID',
		},
	];
	columns = columns.concat(
		filteredConfigParams.map((param) => {
			return {
				field: param.internalName,
				width: param.width,
				renderHeader: (params) => {
					return getHeaderButtons(param.index, param.name, param.internalName);
				},
			};
		})
	);
	columns.push({
		field: 'status',
		width: 200,
		headerName: 'Status',
		renderCell: (param) => {
			return (
				<div className='d-flex justify-content-around statusCell'>
					<StatusLabel status={param.row.status} />
					<input
						className='form-check-intput'
						type='checkbox'
						checked={selectedJobs.includes(param.row.id)}
						onChange={() => toggleSelectedJob(param.row.id)}
						value=''
					></input>
				</div>
			);
		},
	});

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

			row[param.internalName] = value ? value : '';
		});
		return row;
	});

	return (
		<div className='dataGridContainer'>
			<div className='controlBar d-flex flex-row align-items-end'>
				<DropdownComponent
					title={'Pick shown attributes'}
					items={paramDropdownItems}
				></DropdownComponent>
				{isAdmin && (
					<React.Fragment>
						<div className='tableHeaderSpacer'></div>
						<div className='showJobsLabel'>Show all jobs</div>
						<div className='tableHeaderSpacer'></div>
						<input
							className='form-check-input'
							type='checkbox'
							onClick={() => setFilterUser(!filterUser)}
							checked={!filterUser}
							onChange={() => setFilterUser(!filterUser)}
						></input>
						<div className='tableHeaderSpacer'></div>

					</React.Fragment>
				)}
                <div className='tableHeaderSpacer'></div>
						<button className='btn btn-primary' onClick={() => props.refresh()}>
							refresh
						</button>
			</div>
			<div className='dataGridWrapper'>
				<DataGrid
					rows={rows}
					columns={columns}
					disableColumnMenu={true}
					disableExtendRowFullWidth={true}
					disableSelectionOnClick={true}
					onRowClick={(params, event) => {
						if (event.target.type === 'checkbox') {
							return;
						}
						props.setClickedJob(params.id);
					}}
					sx={{
						'& .MuiDataGrid-columnHeader, .MuiDataGrid-cell': {
							borderRight: '1px solid black',
						},
						'& .MuiDataGrid-columnSeparator': {
							display: 'none',
						},
					}}
				/>
			</div>
		</div>
	);
}
