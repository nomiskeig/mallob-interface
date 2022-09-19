import React, {useContext, useEffect}  from 'react';
import {UserContext} from '../../context/UserContextProvider';
import {InfoContext, TYPE_ERROR} from '../../context/InfoContextProvider';
import {useNavigate} from 'react-router-dom';

export function NotFoundPage(props) {
    let userContext = useContext(UserContext);
    let infoContext = useContext(InfoContext);
    let navigate  = useNavigate();
    useEffect(() => {
        infoContext.handleInformation('The requested page could not be found.', TYPE_ERROR);
        if (userContext.user.isLoaded) {
            navigate("/jobs");
        } else {
            navigate("/login");
        }
    })
    return (<div/>) 

}
