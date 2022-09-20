import { RequireAuth } from './RequireAuth';
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
import { ROLE_ADMIN, ROLE_USER, UserContext } from '../../context/UserContextProvider';
import { InfoContext } from '../../context/InfoContextProvider';
import axios from 'axios';
jest.mock('axios');

const mockedUseNavigate = jest.fn();
let mockedLocation = { pathname: '/jobs' };
jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useNavigate: () => mockedUseNavigate,
	useLocation: () => mockedLocation,
}));


const fakeInfoProvider = {
	handleInformation: jest.fn(),
};
let fakeUserProvider;
const customRender = (ui, jobProvider, userProvider) => {
	return render(
		<InfoContext.Provider value={fakeInfoProvider}>
			<UserContext.Provider
				value={userProvider ? userProvider : fakeUserProvider}
			>
				<RequireAuth></RequireAuth>
			</UserContext.Provider>
		</InfoContext.Provider>
	);
};
beforeEach(()=> {
    jest.resetAllMocks()
})

test('job table can be viewd', () => {
	fakeUserProvider = {
		user: {
			isLoaded: true,
		},
	};

	customRender(null, null, fakeUserProvider);
	expect(mockedUseNavigate).toHaveBeenCalledTimes(0);
});
test('unloaded user is sent to login page', () => {
	fakeUserProvider = {
		user: {
			isLoaded: false,
		},
	};

	customRender(null, null, fakeUserProvider);
	expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
	expect(mockedUseNavigate).toHaveBeenCalledWith('/login');
});
test('normal user can not view admin page', () => {
	fakeUserProvider = {
		user: {
			isLoaded: true,
            role: ROLE_USER
		},
	};
    mockedLocation = {pathname: "/admin"}

	customRender(null, null, fakeUserProvider);
	expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
	expect(mockedUseNavigate).toHaveBeenCalledWith('/jobs');
});
test('unverified user can not view submit page', () => {
	fakeUserProvider = {
		user: {
			isLoaded: true,
            role: ROLE_USER,
            isVerified: false,
		},
	};
    mockedLocation = {pathname: "/submit"}

	customRender(null, null, fakeUserProvider);
	expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
	expect(mockedUseNavigate).toHaveBeenCalledWith('/jobs');
});
