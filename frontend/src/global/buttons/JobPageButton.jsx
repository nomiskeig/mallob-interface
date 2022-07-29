import { Link } from 'react-router-dom';
import {useNavigate} from 'react-router-dom';
import './Button.scss'

export function JobPageButton(props) {
    let navigate =useNavigate();
	return (
			<button className='button bg-primary' onClick={()=> navigate('/job/'+ props.jobID)}>
				open job page
			</button>
	);
}
