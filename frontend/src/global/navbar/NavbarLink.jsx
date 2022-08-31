import { Link } from 'react-router-dom';

import './NavbarLink.scss';

export function NavbarLink(props) {
	return (
		<Link
			className={`link ${props.highlight ? 'link-highlight' : 'link-nohighlight'} `}
			to={props.link}
		>
			{props.name}
		</Link>
	);
}
