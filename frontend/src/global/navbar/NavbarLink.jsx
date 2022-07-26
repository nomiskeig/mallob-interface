import {Link} from 'react-router-dom'


export function NavbarLink(props) {
    return <Link to={props.link} style={{color: props.highlight ? '$primary' : 'white'}}>{props.name}</Link>
}
