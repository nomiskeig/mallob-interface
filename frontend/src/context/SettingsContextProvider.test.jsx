import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { SettingsContextProvider } from './SettingsContextProvider';
import { SettingsContext } from './SettingsContextProvider';
import axios from 'axios';

jest.mock('axios');

function DummyDisplay(props) {
	return (
		<SettingsContextProvider>
			<SettingsContext.Consumer>
				{({ settings }) => (
					<div data-testid='dummyDisplay'>{JSON.stringify(settings)}</div>
				)}
			</SettingsContext.Consumer>
		</SettingsContextProvider>
	);
}
test.todo("repair");
/*test('Context does fetch jobs', async () => {
	const fakeSettings = {
		data: {
			amountProcesses: 500,
			startTime: '2020-02-13T18:51:10.840Z',
			defaults: {
				prioriy: 1,
				wallclockLimit: 1000,
				contentMode: 'text',
			},
		},
	};
	axios.get.mockResolvedValueOnce(fakeSettings);

	render(<DummyDisplay />)
	await waitFor(() =>
		expect(screen.getByTestId('dummyDisplay').textContent).toBe(
			JSON.stringify(fakeSettings.data)
		)
	);
});
*/
