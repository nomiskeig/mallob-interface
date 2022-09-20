import { useContext, useState } from 'react';
import { InfoContext } from '../../context/InfoContextProvider';
import { UserContext } from '../../context/UserContextProvider';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../global/buttons/Button';
import { InputField } from '../../global/textfields/InputField';
import './LoginPage.scss';
import { TYPE_ERROR } from '../../context/InfoContextProvider';
import axios from 'axios';

export function LoginPage(props) {
	let userContext = useContext(UserContext);
	let infoContext = useContext(InfoContext);
	let navigate = useNavigate();

	const btext = 'Login';
	const username = 'username';
	const password = 'password';

	let [usernameContent, setUsernameContent] = useState('');
	let [passwordContent, setPasswordContent] = useState('');
	//let usernameContent = '';
	//let passwordContent = '';

	const handleChangeUsername = (event) => {
		//	usernameContent = event.target.value;J
		setUsernameContent(event.target.value);
	};

	const handleChangePassword = (event) => {
		//passwordContent = event.target.value;
		setPasswordContent(event.target.value);
	};

	const loginURL = process.env.REACT_APP_API_BASE_PATH + '/api/v1/users/login';
	function login() {
		//alert("Logging in with : username=" + usernameContent + ", password=" + passwordContent);
		if (usernameContent == '' || passwordContent == '') {
			infoContext.handleInformation(
				'Please enter a valid username and/or password.',
				TYPE_ERROR
			);
			return;
		}

		axios
			.post(loginURL, {
				username: usernameContent,
				password: passwordContent,
			})
			.then((res) => {
				//request went through and username + password were accepted
				userContext.login(res.data.token);
				navigate('/jobs');
				return;
			})
			.catch((err) => {
				infoContext.handleInformation(
					`Could not log in.\nReason: ${
						err.response.data.message ? err.response.data.message : err.message
					}`,
					TYPE_ERROR
				);
			});
	}

	function getInputField(placeholder, id, changeHandler, inputType) {
		return (
			<div className='form-outline mb-3'>
				<InputField
					placeholder={placeholder}
					id={id}
					onChange={changeHandler}
					type={inputType}
					className='form-control form-control-lg'
				></InputField>
			</div>
		);
	}

	return (
		<div className='py-5 loginPage'>
			<div>
				<div className='text-center'>
					<h1 className='mt-1 mb-5 pb-1 loginSlogan'>Welcome to Mallob!</h1>
				</div>
				<div
					className='d-flex align-items-center justify-content-center container h-100 formContainer'
					id='logindiv'
				>
					<div>
						{getInputField(username, username, handleChangeUsername, 'text')}
						{getInputField(
							password,
							password,
							handleChangePassword,
							'password'
						)}

						<div></div>

						<div className='d-flex justify-content-between align-items-center '>
							<Button
								text={btext}
								onClick={login}
								className='btn btn-primary btn-lg btn-block'
							/>
							<Button
								className='btn btn-primary btn-lg btn-block'
								text={'Register'}
								onClick={() => navigate('/register')}
							></Button>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
