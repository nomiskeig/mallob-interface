export function InputField(props) {
	let id = props.id;

	/* 
    let bgClass = props.color ? '' : 'bg-primary'
    let style = props.color ? {
        'backgroundColor': props.color 
    } : {}
    */
	return (
		<input
            data-testid={'inputField-' + id}
			placeholder={props.placeholder}
			id={id}
			onChange={props.onChange}
			className={props.className}
			type={props.type}
		/>
	);
}
