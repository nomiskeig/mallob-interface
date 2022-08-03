import React, { useState, useEffect, useContext } from 'react';
import { JobContext } from '../../context/JobContextProvider';
import { DependencyTable } from '../../global/dependencyTable/DependencyTable';
import { useParams } from 'react-router-dom';
import './JobPage.scss';
export function JobPage(props) {
	let { jobID } = useParams();
	let jobContext = useContext(JobContext);
	//	let [job, setJob] = useState(null);
	let [loaded, setLoaded] = useState(false);
	let [loadedDependencies, setLoadedDependencies] = useState(false);

	let job;
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
	});

	job = jobContext.jobs.find((job) => job.jobID == jobID);
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
	return (
		<div className='jobPageContainer'>
			<div className='marginContainer'>
				<div className='row g-0'>
					<div className='panel upperPanel infoPanel col' />
				</div>
				<div className='lowerPanelContainer row g-0'>
					<div className='col-12 col-md-6'>
						<div className='panel lowerPanel descriptionPanel'></div>
					</div>
					<div className='col-12 col-md-6'>
						<div className='panel lowerPanel dependencyPanel'>
							<DependencyTable dependencies={dependencies} />
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
