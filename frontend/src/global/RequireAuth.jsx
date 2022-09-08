import { ROLE_ADMIN, UserContext } from '../context/UserContextProvider';
import React, { useContext, useEffect } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { InfoContext, TYPE_ERROR } from '../context/InfoContextProvider';
import {JobContext} from '../context/JobContextProvider';
export function RequireAuth({ children }) {
	let infoContext = useContext(InfoContext);
	let userContext = useContext(UserContext);
    let jobContext = useContext(JobContext);
	let location = useLocation();
	let navigate = useNavigate();
    let checked = false;

	useEffect(() => {
		if (!userContext.user.isLoaded) {
			infoContext.handleInformation(
				'Only logged in users can access the requested page.', TYPE_ERROR
			);
			navigate('/login');
		}
		if (
			location.pathname.endsWith('admin') &&
			userContext.user.role !== ROLE_ADMIN
		) {
			infoContext.handleInformation(
				'Only administrators can access the admin page.',
				TYPE_ERROR
			);
            navigate('/jobs')
		}

		if (location.pathname.endsWith('submit') && !userContext.user.isVerified) {
			infoContext.handleInformation(
				'You can not submit a job as an unverified user.',
				TYPE_ERROR
			);
			navigate('/jobs');
		}
        checked = true;
	}, []);

	return (<React.Fragment>{checked && children}</React.Fragment>);
}
