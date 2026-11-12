import { useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import {
    getPageName,
    trackPageViewed,
    trackPageScrolled
} from './amplitude';

function PageTracker() {
    const location = useLocation();
    const previousPageRef = useRef(null);

    const isDemo = sessionStorage.getItem('isDemo') === 'true';

    const isDemoPage = [
        '/goal-setting',
        '/dashboard',
        '/study',
    ].some(path =>
        location.pathname === path ||
        location.pathname.startsWith(`${path}/`)
    );

    const shouldSkipTracking =
        location.pathname === '/' ||
        location.pathname === '/entry' ||
        location.pathname === '/study' ||
        (isDemo && isDemoPage);

    // Page_Viewed
    useEffect(() => {
        if (shouldSkipTracking) return;

        trackPageViewed(previousPageRef.current);
        previousPageRef.current = getPageName();
    }, [location.pathname]);

    // Page_Scrolled
    useEffect(() => {
        if (shouldSkipTracking) return;

        const reachedDepths = new Set();
        const thresholds = [50, 100];

        const handleScroll = () => {
            const ScrollTop = window.scrollY || document.documentElement.scrollTop;

            const scrollableHeight = document.documentElement.scrollHeight - window.innerHeight;

            if (scrollableHeight <= 0) return;

            const scrollPercent = Math.min(100, Math.floor((ScrollTop / scrollableHeight) * 100));

            thresholds.forEach((depth) => {
                if (scrollPercent >= depth && !reachedDepths.has(depth)) {
                    reachedDepths.add(depth);
                    trackPageScrolled(depth);
                }
            });
        };

        window.addEventListener('scroll', handleScroll, { passive: true });

        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, [location.pathname, shouldSkipTracking]);

    return null;
}

export default PageTracker;