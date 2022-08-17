import React, { useState, useEffect, useContext } from 'react';
import { JobContext } from '../../context/JobContextProvider';
import { UserContext } from '../../context/UserContextProvider';
import { DependencyTable } from '../../global/dependencyTable/DependencyTable';
import { InputWithLabel } from '../../global/input/InputWithLabel';
import { TextFieldDescription } from '../../global/description/TextFieldDescription';
import { Header } from '../../global/header/Header';
import { JobPageButton } from '../../global/buttons/JobPageButton';
import {Button} from '../../global/buttons/Button'
import { useParams } from 'react-router-dom';
import { configParameters, getIndexByParam } from './Parameters';
import {
	JOB_STATUS_DONE,
	JOB_STATUS_INPROGRESS,
	JOB_STATUS_CANCELLED,
	StatusLabel,
} from '../../global/statusLabel/StatusLabel';
import './JobPage.scss';
import axios from 'axios';
import { InfoContext, TYPE_INFO } from '../../context/InfoContextProvider';
function getStatus(job) {
	let status;
	switch (job.status) {
		case 'done':
			status = JOB_STATUS_DONE;
			break;
		case 'inProgress':
			status = JOB_STATUS_INPROGRESS;
			break;

		case 'cancelled':
			status = JOB_STATUS_CANCELLED;
			break;
		default:
			status = 'undefined';
			break;
	}
	return status;
}
export function JobPage(props) {
	let { jobID } = useParams();
	if (jobID === undefined) {
		jobID = props.jobID;
	}
	let embedded = props.embedded ? true : false;
	let jobContext = useContext(JobContext);
	let userContext = useContext(UserContext);
	let infoContext = useContext(InfoContext);
	let [loaded, setLoaded] = useState(false);
	let [loadedDependencies, setLoadedDependencies] = useState(false);
	let [descriptionDisplay, setDescriptionDisplay] = useState([]);

	let job = jobContext.jobs.find((job) => job.jobID == jobID);
	useEffect(() => {
		if (!loaded) {
			jobContext.loadSingleJob(jobID);
			setLoaded(true);
		}
		if (!loadedDependencies) {
			if (job && job.config.dependencies) {
				job.config.dependencies.forEach((dep) => jobContext.loadSingleJob(dep));
				setLoadedDependencies(true);
			}
		}
	}, [loaded, loadedDependencies, jobContext, jobID, job]);

	// load description
	useEffect(() => {
		if (!loaded) {
			return;
		}

		if (job.user !== userContext.user.username) {
			setDescriptionDisplay([]);
			return;
		}
		axios({
			method: 'get',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/jobs/description/single/' +
				jobID,
			responseType: 'blob',
		})
			.then((res) => {
				if (res.headers['content-type'].startsWith('application/json')) {
					// convert the blob to json so we can display the description
					let fr = new FileReader();
					fr.onload = function () {
						setDescriptionDisplay(
							<TextFieldDescription
								displayOnly={true}
								descriptions={JSON.parse(fr.result).description}
							></TextFieldDescription>
						);
					};
					fr.readAsText(res.data);
				} else if (res.headers['content-type'].startsWith('application/zip')) {
					// https://stackoverflow.com/questions/41938718/how-to-download-files-using-axios
					let url = window.URL.createObjectURL(new Blob([res.data]));
					let link = document.createElement('a');
					link.href = url;
					link.setAttribute('download', 'description.zip');
					document.body.appendChild(link);
					setDescriptionDisplay(
						<button className='btn btn-primary' onClick={() => link.click()}>
							Download description
						</button>
					);
				}
			})
			.catch((err) => {
				console.log(err.message);
			});
	}, [jobID, loaded]);

	function downloadResult() {
		axios({
			method: 'get',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/jobs/solution/single/' +
				jobID,
			responseType: 'blob',
		}).then((res) => {
			let url = window.URL.createObjectURL(new Blob([res.data]));
			let link = document.createElement('a');
			link.href = url;
			link.setAttribute('download', 'description.zip');
			document.body.appendChild(link);
			link.click();
		});
	}

	function cancelJob() {
		axios({
			method: 'post',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/jobs/cancel/single/' +
				jobID,
		}).then((res) => {
                console.log(res.data)
			infoContext.handleInformation(
				'Sucessesfully cancelled the job.',
				TYPE_INFO
			);
		});
	}

	let parameterDisplayList = [];
	if (job) {
		configParameters
			.filter((param) => param.showOnJobPage)
			.forEach((param) => {
				let value = job;
				param.path.forEach((path) => {
					value = value[path];
					if (!value) {
						return;
					}
				});
				if (value) {
					parameterDisplayList.push(
						<div key={getIndexByParam(param)} className='singleParamDisplay'>
							<InputWithLabel
								disabled={true}
								value={value}
								labelText={param.name}
							/>
						</div>
					);
				}
			});
	}
	let dependencies = [];
	if (job && job.config.dependencies) {
		job.config.dependencies.forEach((dep) => {
			let depJob = jobContext.jobs.find((job) => job.jobID === dep);
			if (!depJob) {
				return;
			}
			dependencies.push(depJob);
		});
	}

	let name = job ? job.config.name : '';
	let submitted = job ? job.submitTime : '';
	let status = job ? getStatus(job) : null;

	return (
		<div className={embedded ? 'embeddedPageContainer' : 'jobPageContainer'}>
			<div className={embedded ? '' : 'marginContainer'}>
				<div className={embedded ? '' : 'row jobPageRow g-0'}>
					<div className={embedded ? '' : 'col jobPageColumn'}>
						<div
							className={
								embedded
									? ''
									: 'jobPagePanel upperPanel infoPanel d-flex flex-column'
							}
						>
							<Header title={name}>
								{status === JOB_STATUS_INPROGRESS && (
									<Button
										onClick={() => cancelJob()}
										text={'Cancel job'}
									></Button>
								)}
								{status === JOB_STATUS_DONE &&
									job.user === userContext.user.username && (
										<Button
											onClick={() => downloadResult()}
											text={'Download result'}
										></Button>
									)}
								{embedded && <JobPageButton jobID={jobID}></JobPageButton>}
							</Header>
							<div className='infoPanelContainer d-flex flex-row justify-content-between'>
								<div className='parameterDisplay d-flex flex-column flex-wrap align-items-start justify-content-start'>
									{parameterDisplayList}
								</div>
								<div className='submitAndStatus '>
									<InputWithLabel
										disabled={true}
										value={submitted}
										labelText={'Submitted at'}
									/>
									<div className='statusTitle'>Status</div>
									{status && <StatusLabel status={status} />}
								</div>
							</div>
						</div>
					</div>
				</div>
				<div
					className={
						embedded
							? 'd-flex flex-column-reverse '
							: 'lowerPanelContainer row jobPageRow g-0'
					}
				>
					<div className={embedded ? '' : 'col-12 col-md-6'}>
						<div
							className={
								embedded
									? 'embeddedDescriptionContainer'
									: 'jobPagePanel lowerPanel  lowerPanelLeft descriptionPanel d-flex flex-column'
							}
						>
							<Header title={'Description'} />
							{descriptionDisplay}
						</div>
					</div>
					<div className={embedded ? '' : 'col-12 col-md-6'}>
						<div
							className={
								embedded
									? ''
									: 'jobPagePanel lowerPanel lowerPanelRight dependencyPanel'
							}
						>
							<Header title={'Dependencies'} />
							<DependencyTable dependencies={dependencies} />
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
