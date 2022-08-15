import './DropdownComponent.scss';
import { useState } from 'react';

export function DropdownComponent(props) {
	let [selected, setSelected] = useState(null);
	let displayedText = 'Select';
	if (props.displaySelectedValue && selected !== null) {
		displayedText = selected;
	} else if (props.default && selected === null) {
		displayedText = props.default;
	}

	return (
		<div className='dropdown dropdownComponentContainer'>
			<div className='dropdownLabel'>{props.title}</div>
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
					{displayedText}
				</button>
				<ul className='dropdown-menu' aria-labelledby='dropdownMenu'>
					{props.items.map((item, index) => {
						return (
							<li key={index}>
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
