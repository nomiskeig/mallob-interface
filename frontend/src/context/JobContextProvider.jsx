        import React, { useContext, useState, useEffect, createContext } from 'react';
import axios from 'axios';

export const JobContext = createContext({});
export function JobContextProvider({ children }) {
	const [jobs, setJobs] = useState([]);

	async function fetchJobs() {
		await axios
			.get('/api/v1/jobs/all')
			.then((res) => setJobs(res))
			.catch((err) => console.log(err));
	}

	return (
		<JobContext.Provider value={{ jobs: jobs, fetchJobs: fetchJobs }}>
			{children}
		</JobContext.Provider>
	);
}
