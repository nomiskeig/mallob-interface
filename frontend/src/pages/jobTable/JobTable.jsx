import './JobTable.scss';
import { configParameters } from '../jobPage/Parameters';
import React, { useState, useContext } from 'react';
import axios from 'axios';
import { DataGrid } from '@mui/x-data-grid';
import { DropdownComponent } from '../../global/dropdown/DropdownComponent';
import { ROLE_ADMIN } from '../../context/UserContextProvider';
import { StatusLabel } from '../../global/statusLabel/StatusLabel';
import { UserContext } from '../../context/UserContextProvider';
import { InfoContext, TYPE_INFO, TYPE_WARNING } from '../../context/InfoContextProvider';
export function JobTable(props) {
	let [selectedIndices, setSelectedIndices] = useState([]);
	let [selectedJobs, setSelectedJobs] = useState([]);
	let [filterUser, setFilterUser] = useState(false);
	let [canCancelJobs, setCanCancelJobs] = useState(false);
    let [canDownloadResults, setCanDownloadsResults] = useState(false);

	let userContext = useContext(UserContext);
	let infoContext = useContext(InfoContext);

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
		updateActionPossibilities(newSelectedJobs);
	}
	function updateActionPossibilities(selectedJobs) {
		if (selectedJobs.length === 0) {
			setCanCancelJobs(false);
            setCanDownloadsResults(false);
			return;
		}
        setCanDownloadsResults(true);

		setCanCancelJobs(true);
	}

	function cancelSelectedJobs() {
        let jobsToCancel = selectedJobs;
		axios({
			method: 'post',
			url: process.env.REACT_APP_API_BASE_PATH + '/api/v1/jobs/cancel',
			data: {
				jobs: [...jobsToCancel],
			},
			headers: {
				Authorization: 'Bearer ' + userContext.user.token,
			},
		})
			.then((res) => {
                console.log(res.data)
                if (res.data.cancelled.length === jobsToCancel.length ) {
				infoContext.handleInformation(
					'Successfully cancelled the selected jobs.',
					TYPE_INFO
				);
                } else {
                    let notCancelled = jobsToCancel.filter((id) => !res.data.cancelled.includes(id));
                    let message = 'Successfully cancelled some of the selected jobs. \nThe jobs with the following IDs could not be cancelled: '
                    notCancelled.forEach((id) => message += id + ', ');
                    message = message.slice(0, message.length-2);
                    infoContext.handleInformation(message, TYPE_WARNING);
                }

			})
			.catch((e) => {
				console.log(e);
			});
	}
	function downloadSelectedResults() {
		axios({
			method: 'get',
			url: process.env.REACT_APP_API_BASE_PATH + '/api/v1/jobs/solution',
			params: {
				jobs: [...selectedJobs],
			},
			headers: {
				Authorization: 'Bearer ' + userContext.user.token,
			},
            responseType: 'blob'
		}).then((res) => {
            let url = window.URL.createObjectURL(new Blob([res.data]));
                let link = document.createElement('a');
                link.href= url;
                link.setAttribute('download', 'descriptions.zip');
                document.body.appendChild(link);
                link.click();
            });
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
				if (!value) {
					return;
				}
				value = value[path];
			});

			row[param.internalName] = value ? param.transformOutput(value) : '';
		});
		return row;
	});

	return (
		<div className='dataGridContainer'>
			<div className='controlBar d-flex flex-row align-items-end justify-content-between'>
				<DropdownComponent
					title={'Pick shown attributes'}
					items={paramDropdownItems}
				></DropdownComponent>
				{isAdmin && (
					<div className='d-flex flex-row'>
						<div className='showJobsLabel'>Show all jobs</div>
						<div className='tableHeaderSpacer'></div>
						<input
							className='form-check-input'
							type='checkbox'
							onClick={() => setFilterUser(!filterUser)}
							checked={!filterUser}
							onChange={() => setFilterUser(!filterUser)}
						></input>
					</div>
				)}
				<button className='btn btn-primary' onClick={() => props.refresh()}>
					refresh
				</button>
				<DropdownComponent
					title={'Choose action'}
					items={[
						{
							name: 'Download results of selected Jobs',
							onClick: () => downloadSelectedResults(),
                            disabled: !canDownloadResults
						},
						{
							name: 'Cancel selected jobs',
							onClick: () => cancelSelectedJobs(),
							disabled: !canCancelJobs,
						},
					]}
				></DropdownComponent>
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
