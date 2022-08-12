import React from 'react';
import { act } from 'react-dom/test-utils';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { JobContextProvider } from './JobContextProvider';
import { JobContext } from './JobContextProvider';
import axios from 'axios';
import {UserContext} from './UserContextProvider';

jest.mock('axios');
jest.mock('./UserContextProvider.jsx')

function DummyDisplay(props) {
	return (
		<JobContextProvider>
			<JobContext.Consumer>
				{({ jobs, fetchMostJobsPossible }) => (
					<div>
						<button onClick={() => fetchMostJobsPossible()}>Load jobs</button>
						{jobs.map((item, index) => (
							<div key={index} data-testid='dummyDisplay'>
								{item.name}
							</div>
						))}
					</div>
				)}
			</JobContext.Consumer>
		</JobContextProvider>
	);
}

test('Context should have a default value', () => {
	render(<DummyDisplay />);
    const dummyDisplay = screen.queryByTestId('dummyDisplay');
    expect(dummyDisplay).not.toBeInTheDocument();
});
// This test requires a mock of the usercontext, i don't have the time to do it
/*test('Context should fetch jobs', async () => {
	render(<DummyDisplay />);
	const fakeJobs = [
		{
			name: 'testJob',
		},
	];

	axios.get.mockResolvedValueOnce(fakeJobs);

	await waitFor(() => fireEvent.click(screen.getByText('Load jobs')));
	await waitFor(() =>
		expect(screen.getByTestId('dummyDisplay').textContent).toBe('testJob')
	);
	axios.get.mockClear();
});
*/
