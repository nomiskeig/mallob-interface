import {useContext} from 'react';
import {UserContext} from '../../context/UserContextProvider'
import {useNavigate} from 'react-router-dom';
import {Button} from '../../global/buttons/Button';
import {InputLabel} from '../../global/textfields/Textfield';
import { Link } from 'react-router-dom';



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


    const loginURL = '/login';
    function login(){
        //alert("Logging in with : username=" + usernameContent + ", password=" + passwordContent);
        if (usernameContent === username || passwordContent === password){
            alert("Please enter a valid username and/or password.");
            return;
        }

        const axios = require('axios');
        const response = axios.post(loginURL, {
            username : {usernameContent},
            password : {passwordContent}
        });
        
        if (response.status === 200){ //request went through and username + password were accepted 
            userContext.login(response.data);
        } 

        if (response.status === 404){ //user not found 

        }
        
        else{

        }
        //TODO : testing, error handling if pw was wrong, forward user onto job-page, tidy up login page (make it look nice )
    }


    return (
		<div className="h-100 d-flex align-items-center justify-content-center">

            <form>
                <div class="form-outline mb-4">
                <InputLabel placeholder={username} id={username} onChange={handleChangeUsername} type="email" className="form-control form-control-lg"></InputLabel>
                </div>

                <div class="form-outline mb-4">
                <InputLabel placeholder={password} id={password} onChange={handleChangePassword} type="password" className="form-control form-control-lg"></InputLabel>
                </div>

                <Button text={btext} onClick={login} className="btn btn-primary btn-lg btn-block" />

                <button type="submit" class="btn btn-primary btn-lg btn-block">Sign in</button>

                <div class="d-flex justify-content-around align-items-center mb-4">
                    <Link to="/register/RegisterPage">Register</Link>
                </div>
            </form>


		</div>
	);
}
