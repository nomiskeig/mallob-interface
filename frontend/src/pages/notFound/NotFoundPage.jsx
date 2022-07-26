import React, {useContext} from 'react';
import {Navigate } from 'react-router-dom';
import {JobContext} from '../../context/JobContextProvider'
import {UserContext} from '../../context/UserContextProvider'

export function NotFoundPage(props) {
    let userContext = useContext(UserContext)
    // TODO: send user to login page if user is not logged in

    return (<Navigate to='/jobs' replace={true}/>)
}
