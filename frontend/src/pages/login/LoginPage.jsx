import {useContext} from 'react';
import {UserContext} from '../../context/UserContextProvider'
import {useNavigate} from 'react-router-dom';


export function LoginPage(props) {
    let userContext = useContext(UserContext)
    let navigate = useNavigate();
    function loginAdmin() {
        userContext.login('eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwicm9sZSI6ImFkbWluIn0.1C4B-OmDfkegwoyCPmciXvk8G-TyS7Suv5f09nBJ5_A')
        navigate('/jobs');
         
    }
    function loginUser() {
        userContext.login('eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwicm9sZSI6InVzZXIifQ.lOw9NWomYJcsJrsLJdYbbS-14sEAe06WCKbkY3TbO6U');
        navigate('/jobs');
    }
	return (
		<div>
			<button onClick={loginAdmin}>Log in as admin</button>
            <button onClick={loginUser}>Login in as user</button>
		</div>
	);
}
