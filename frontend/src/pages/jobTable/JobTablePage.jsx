import './JobTablePage.scss';
import { JobTable } from './JobTable';
import { JobContext } from '../../context/JobContextProvider';
import {UserContext} from '../../context/UserContextProvider';

import { useEffect, useContext } from 'react';

export function JobTablePage(props) {
	let jobContext = useContext(JobContext);
    let userContext = useContext(UserContext);
    console.log(userContext)
	useEffect(() => {
		jobContext.fetchMostJobsPossible();
	}, []);
    let jobs = jobContext.jobs;

	return (
		<div className='jobTablePageContainer'>
			<div className='row g-0'>
				<div className='col-12 col-md-6 tableHalf'>
					<div className='jobTablePagePanel'>
						<JobTable jobs={jobs} user={userContext.user}></JobTable>
					</div>
				</div>
				<div className='col-12 col-md-6 jobPageHalf'>
					<div className='jobTablePagePanel'></div>
				</div>
			</div>
		</div>
	);
}
