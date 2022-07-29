import { UserContext } from '../context/UserContextProvider';
import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';

export function RequireAuth({ children }) {
	let userContext = React.useContext(UserContext);
	let location = useLocation();

	if (!userContext.user.isLoaded) {
		return <Navigate to='/login' state={{from: location}} replace />;
	}
	return children;
}
