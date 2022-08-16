import {useContext, useState} from 'react';
import {UserContext} from '../../context/UserContextProvider'
import {useNavigate} from 'react-router-dom';
import {Button} from '../../global/buttons/Button';
import {InputLabel} from '../../global/textfields/Textfield';
import { Link } from 'react-router-dom';
import './LoginPage.scss'



export function LoginPage(props) {
    let userContext = useContext(UserContext)
    let navigate = useNavigate();

    


    const btext = "Login";
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


    const loginURL = process.env.REACT_APP_API_BASE_PATH + '/api/v1/users/login';
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
            navigate('/jobs');
        } 

        if (response.status === 404){ //user not found 
            alert("Username not found.");
        }
        
        else{
            alert("Username or password wrong.");
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
        <div className="py-5 loginPage">
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
