import {useContext} from 'react';
import {UserContext} from '../../context/UserContextProvider'
import {useNavigate} from 'react-router-dom';
import {Button} from '../../global/buttons/Button';
import {InputLabel} from '../../global/textfields/Textfield';


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


    let btext = "Login";
    const username = "username";
    const password = "password";

    let usernameContent = username;
    let passwordContent = password;

    const handleChangeUsername = event =>{
        usernameContent = event.target.value;
    }

    const handleChangePassword = event =>{
        passwordContent = event.target.value;
    }


    function login(){
        alert("Logging in with : username=" + usernameContent + ", password=" + passwordContent);
    }


    return (
		<div>
			<button onClick={loginAdmin}>Log in as admin</button>
            <button onClick={loginUser}>Login in as user</button>


            <div>
                <InputLabel placeholder={username} id={username} onChange={handleChangeUsername}></InputLabel>
                <InputLabel placeholder={password} id={password} onChange={handleChangePassword}></InputLabel>

                <Button text={btext} onClick={login} />
            </div>

		</div>
	);
}
