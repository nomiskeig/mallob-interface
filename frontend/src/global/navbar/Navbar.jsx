
import {NavbarLink} from './NavbarLink';
import {useContext} from  'react';
import {useNavigate} from 'react-router-dom';
import {Link} from 'react-router-dom'
import {UserContext} from '../../context/UserContextProvider'
import './Navbar.scss'
export const PAGE_JOBS = 'jobsPage';
export const PAGE_VIZ = 'vizPage';
export const PAGE_SUBMIT = 'submitPage';
export const PAGE_ADMIN = 'adminPage';
export function Navbar(props) {
    let userContext = useContext(UserContext);
    let navigate = useNavigate();

    function logout() {
        userContext.logout();
        navigate('/login')

    }
    return <div className='navbarContainer'>
        <NavbarLink link='/jobs' name='Jobs' highlight={props.highlight === PAGE_JOBS}/>
        <NavbarLink link='/visualization' name='Visualization' highlight={props.highlight === PAGE_VIZ}/>
        <NavbarLink link='/submit' name='Submit' highlight={props.highlight === PAGE_SUBMIT} />
        <button onClick={logout}>Logout</button>
    </div>
}
