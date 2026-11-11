import { Outlet, useLocation } from 'react-router-dom';
import DemoBanner from './DemoBanner';
import './DemoLayout.css';

function DemoLayout() {
    const isDemo = sessionStorage.getItem('isDemo') === 'true';
    const location = useLocation();

    const isGoalSetting = location.pathname === '/goal-setting';

    return (
        <div className={`demo-layout ${isDemo && isGoalSetting ? 'demo-layout-overlay' : ''}`}>
            {isDemo && <DemoBanner />}
            <Outlet />
        </div>
    );
}

export default DemoLayout;