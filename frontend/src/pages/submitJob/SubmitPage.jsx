import React, { useState, useEffect, useContext } from 'react';
import './SubmitPage.scss';

import { DependencyTable } from '../../global/dependencyTable/DependencyTable';
import { InputWithLabel } from '../../global/input/InputWithLabel';
import { Header } from '../../global/header/Header';
import { JobContext } from '../../context/JobContextProvider';
import { UserContext } from '../../context/UserContextProvider';

function submitJob() {

}
export function SubmitPage(props) {
	let jobContext = useContext(JobContext);
	let userContext = useContext(UserContext);
	let selectedJobIDs = [];
	useEffect(() => {
		jobContext.loadAllJobsOfUser();
	}, []);
	let ownJobs = jobContext.jobs.filter(
		(job) => job.user === userContext.user.username
	);
	return (
		<div className='submitPageContainer'>
			<div className='marginContainer'>
				<div className='row g-0 submitPageRow'>
					<div className='col'>
						<div className='submitPagePanel d-flex flex-row upperPanel'>
							<div className='requiredParamsContainer'>
								<Header title={'Requried'} />
							</div>
							<div className='optionalParamsContainer'>
								<Header title={'Optional'} />
							</div>
						</div>
					</div>
				</div>
				<div className='row g-0 submitPageRow'>
					<div className='col-12 col-md-6'>
						<div className='submitPagePanel lowerPanel lowerPanelLeft'>
							<Header title={'Description'} />
						</div>
					</div>
					<div className='col-12 col-md-6'>
						<div className='submitPagePanel lowerPanel lowerPanelRight'>
							<Header title={'Dependencies'} />
							<DependencyTable
								dependencies={ownJobs}
								input={true}
								onChange={(selectedJobIDs) => {
									selectedJobIDs = selectedJobIDs;
								}}
							/>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
