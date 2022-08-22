import { useContext, useState } from 'react';
import { InfoContext } from '../../context/InfoContextProvider';
import { UserContext } from '../../context/UserContextProvider';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../global/buttons/Button';
import { InputLabel } from '../../global/textfields/Textfield';
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

    let [usernameContent, setUsernameContext] = useState('');
    let [passwordContent, setPasswordContent] = useState('');
	//let usernameContent = '';
	//let passwordContent = '';

	const handleChangeUsername = (event) => {
	//	usernameContent = event.target.value;J
        setUsernameContext(event.target.value);
	};

	const handleChangePassword = (event) => {
		//passwordContent = event.target.value;
        setPasswordContent(event.target.value)
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
				username:  usernameContent ,
				password:  passwordContent ,
			})
			.then((res) => {

				//request went through and username + password were accepted
				userContext.login(res.data.token);
				navigate('/jobs');
				return;
			})
			.catch((err) => {
				if (err.response.status === 404) {
					//user not found
					infoContext.handleInformation('Username not found', TYPE_ERROR);
				} else {
					infoContext.handleInformation(
						'Username or password wrong',
						TYPE_ERROR
					);
				}
			});

		//TODO : testing, error handling if pw was wrong, forward user onto job-page, tidy up login page (make it look nice )
	}

	function getInputLabel(placeholder, id, changeHandler, inputType) {
		return (
			<div class='form-outline mb-3'>
				<InputLabel
					placeholder={placeholder}
					id={id}
					onChange={changeHandler}
					type={inputType}
					className='form-control form-control-lg'
				></InputLabel>
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
						{getInputLabel(username, username, handleChangeUsername, 'text')}
						{getInputLabel(
							password,
							password,
							handleChangePassword,
							'password'
						)}

						<div></div>

						<div class='d-flex justify-content-between align-items-center '>
							<Button
								text={btext}
								onClick={login}
								className='btn btn-primary btn-lg btn-block'
							/>
							<Button
								className='btn btn-primary btn-lg btn-block'
								text={'Register'}
								onClick={() => navigate('/register/')}
							></Button>
						</div>
					</div>
				</div>
				<div>
					<button onClick={loginAdmin}>Log in as admin</button>
					<button onClick={loginUser}>Login in as user</button>
					<button onClick={loginUnverifiedUser}>
						Login as unverified user
					</button>
				</div>
			</div>
		</div>
	);

	function loginAdmin() {
		userContext.login(
			'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwicm9sZSI6ImFkbWluIiwidmVyaWZpZWQiOiJ0cnVlIn0.9eR-rtIsjVmygEFa9CYKd9v3B5erW5lfK3kaENnNf9s'
		);
		navigate('/jobs');
	}
	function loginUser() {
		userContext.login(
			'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwicm9sZSI6InVzZXIiLCJ2ZXJpZmllZCI6InRydWUifQ.YRKFOZKrvoTPuS2sUbSxfwI2pTR3dcOhFoqSfd4XLN0'
		);
		navigate('/jobs');
	}
	function loginUnverifiedUser() {
		userContext.login(
			'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwicm9sZSI6InVzZXIiLCJ2ZXJpZmllZCI6ImZhbHNlIn0.Bq-zHXYMFc8-yGZL1iumHMiLV7Mm6aaxM4K1IZJQPj4'
		);
		navigate('/jobs');
	}

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
