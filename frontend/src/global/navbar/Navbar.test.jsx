import { Navbar } from './Navbar';
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
import {
	ROLE_ADMIN,
	ROLE_USER,
	UserContext,
} from '../../context/UserContextProvider';
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
let fakeUserProvider;
const customRender = (ui, jobProvider, userProvider) => {
	return render(
        <BrowserRouter>
		<InfoContext.Provider value={fakeInfoProvider}>
			<UserContext.Provider
				value={userProvider ? userProvider : fakeUserProvider}
			>
				<Navbar></Navbar>
			</UserContext.Provider>
		</InfoContext.Provider>
        </BrowserRouter>
	);
};
beforeEach(() => {
	jest.resetAllMocks();
});

test('can log out', () => {
	fakeUserProvider = {
		logout: jest.fn(),
		user: {
			isLoaded: true,
			isVeriefied: true,
			role: ROLE_ADMIN,
		},
	};
	customRender();
	fireEvent.click(screen.getByRole('button', { name: 'Logout' }));
	expect(fakeUserProvider.logout).toHaveBeenCalledTimes(1);
	expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
	expect(mockedUseNavigate).toHaveBeenCalledWith('/login');
});
