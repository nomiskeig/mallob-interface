import React from 'react';
import { render, waitFor, screen } from '@testing-library/react';
import { JobStorage } from '../model/JobStorage';
import { DetailsComponent } from './DetailsComponent';
import { Job } from '../model/Job';
import { JobTreeVertex } from '../model/JobTreeVertex';
import { BrowserRouter } from 'react-router-dom';
('');

jest.mock('../model/JobStorage.jsx');

test('renders', async () => {
	let jobStorage = new JobStorage();
	render(<DetailsComponent jobStorage={jobStorage} />);
	expect(jobStorage.getJob).toBeCalledTimes(1);
});

test('displays the buttons if the name of the job is set',async () => {
	let jobStorage = new JobStorage();
	let testJobName = 'testJob';
	let rank = 4;
	let treeIndex = 2;
	let job = new Job(1, '#123456');
	let vertex = new JobTreeVertex(rank, treeIndex);
	job.setJobName(testJobName);
	job.addVertex(vertex);

	jobStorage.getJob.mockReturnValueOnce(job);

	render(
		<React.Fragment>
			<BrowserRouter>
				<DetailsComponent jobStorage={jobStorage}  />
			</BrowserRouter>
		</React.Fragment>
	);
    expect(screen.queryAllByRole('button').length).toBe(2)
});
test('displays no buttons if the name of the job is not set',async () => {
	let jobStorage = new JobStorage();
	let job = new Job(1, '#123456');

	jobStorage.getJob.mockReturnValueOnce(job);

	render(
		<React.Fragment>
			<BrowserRouter>
				<DetailsComponent jobStorage={jobStorage} />
			</BrowserRouter>
		</React.Fragment>
	);
    expect(screen.queryAllByRole('button')).toEqual([])
});
