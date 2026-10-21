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

    // 기존 Page_Viewed
    useEffect(() => {
        if (location.pathname === '/study') return;

        trackPageViewed(previousPageRef.current);
        previousPageRef.current = getPageName();
    }, [location.pathname]);

    // Page_Scrolled
    useEffect(() => {
        if (location.pathname === '/study') return;

        const reachedDepths = new Set();
        const INTERVAL = 10;

        const handleScroll = () => {
            const scrollTop =
                window.scrollY || document.documentElement.scrollTop;

            const scrollableHeight =
                document.documentElement.scrollHeight - window.innerHeight;

            if (scrollableHeight <= 0) return;

            const scrollPercent = Math.min(
                100,
                Math.floor((scrollTop / scrollableHeight) * 100)
            );

            for (let depth = INTERVAL; depth <= 100; depth += INTERVAL) {
                if (scrollPercent >= depth && !reachedDepths.has(depth)) {
                    reachedDepths.add(depth);
                    trackPageScrolled(depth);
                }
            }
        };

        window.addEventListener('scroll', handleScroll, { passive: true });

        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, [location.pathname]);

    return null;
}

export default PageTracker;