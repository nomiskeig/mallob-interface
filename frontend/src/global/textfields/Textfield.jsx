export function InputLabel(props) {
	let id = props.id;

	/* 
    let bgClass = props.color ? '' : 'bg-primary'
    let style = props.color ? {
        'backgroundColor': props.color 
    } : {}
    */
	return (
		<input
			placeholder={props.placeholder}
			id={id}
			onChange={props.onChange}
			className={props.className}
			type={props.type}
		/>
	);
}
