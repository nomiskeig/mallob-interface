import { JobPageRedirect } from './JobPageRedirect';

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
jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useNavigate: () => mockedUseNavigate,
    useParams: () => ({username: 'user1', jobname: 'job1'})
}));


const fakeInfoProvider = {
	handleInformation: jest.fn(),
};
let fakeUserProvider;
let fakeJobProvider;
const customRender = (ui, jobProvider, userProvider) => {
	return render(
		<InfoContext.Provider value={fakeInfoProvider}>
            <JobContext.Provider value={fakeJobProvider}>
			<UserContext.Provider
				value={userProvider ? userProvider : fakeUserProvider}
			>
				<JobPageRedirect></JobPageRedirect>
			</UserContext.Provider>
            </JobContext.Provider>
		</InfoContext.Provider>
	);
};
beforeEach(()=> {
    jest.resetAllMocks()
})

test("can redirect to exisiting job", ()=> {
    fakeJobProvider = {
        fetchMostJobsPossible: (all, callback) => {
            callback([{config: {name: "job1"}, user: "user1", jobID: 1}]);
        }
    }
    customRender();
    expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
    expect(mockedUseNavigate).toHaveBeenCalledWith("/job/1");

})

test("can redirect to job tabvle if job does not exist", ()=> {
    fakeJobProvider = {
        fetchMostJobsPossible: (all, callback) => {
            callback([{config: {name: "job2"}, user: "user1", jobID: 1}]);
        }
    }
    customRender();
    expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
    expect(mockedUseNavigate).toHaveBeenCalledWith("/jobs");

})

