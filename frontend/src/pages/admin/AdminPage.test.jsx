import { AdminPage } from './AdminPage';
import { within, prettyDOM } from '@testing-library/dom';
import format from 'date-fns/format';
import roundToNearestMinutes from 'date-fns/roundToNearestMinutes';

import React from 'react';
import {
	render,
	screen,
	act,
	waitFor,
	fireEvent,
} from '@testing-library/react';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { JobContext } from '../../context/JobContextProvider';
import { ROLE_ADMIN, UserContext } from '../../context/UserContextProvider';
import { InfoContext } from '../../context/InfoContextProvider';
import axios from 'axios';
jest.mock('axios');

const fakeInfoProvider = {
	handleInformation: jest.fn(),
};
let fakeUserProvider = {
	user: {
		token: 'fakeToken',
	},
};
const customRender = (userProvider) => {
	return render(
		<InfoContext.Provider value={fakeInfoProvider}>
			<UserContext.Provider
				value={userProvider ? userProvider : fakeUserProvider}
			>
				<BrowserRouter>
					<AdminPage></AdminPage>
				</BrowserRouter>
			</UserContext.Provider>
		</InfoContext.Provider>
	);
};

test('handles no warnings', async () => {
	axios.mockRejectedValueOnce({
		response: { data: { message: 'Could not load warnings' } },
	});
    await waitFor(() => {
		customRender();
    })
    await waitFor(() => {
        
		expect(screen.getByText(/No warnings available/)).not.toBeNull();
        expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);
    })
});

test('displays warnings', async () => {
	axios.mockResolvedValueOnce({
		data: {
			warnings: [
				{ timestamp: '', message: 'WarningMessage' },
				{ timestamp: '2020-10-10T10:10:10.000Z', message: 'WarningMessage2' },
			],
		},
	});
	act(() => {
		customRender();

	});
    await waitFor(() => {
		let warnings = screen.queryAllByText(/WarningMessage/);
		expect(warnings).not.toBeNull();
		expect(warnings).toHaveLength(2);

    })
});
