import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { SettingsContextProvider } from './SettingsContextProvider';
import { SettingsContext } from './SettingsContextProvider';
import { UserContext } from './UserContextProvider';
import { InfoContext } from './InfoContextProvider';
import axios from 'axios';
import { BrowserRouter } from 'react-router-dom';

jest.mock('axios');

let fakeUserContext = { logout: jest.fn(), user: { isLoaded: true } };
let fakeInfoContext = { handleInformation: jest.fn() };
function DummyDisplay(props) {
	return (
		<BrowserRouter>
			<InfoContext.Provider value={fakeInfoContext}>
				<UserContext.Provider value={fakeUserContext}>
					<SettingsContextProvider>
						<SettingsContext.Consumer>
							{({ settings }) => (
								<div data-testid='dummyDisplay'>{JSON.stringify(settings)}</div>
							)}
						</SettingsContext.Consumer>
					</SettingsContextProvider>
				</UserContext.Provider>
			</InfoContext.Provider>
		</BrowserRouter>
	);
}
beforeEach(() => {
	jest.resetAllMocks();
});
test('Context does fetch settings', async () => {
	const fakeSettings = {
		data: {
			amountProcesses: 500,
			startTime: '2020-02-13T18:51:10.840Z',
			defaults: {
				prioriy: 1,
				wallclockLimit: '1000s',
				contentMode: 'text',
			},
		},
	};
	axios.mockResolvedValueOnce(fakeSettings);

	render(<DummyDisplay />);
	await waitFor(() =>
		expect(screen.getByTestId('dummyDisplay').textContent).toBe(
			JSON.stringify(fakeSettings.data)
		)
	);
});

test('does handle invalid token', async () => {
	axios.mockRejectedValueOnce({ response: { status: 401 } });
	render(<DummyDisplay />);
	await waitFor(() => {
		expect(fakeUserContext.logout).toHaveBeenCalledTimes(1);
	});
});

test('does handle other erros', async () => {
	axios.mockRejectedValueOnce({
		response: { status: 402, data: { message: 'This is an error message.' } },
	});
	render(<DummyDisplay />);
	await waitFor(() => {
		expect(fakeInfoContext.handleInformation).toHaveBeenCalledTimes(1);
	});
});
