import React  from 'react';
import {Navigate } from 'react-router-dom';

export function NotFoundPage(props) {
    // TODO: send user to login page if user is not logged in

    return (<Navigate to='/jobs' replace={true}/>)
}
