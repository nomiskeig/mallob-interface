import {useContext} from 'react';
import {UserContext} from '../../context/UserContextProvider'
import {useNavigate} from 'react-router-dom';
import {Button} from '../../global/buttons/Button';
import {InputLabel} from '../../global/textfields/Textfield';
import { Link } from 'react-router-dom';



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

  
  function register(){
      if (passwordContent !== confirmPasswordContent){
            alert("Password does not match confirmation");
      }
      alert("register!");
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
      <div className="h-100 d-flex align-items-center justify-content-center" id="logindiv">

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
	);
}
