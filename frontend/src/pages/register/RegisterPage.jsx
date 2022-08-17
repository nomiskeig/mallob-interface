import {useContext} from 'react';
import {UserContext} from '../../context/UserContextProvider'
import {useNavigate} from 'react-router-dom';
import {Button} from '../../global/buttons/Button';
import {InputLabel} from '../../global/textfields/Textfield';
import './../login/LoginPage.scss'




export function RegisterPage(props) {
    let userContext = useContext(UserContext)
    let navigate = useNavigate();

    let emailContent = "";
    let usernameContent = "";
    let passwordContent = "";
    let confirmPasswordContent = "";

    const handleChangeEmail = event =>{
      emailContent = event.target.value;
  }

  const handleChangeUsername = event =>{
        usernameContent = event.target.value;
  }

  const handleChangePassword = event =>{
      passwordContent = event.target.value;
  }

  const handleChangeConfirmPassword = event =>{
      confirmPasswordContent = event.target.value;
  }

  
  const registerURL = process.env.REACT_APP_API_BASE_PATH + '/api/v1/users/register';
  function register(){
      if (passwordContent !== confirmPasswordContent){
            alert("Password does not match confirmation");
      }
      const axios = require('axios');
        const response = axios.post(registerURL, {
            "username" : {usernameContent},
            "password" : {passwordContent},
            "email" : {emailContent}
        });
        if (response.status === 200){ //register was successful
            alert("Account created successfully.");
            navigate('/jobs');
        }
        if (response.status !== 200){ 
            alert("Username not available.");
        }
  }


  
  function getInputLabel(placeholder,changeHandler, inputType){
    return (
        <div class="form-outline mb-4">
        <InputLabel placeholder={placeholder} onChange={changeHandler} type={inputType} className="form-control form-control-lg"></InputLabel>
        </div>
    );

}


    function goBackToLogin(){
      navigate('/login/');
    }

    return (
        <div className="py-5 loginPage">
            <div>
                <div class="text-center">
                    <h1 class="mt-1 mb-5 pb-1 loginSlogan">Sign up!</h1>
                </div>
                <div className="d-flex align-items-center justify-content-center container h-100 formContainer" id="logindiv">

                    <form>

                    {getInputLabel("youremail@bsp.exmpl", handleChangeEmail, "email")}
                    {getInputLabel("username", handleChangeUsername, "text")}
                    {getInputLabel("password", handleChangePassword, "password")}
                    {getInputLabel("confirm password", handleChangeConfirmPassword, "password")}
                <div>
                    <Button text={"Register!"} onClick={register} className="btn btn-primary btn-lg btn-block" />
                    <Button text={"Back to Login"} onClick={goBackToLogin} className="btn-link btn btn-primary btn-lg btn-block" />
                </div>
                    </form>
                </div>
            </div>
        </div>
	);
}
