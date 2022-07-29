import { NavbarLink } from './NavbarLink';
import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserContext } from '../../context/UserContextProvider';
import './Navbar.scss';
export const PAGE_JOBS = 'jobsPage';
export const PAGE_VIZ = 'vizPage';
export const PAGE_SUBMIT = 'submitPage';
export const PAGE_ADMIN = 'adminPage';
export function Navbar(props) {
	let userContext = useContext(UserContext);
	let navigate = useNavigate();

	function logout() {
		userContext.logout();
		navigate('/login');
	}
	return (
		<div className='navbarContainer d-flex flex-row  align-items-center'>
            <div className='logo'></div>
				<NavbarLink
					link='/jobs'
					name='Jobs'
					highlight={props.highlight === PAGE_JOBS}
				/>

				<NavbarLink
					link='/visualization'
					name='Visualization'
					highlight={props.highlight === PAGE_VIZ}
				/>
				<NavbarLink
					link='/submit'
					name='Submit job'
					highlight={props.highlight === PAGE_SUBMIT}
				/>
				{userContext.user.role === 'admin' && (
					<NavbarLink
						link='/admin'
						name='Administration'
						highlight={props.highlight === PAGE_ADMIN}
					/>
				)}
			<button className='logoutButton ms-auto' onClick={logout}>Logout</button>
		</div>
	);
}
