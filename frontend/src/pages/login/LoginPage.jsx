import { useContext } from 'react';
import { UserContext } from '../../context/UserContextProvider';
import { useNavigate } from 'react-router-dom';

export function LoginPage(props) {
	let userContext = useContext(UserContext);
	let navigate = useNavigate();
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
		userContext.login('eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwicm9sZSI6InVzZXIiLCJ2ZXJpZmllZCI6ImZhbHNlIn0.Bq-zHXYMFc8-yGZL1iumHMiLV7Mm6aaxM4K1IZJQPj4'
		);
		navigate('/jobs');
	}
	return (
		<div>
			<button onClick={loginAdmin}>Log in as admin</button>
			<button onClick={loginUser}>Login in as user</button>
            <button onClick={loginUnverifiedUser}>Login as unverified user</button>
		</div>
	);
}
