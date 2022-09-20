import { RegisterPage } from './RegisterPage';
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
import { InfoContext, TYPE_ERROR } from '../../context/InfoContextProvider';
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
				<BrowserRouter>
                <RegisterPage></RegisterPage>
				</BrowserRouter>
		</InfoContext.Provider>
	);
};

const email = 'user1 email';
const username = 'user1';
const password = 'password1';

beforeEach(() => {
	jest.resetAllMocks();
});
test('Can register user', async () => {
    customRender();
	fireEvent.change(screen.getByTestId('inputField-email'), {
		target: { value:  email},
	});
	fireEvent.change(screen.getByTestId('inputField-name'), {
		target: { value:  username},
	});
	fireEvent.change(screen.getByTestId('inputField-password1'), {
		target: { value: password },
	});
	fireEvent.change(screen.getByTestId('inputField-password2'), {
		target: { value: password },
	});
	axios.post.mockResolvedValueOnce({});
    fireEvent.click(screen.getByRole('button', {name: 'Register!'}))
	expect(axios.post).toHaveBeenCalledTimes(1);
	const axiosParameters = axios.post.mock.calls[0][1];
	expect(axiosParameters).toEqual(
		expect.objectContaining({
			username: username,
			password: password,
            email: email
		})
	);
    await waitFor(() => {
    expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
    expect(mockedUseNavigate).toHaveBeenCalledWith('/login');

    })
    
});

test('Can nagivate back to the login page', () => {
	customRender();
	fireEvent.click(screen.getByRole('button', { name: 'Back to Login' }));
	expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
	expect(mockedUseNavigate).toHaveBeenCalledWith('/login');
});

test('Checks that the passwords are the same', () => {
	customRender();
	fireEvent.change(screen.getByTestId('inputField-email'), {
		target: { value:  email},
	});
	fireEvent.change(screen.getByTestId('inputField-name'), {
		target: { value:  username},
	});
	fireEvent.change(screen.getByTestId('inputField-password1'), {
		target: { value: password },
	});
	fireEvent.change(screen.getByTestId('inputField-password2'), {
		target: { value: password + "wrong value" },
	});
	fireEvent.click(screen.getByRole('button', { name: 'Register!' }));
	expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);
});


test('Displays error status code is not 200', async () => {
    customRender();
	fireEvent.change(screen.getByTestId('inputField-email'), {
		target: { value:  email},
	});
	fireEvent.change(screen.getByTestId('inputField-name'), {
		target: { value:  username},
	});
	fireEvent.change(screen.getByTestId('inputField-password1'), {
		target: { value: password },
	});
	fireEvent.change(screen.getByTestId('inputField-password2'), {
		target: { value: password },
	});
	axios.post.mockRejectedValueOnce({response: {data: {message: "Could not register."}}});
    fireEvent.click(screen.getByRole('button', {name: 'Register!'}))
	expect(axios.post).toHaveBeenCalledTimes(1);
	const axiosParameters = axios.post.mock.calls[0][1];
	expect(axiosParameters).toEqual(
		expect.objectContaining({
			username: username,
			password: password,
            email: email
		})
	);
    await waitFor(() => {
    expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);
    expect(fakeInfoProvider.handleInformation).toHaveBeenCalledWith("Could not register.\nReason: Could not register.", TYPE_ERROR);

    })
});
