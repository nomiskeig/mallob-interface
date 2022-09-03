import { LoginPage } from './LoginPage';
import { within, prettyDOM } from '@testing-library/dom';

import React from 'react';
import {
	render,
	screen,
	act,
	waitFor,
	fireEvent,
} from '@testing-library/react';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { ROLE_ADMIN, UserContext } from '../../context/UserContextProvider';
import { InfoContext } from '../../context/InfoContextProvider';
import axios from 'axios';
jest.mock('axios');

const mockedUseNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useNavigate: () => mockedUseNavigate,
}));

const fakeInfoProvider = {
	handleInformation: jest.fn(),
};
let fakeUserProvider = {
	login: jest.fn(),
};
const customRender = (ui, userProvider) => {
	return render(
		<InfoContext.Provider value={fakeInfoProvider}>
			<UserContext.Provider
				value={userProvider ? userProvider : fakeUserProvider}
			>
				<BrowserRouter>
					<Routes>
						<Route path='/' element={ui} />
					</Routes>
				</BrowserRouter>
			</UserContext.Provider>
		</InfoContext.Provider>
	);
};

const username = 'user1';
const password = 'password1';

beforeEach(() => {
	jest.resetAllMocks();
});
test('Can log in user', async () => {
	customRender(<LoginPage />);
	fireEvent.change(screen.getByTestId('inputField-username'), {
		target: { value: username },
	});
	fireEvent.change(screen.getByTestId('inputField-password'), {
		target: { value: password },
	});
	axios.post.mockResolvedValueOnce({ data: { token: 'token' }, status: 404 });
	fireEvent.click(screen.getByRole('button', { name: 'Login' }));
	expect(axios.post).toHaveBeenCalledTimes(1);
	const axiosParameters = axios.post.mock.calls[0][1];
	expect(axiosParameters).toEqual(
		expect.objectContaining({
			username: username,
			password: password,
		})
	);
});

test('Can nagivate to the register page', () => {
	customRender(<LoginPage />);
	fireEvent.click(screen.getByRole('button', { name: 'Register' }));
	expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
	expect(mockedUseNavigate).toHaveBeenCalledWith('/register');
});

test('Checks that neither password or username are empty', () => {
	customRender(<LoginPage />);
	fireEvent.click(screen.getByRole('button', { name: 'Login' }));
	expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);
});

test('Displays error status code is not 200', async () => {
	customRender(<LoginPage />);
	fireEvent.change(screen.getByTestId('inputField-username'), {
		target: { value: username },
	});
	fireEvent.change(screen.getByTestId('inputField-password'), {
		target: { value: password },
	});
	axios.post.mockRejectedValueOnce({ response: { status: 404 } });

	await waitFor(() => {
		fireEvent.click(screen.getByRole('button', { name: 'Login' }));
	});
	expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);
});
