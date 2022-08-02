import React, { useContext, useState, createContext } from 'react';
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
		dependencies: [2, 3],
		incremental: true,
		precursor: 4,
		contentMode: text,
	},
	jobID: jobID,
	submitTime: new Date().toISOString(),
	status: 'done',
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
};

const dep2Job = {
	config: {
		name: 'dep2name',
	},
	jobID: 3,
};
export const JobContext = createContext({});
export function JobContextProvider({ children }) {
	let userContext = useContext(UserContext);
	let infoContext = useContext(InfoContext);
	const [jobs, setJobs] = useState([]);

	async function fetchJobs() {
		await axios
			.get('/api/v1/jobs/all')
			.then((res) => setJobs(res))
			.catch((err) => console.log(err));
	}

	function fetchMostJobsPossible() {
		let apiAddress;
		if (userContext.user.role === ROLE_USER) {
			apiAddress = '/api/v1/jobs/info/all';
		} else if (userContext.user.role === ROLE_ADMIN) {
			console.log('address is global');
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
				setJobs(res.data.information);
				console.log(jobs);
				console.log(res.data.information);
			})
			.catch((err) =>
				infoContext.handleInformation(
					'Could not load any of the jobs.',
					TYPE_ERROR
				)
			);
	}

	// loads a single job from the backend and stores it into state;
	// replaces the information in state if its currently in there
	// TODO: does not actually replace the job if its there
	function loadSingleJob(jobID) {
		if (process.env.NODE_ENV === 'development') {
			if (jobID === 1) {
				setJobs([...jobs, testJob]);
			}
			if (jobID === 2) {
				setJobs([...jobs, dep1Job]);
			}
			if (jobID === 3) {
				setJobs([...jobs, dep2Job]);
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
							jobs.push(res.data);
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
			}}
		>
			{children}
		</JobContext.Provider>
	);
}
