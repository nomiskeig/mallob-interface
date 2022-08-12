import React, { useContext, createContext, useReducer } from 'react';
import { ROLE_ADMIN, ROLE_USER, UserContext } from './UserContextProvider';
import { InfoContext, TYPE_ERROR } from './InfoContextProvider';
import axios from 'axios';
const testJob = {
	config: {
		name: 'jobName',
		priority: 4.3,
		application: 'SAT',
		maxDemand: 100,
		wallclockLimit: '100s',
		cpuLimit: '100ms',
		arrival: new Date().toISOString(),
		dependencies: [2, 3, 4],
		incremental: true,
		precursor: 4,
		contentMode: 'text',
	},
	jobID: 1,
	submitTime: new Date().toISOString(),
	status: 'inProgress',
	resultData: {
		time: {
			parsing: 0.72,
			processing: 2.67,
			scheduling: 0.0023,
			total: 3.4,
		},
		usedCpuSecods: 5.34,
		usedWallclockSeconds: 5.8,
	},
};
const dep1Job = {
	config: {
		name: 'dep1name',
	},
	jobID: 2,
    status: 'inProgress'
};

const dep2Job = {
	config: {
		name: 'dep2name',
	},
	jobID: 3,
};
const dep3Job = {
	config: {
		name: 'dep3name',
	},
	jobID: 4,
};

function reducer(jobs, action) {
	switch (action.type) {
		case 'addJob':
			return [...jobs, action.newJob];
		case 'addOrReplaceJob':
			let index = jobs.findIndex((job) => job.jobID === action.newJob.jobID);
			console.log('index from addOrReplace', index);
			if (index !== undefined) {
				let newJobs = [...jobs];
				newJobs.splice(index, 1, action.newJob);
				console.log(newJobs);
				return newJobs;
			}
			return [...jobs, action.newJob];
		case 'setJobs':
			// TODO: maybe keep jobs wich  are not in new jobs
			return action.jobs;
        default: return jobs
	}
}
export const JobContext = createContext({});
export function JobContextProvider({ children }) {
	let userContext = useContext(UserContext);
	let infoContext = useContext(InfoContext);
	//	const [jobs, setJobs] = useState([]);

	const [jobs, dispatch] = useReducer(reducer, []);

	async function fetchJobs() {
		await axios
			.get('/api/v1/jobs/all')
			.then((res) => {}) // setJobs(res))
			.catch((err) => console.log(err));
	}

	function fetchMostJobsPossible(restrictToOwnJobs) {
		let apiAddress;
		if (userContext.user.role === ROLE_USER || restrictToOwnJobs) {
			apiAddress = '/api/v1/jobs/info/all';
		} else if (userContext.user.role === ROLE_ADMIN) {
			apiAddress = '/api/v1/jobs/info/global';
		}
		axios({
			method: 'get',
			url: process.env.REACT_APP_API_BASE_PATH + apiAddress,
			headers: {
				Authorization: 'Bearer ' + userContext.user.token,
			},
		})
			.then((res) => {
				dispatch({ type: 'setJobs', jobs: res.data.information });
			})
			.catch((err) =>
				infoContext.handleInformation(
					'Could not load any of the jobs.',
					TYPE_ERROR
				)
			);
	}
	function loadAllJobsOfUser() {
		console.log('load all jobs of user');
		this.fetchMostJobsPossible(true);
	}

	// loads a single job from the backend and stores it into state;
	// replaces the information in state if its currently in there
	// TODO: does not actually replace the job if its there
	function loadSingleJob(jobID) {
		if (process.env.NODE_ENV === 'development') {
			if (jobID === 1) {
				dispatch({ type: 'addOrReplaceJob', newJob: testJob });
				return;
			}
			if (jobID === 2) {
				dispatch({ type: 'addOrReplaceJob', newJob: dep1Job });
				return;
			}
			if (jobID === 3) {
				dispatch({ type: 'addOrReplaceJob', newJob: dep2Job });
				return;
			}
			if (jobID === 4) {
				dispatch({ type: 'addOrReplaceJob', newJob: dep3Job });
				return;
			}
		}
	}
	// returns a promise which resolves to the job information
	function getSingleJobInfo(jobID) {
		return new Promise(function (resolve, reject) {
			if (jobs !== undefined) {
				let job = jobs.find((job) => job.jobID === jobID);
				if (job !== undefined) {
					resolve(job);
				} else {
					axios({
						method: 'get',
						url:
							process.env.REACT_APP_API_BASE_PATH +
							'/api/v1/jobs/info/single/' +
							jobID,
						headers: {
							Authorization: 'Bearer ' + userContext.user.token,
						},
					})
						.then((res) => {
							dispatch({ type: 'addJob', newJob: res.data });
							resolve(res.data);
						})
						.catch((res) => reject(null));
				}
			}
		});
	}

	return (
		<JobContext.Provider
			value={{
				jobs: jobs,
				fetchJobs: fetchJobs,
				fetchMostJobsPossible: fetchMostJobsPossible,
				getSingleJobInfo: getSingleJobInfo,
				loadSingleJob: loadSingleJob,
				loadAllJobsOfUser: loadAllJobsOfUser,
			}}
		>
			{children}
		</JobContext.Provider>
	);
}
