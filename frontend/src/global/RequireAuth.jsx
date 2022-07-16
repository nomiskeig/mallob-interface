import { UserContext } from '../context/UserContextProvider';
import React, {useEffect} from 'react';
import { Navigate, useLocation } from 'react-router-dom';

export function RequireAuth({ children }) {
	let userContext = React.useContext(UserContext);
	let location = useLocation();

	if (!userContext.user.isLoaded) {
		console.log('redirecting to login page');
		return <Navigate to='/login' state={{from: location}} replace />;
	}
    console.log('logged in ')
	return children;
}
