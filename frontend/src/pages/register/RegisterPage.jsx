import { useContext, useState } from 'react';
import {InfoContext, TYPE_ERROR, TYPE_INFO} from '../../context/InfoContextProvider';

import { useNavigate } from 'react-router-dom';
import { Button } from '../../global/buttons/Button';
import { InputLabel } from '../../global/textfields/Textfield';
import './../login/LoginPage.scss';
import axios from 'axios'

export function RegisterPage(props) {
    let infoContext = useContext(InfoContext);
	let navigate = useNavigate();

    let [emailContent, setEmailContent] = useState('')
    let [usernameContent, setUsernameContent] = useState('');
    let [passwordContent, setPasswordContent] = useState('');
    let [confirmPasswordContent, setConfirmPasswordContent] = useState('')

	const handleChangeEmail = (event) => {
        setEmailContent(event.target.value);
	};

	const handleChangeUsername = (event) => {
        setUsernameContent(event.target.value);
	};

	const handleChangePassword = (event) => {
        setPasswordContent(event.target.value);
	};

	const handleChangeConfirmPassword = (event) => {
        setConfirmPasswordContent(event.target.value)
	};

	const registerURL =
		process.env.REACT_APP_API_BASE_PATH + '/api/v1/users/register';
	function register() {
		if (passwordContent !== confirmPasswordContent) {
            infoContext.handleInformation('The passwords do not match.', TYPE_ERROR);
            return;
		}
		axios.post(registerURL, {
			username: { usernameContent },
			password: { passwordContent },
			email: { emailContent },
		}).then((res) => {

			//register was successful
            infoContext.handleInformation('Account successfully created.', TYPE_INFO);
			navigate('/login');
        }).catch((err) => {
           infoContext.handleInformation('Username not available', TYPE_ERROR);
        })
	}

	function getInputLabel(placeholder, changeHandler, inputType) {
		return (
			<div class='form-outline mb-4'>
				<InputLabel
					placeholder={placeholder}
					onChange={changeHandler}
					type={inputType}
					className='form-control form-control-lg'
				></InputLabel>
			</div>
		);
	}

	function goBackToLogin() {
		navigate('/login/');
	}

	return (
		<div className='py-5 loginPage'>
			<div>
				<div class='text-center'>
					<h1 class='mt-1 mb-5 pb-1 loginSlogan'>Sign up!</h1>
				</div>
				<div
					className='d-flex align-items-center justify-content-center container h-100 formContainer'
					id='logindiv'
				>
					<div>
						{getInputLabel('youremail@bsp.exmpl', handleChangeEmail, 'email')}
						{getInputLabel('username', handleChangeUsername, 'text')}
						{getInputLabel('password', handleChangePassword, 'password')}
						{getInputLabel(
							'confirm password',
							handleChangeConfirmPassword,
							'password'
						)}
						<div>
							<Button
								text={'Register!'}
								onClick={register}
								className='btn btn-primary btn-lg btn-block'
							/>
							<Button
								text={'Back to Login'}
								onClick={goBackToLogin}
								className='btn btn-primary btn-lg btn-block'
							/>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
