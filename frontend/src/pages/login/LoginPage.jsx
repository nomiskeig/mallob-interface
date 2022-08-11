import {useContext} from 'react';
import {UserContext} from '../../context/UserContextProvider'
import {useNavigate} from 'react-router-dom';
import {Button} from '../../global/buttons/Button';
import {InputLabel} from '../../global/textfields/Textfield';
import { Link } from 'react-router-dom';
import './LoginPage.scss'



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
        navigate('/jobs');


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

    function getInputLabel(placeholder, id, changeHandler, inputType){
        return (
            <div class="form-outline mb-3">
                <InputLabel placeholder={placeholder} id={id} onChange={changeHandler} type={inputType} className="form-control form-control-lg"></InputLabel>
            </div>
        );

    }

    return (
        <div className="container py-5 loginPage">
            <div>
                <div class="text-center">
                    <h1 class="mt-1 mb-5 pb-1 loginSlogan">Welcome to Mallob!</h1>
                </div>
                <div className="d-flex align-items-center justify-content-center container h-100 formContainer" id="logindiv">

                    <form>

                        {getInputLabel(username, username, handleChangeUsername, "text")}
                        {getInputLabel(password, password, handleChangePassword, "password")}

                        <div>
                            <Button text={btext} onClick={login} className="btn btn-primary btn-lg btn-block" />
                        </div>

                        <div class="d-flex justify-content-around align-items-center mb-4">
                            <Link to="/register" onClick={navigate('/register/')}>Register</Link>
                        </div>
                    </form>
                </div>
            </div>
        </div>

	);

    /**
     * Approach like outlined in the first gui-sketches ;
     <div class="mb-3 row">
    <label for="staticEmail" class="col-sm-2 col-form-label">Email</label>
    <div class="col-sm-10">
      <input type="text" readonly class="form-control-plaintext" id="staticEmail" value="email@example.com">
    </div>
  </div>
  <div class="mb-3 row">
    <label for="inputPassword" class="col-sm-2 col-form-label">Password</label>
    <div class="col-sm-10">
      <input type="password" class="form-control" id="inputPassword">
    </div>
  </div>
     */
}
