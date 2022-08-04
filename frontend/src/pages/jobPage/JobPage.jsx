import React, { useState, useEffect, useContext } from 'react';
import { JobContext } from '../../context/JobContextProvider';
import { DependencyTable } from '../../global/dependencyTable/DependencyTable';
import { InputWithLabel } from '../../global/input/InputWithLabel';
import { Header } from '../../global/header/Header';
import { useParams } from 'react-router-dom';
import { configParameters } from './Parameters';
import {
	JOB_STATUS_DONE,
	JOB_STATUS_INPROGRESS,
	JOB_STATUS_CANCELLED,
	StatusLabel,
} from '../../global/statusLabel/StatusLabel';
import './JobPage.scss';
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
	}
	return status;
}
export function JobPage(props) {
	let { jobID } = useParams();
	let jobContext = useContext(JobContext);
	//	let [job, setJob] = useState(null);
	let [loaded, setLoaded] = useState(false);
	let [loadedDependencies, setLoadedDependencies] = useState(false);

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
    


	let parameterDisplayList = configParameters.map((param) => {
		if (!job) {
			return;
		}
		let value = job;
		param.path.forEach((path) => {
			value = value[path];
			if (!value) {
				return;
			}
		});
		if (value) {
			return (
				<div className='singleParamDisplay'>
					<InputWithLabel value={value} labelText={param.name} />
				</div>
			);
		}
	});

	let dependencies = [];
	if (job && job.config.dependencies) {
		job.config.dependencies.forEach((dep) => {
			let depJob = jobContext.jobs.find((job) => job.jobID == dep);
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
		<div className='jobPageContainer'>
			<div className='marginContainer'>
				<div className='row jobPageRow g-0'>
					<div className='col jobPageColumn'>
						<div className='panel upperPanel infoPanel d-flex flex-column'>
							<Header title={name}></Header>
							<div className='infoPanelContainer d-flex flex-row justify-content-between'>
								<div className='parameterDisplay d-flex flex-column flex-wrap align-items-start justify-content-start'>
									{parameterDisplayList}
								</div>
								<div className='submitAndStatus '>
									<InputWithLabel
										value={submitted}
										labelText={'Submitted at'}
									/>
									<div className='statusTitle'>Status</div>
                                    {status && <StatusLabel status={status}/>}
								</div>
							</div>
						</div>
					</div>
				</div>
				<div className='lowerPanelContainer row jobPageRow g-0'>
					<div className='col-12 col-md-6'>
						<div className='panel lowerPanel  lowerPanelLeft descriptionPanel'></div>
					</div>
					<div className='col-12 col-md-6'>
						<div className='panel lowerPanel lowerPanelRight dependencyPanel'>
							<DependencyTable dependencies={dependencies} />
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
