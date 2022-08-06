import './DropdownComponent.scss'

export function DropdownComponent(props) {
	return (
		<div className='dropdown dropdownComponentContainer'>
			<div style={{textAlign: 'left'}}>{props.title}</div>
			<div className='dropdown' >
				<button
					className='btn btn-secondary dropdown-toggle dropdownButton'
					data-bs-toggle='dropdown'
	
					aria-haspopup='true'
					aria-expanded='false'
                    style={{ textAlign:'left', backgroundColor: 'white', color: 'black'}}
                    
				>
					Select
				</button>
				<ul className='dropdown-menu' aria-labelledby='dropdownMenu'>
					{props.items.map((item) => {
						return (
							<li>
								<button
									className='dropdown-item'
									type='button'
									onClick={() => item.onClick()}
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
