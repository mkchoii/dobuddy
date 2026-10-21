import { NavLink } from 'react-router-dom';

import './NavigationBar.css';

function NavigationBar() {
    return (
        <nav className="navigation-bar">

            <NavLink
                to="/dashboard"
                className={({ isActive }) =>
                    `navigation-item ${isActive ? 'active' : ''}`
                }
            >
                개인 대시보드
            </NavLink>

            <NavLink
                to="/study"
                className={({ isActive }) =>
                    `navigation-item ${isActive ? 'active' : ''}`
                }
                onClick={(event) => {
                    if (window.location.pathname === '/study') {
                        event.preventDefault();
                        window.location.reload();
                    }
                }}
            >
                팀 스페이스
            </NavLink>

            <NavLink
                to="/mypage"
                className={({ isActive }) =>
                    `navigation-item ${isActive || window.location.pathname === '/password-change' ? 'active' : ''}`
                }
            >
                마이페이지
            </NavLink>

        </nav>
    );
}

export default NavigationBar;