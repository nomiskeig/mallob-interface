import React, { useContext, createContext, useReducer } from 'react';
import { ROLE_ADMIN, ROLE_USER, UserContext } from './UserContextProvider';
import { InfoContext, TYPE_ERROR } from './InfoContextProvider';
import axios from 'axios';

function reducer(jobs, action) {
	switch (action.type) {
		case 'addJob':
			return [...jobs, action.newJob];
		case 'addOrReplaceJob':
			let index = jobs.findIndex((job) => job.jobID === action.newJob.jobID);
			if (index !== -1) {
				let newJobs = [...jobs];
				newJobs.splice(index, 1, action.newJob);
				return newJobs;
			}
			return [...jobs, action.newJob];
		case 'setJobs':
			// TODO: maybe keep jobs wich  are not in new jobs
			return action.jobs;
		default:
			return jobs;
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

	function fetchMostJobsPossible(restrictToOwnJobs, callback) {
        console.log(userContext.user.token)
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
				if (callback) {
					callback(res.data.information);
				}
			})
			.catch((err) => {
				console.log(err);
				infoContext.handleInformation(
					'Could not load any of the jobs.',
					TYPE_ERROR
				);
			});
	}
	function loadAllJobsOfUser() {
		this.fetchMostJobsPossible(true);
	}

	// loads a single job from the backend and stores it into state;
	// replaces the information in state if its currently in there
	function loadSingleJob(jobID) {
		axios({
			method: 'get',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/jobs/info/single/' +
				jobID,
			headers: {
				Authorization: 'Bearer ' + userContext.user.token,
			},
		}).then((res) => {
			dispatch({ type: 'addOrReplaceJob', newJob: res.data });
		});
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
