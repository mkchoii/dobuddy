import { NavLink } from 'react-router-dom';

import './NavigationBar.css';

import DemoModal from '../modal/DemoModal';
import { useState } from 'react';
import { trackGuestModalViewed } from '../../amplitude';

function NavigationBar() {
    const isDemo = sessionStorage.getItem('isDemo') === 'true';
    const [isDemoModalOpen, setIsDemoModalOpen] = useState(false);

    return (
        <>
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
                onClick={(event) => {
                    if (isDemo) {
                        event.preventDefault();

                        if (isDemoModalOpen) return;
                        setIsDemoModalOpen(true);
                        trackGuestModalViewed({ feature: 'my_page' });
                    }
                }}
            >
                마이페이지
            </NavLink>

        </nav>

        {isDemoModalOpen && (
            <DemoModal 
                feature="my_page"
                onClose={() => setIsDemoModalOpen(false)}
            />
        )}
        </>
    );
}

export default NavigationBar;