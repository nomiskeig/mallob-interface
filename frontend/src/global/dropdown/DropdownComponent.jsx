import './DropdownComponent.scss';
import { useState } from 'react';

export function DropdownComponent(props) {
	let [selected, setSelected] = useState(null);
	return (
		<div className='dropdown dropdownComponentContainer'>
			<div style={{ textAlign: 'left' }}>{props.title}</div>
			<div className='dropdown'>
				<button
					className='btn btn-secondary dropdown-toggle dropdownButton'
					data-bs-toggle='dropdown'
					aria-haspopup='true'
					aria-expanded='false'
					style={{
						textAlign: 'left',
						backgroundColor: 'white',
						color: 'black',
					}}
				>
					{props.displaySelectedValue && selected !== null
						? selected
						: 'Select'}
				</button>
				<ul className='dropdown-menu' aria-labelledby='dropdownMenu'>
					{props.items.map((item) => {
						return (
							<li>
								<button
									className='dropdown-item'
									type='button'
									onClick={() => {
										item.onClick();
										setSelected(item.name);
									}}
								>
									{item.name}
								</button>
							</li>
						);
					})}
				</ul>
			</div>
		</div>
	);
}
