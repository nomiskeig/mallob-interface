import {Link} from 'react-router-dom'

import './NavbarLink.scss'

export function NavbarLink(props) {
    return <Link className='link' to={props.link} style={{color: props.highlight ? '$primary' : 'white'}}>{props.name}</Link>
}
