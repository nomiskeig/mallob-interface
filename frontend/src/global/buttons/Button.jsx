import './Button.scss'

export function Button(props) {
    let bgClass = props.color ? '' : 'bg-primary'
    let style = props.color ? {
        'backgroundColor': props.color 
    } : {}
    return <button className={`button ${bgClass} ${props.className}`} onClick={props.onClick} style={style}>{props.text}</button>
}
