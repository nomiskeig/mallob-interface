import React, { useContext, useState, useEffect, createContext } from 'react';
import { ROLE_ADMIN, ROLE_USER, UserContext } from './UserContextProvider';
import { InfoContext, TYPE_ERROR } from './InfoContextProvider';
import axios from 'axios';

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
		if (userContext.user.role == ROLE_USER) {
			apiAddress = '/api/v1/jobs/info/all';
		} else if (userContext.user.role == ROLE_ADMIN) {
			console.log('address is global');
			apiAddress = '/api/v1/jobs/info/global';
		}
		axios({
			method: 'get',
			url: process.env.REACT_APP_API_BASE_PATH + apiAddress,
			headers: {
				'Authorization': 'Bearer ' + userContext.user.token,
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
							'Authorization': 'Bearer ' + userContext.user.token,
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
