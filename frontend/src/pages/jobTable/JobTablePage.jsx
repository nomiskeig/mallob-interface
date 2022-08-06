import './JobTablePage.scss';
import { JobTable } from './JobTable';
import { JobContext } from '../../context/JobContextProvider';

import { useEffect, useContext } from 'react';

export function JobTablePage(props) {
	let jobContext = useContext(JobContext);
	useEffect(() => {
		jobContext.fetchMostJobsPossible();
	}, []);
	console.log(jobContext.jobs);
    let jobs = jobContext.jobs;

	return (
		<div className='jobTablePageContainer'>
			<div className='row g-0'>
				<div className='col-12 col-md-6 tableHalf'>
					<div className='jobTablePagePanel'>
						<JobTable jobs={jobs}></JobTable>
					</div>
				</div>
				<div className='col-12 col-md-6 jobPageHalf'>
					<div className='jobTablePagePanel'></div>
				</div>
			</div>
		</div>
	);
}
