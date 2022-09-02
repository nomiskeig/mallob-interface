import './JobTablePage.scss';
import { JobTable } from './JobTable';
import { JobPage } from '../jobPage/JobPage';
import { JobContext } from '../../context/JobContextProvider';

import { useEffect, useContext, useState } from 'react';

export function JobTablePage(props) {
	let jobContext = useContext(JobContext);

	useEffect(() => {
		jobContext.fetchMostJobsPossible();
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);
	let jobs = jobContext.jobs;
	let [displayedJobID, setDisplayedJobID] = useState(null);

	return (
		<div className='jobTablePageContainer'>
			<div className='row g-0 jobTablePageRow'>
				<div className='col-12 col-md-6 tableHalf jobTablePageCol'>
					<div className='jobTablePagePanel'>
						<JobTable
							jobs={jobs}
							setClickedJob={(id) => {
								setDisplayedJobID(id);
							}}
							refresh={() => jobContext.loadAllJobsOfUser()}
						></JobTable>
					</div>
				</div>
				<div className='col-12 col-md-6 jobPageHalf jobTablePageCol'>
					<div className='jobTablePagePanel jobPageHalfPanel'>
						{displayedJobID !== null && (
							<JobPage embedded={true} jobID={displayedJobID}></JobPage>
						)}
					</div>
				</div>
			</div>
		</div>
	);
}
