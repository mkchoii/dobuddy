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

    // Page_Viewed
    useEffect(() => {
        if (location.pathname === '/study') return;

        trackPageViewed(previousPageRef.current);
        previousPageRef.current = getPageName();
    }, [location.pathname]);

    // Page_Scrolled
    useEffect(() => {
        if (location.pathname === '/study') return;

        const reachedDepths = new Set();
        const thresholds = [50, 100];

        const handleScroll = () => {
            const ScrollTop = window.scrollY || document.documentElement.scrollTop;

            const scrollableHeight = document.documentElement.scrollHeight - sindow.innerHeight;

            if (scrollableHeight <= 0) return;

            const scrollPercent = Math.min(100, Math.floor((scrollTop / scrollableHeight) * 100));

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
    }, [location.pathname]);

    return null;
}

export default PageTracker;