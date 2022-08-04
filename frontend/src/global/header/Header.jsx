import './Header.scss'

export function Header(props) {
    return <div className='header d-flex flex-row align-items-center'>
        <div className='headerTitle'>{props.title}</div>
        <div className='headerButtons'>{props.children}</div>
    </div>
}
