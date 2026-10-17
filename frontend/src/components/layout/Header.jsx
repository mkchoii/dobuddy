import './Header.css';
import logoHeader from '../../assets/icons/logo-header.svg';

function Header() {
    return (
        <header className="header">
            <img className="header-logo" src={logoHeader}/>
        </header>
    );
}

export default Header;