import './JobTablePage.scss';
import { JobTable } from './JobTable';
import {JobPage} from '../jobPage/JobPage'
import { JobContext } from '../../context/JobContextProvider';
import { UserContext } from '../../context/UserContextProvider';

import { useEffect, useContext, useState } from 'react';

export function JobTablePage(props) {
	let jobContext = useContext(JobContext);
	let userContext = useContext(UserContext);
    console.log('should update')
    console.log(jobContext.jobs)

	useEffect(() => {
        console.log('fetching')

		jobContext.fetchMostJobsPossible();
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
							user={userContext.user}
							setClickedJob={(id) => {
                                console.log(id);
								setDisplayedJobID(id);
							}}
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
