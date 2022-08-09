import {useContext} from 'react';
import {UserContext} from '../../context/UserContextProvider'
import {useNavigate} from 'react-router-dom';
import {Button} from '../../global/buttons/Button';
import {InputLabel} from '../../global/textfields/Textfield';
import { Link } from 'react-router-dom';



export function LoginPage(props) {
    let userContext = useContext(UserContext)
    let navigate = useNavigate();


    let btext = "Login";
    const username = "username";
    const password = "password";



    return (
		<div className="h-100 d-flex align-items-center justify-content-center">
            <p>This is the reigster page</p>
		</div>
	);
}
