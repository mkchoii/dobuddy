import { useEffect, useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

import { getMyStudy } from '../api/studyApi';

import StudyListPage from './StudyListPage';
import StudyDashboardPage from './StudyDashboardPage';

import './StudyDashboardPage.css';

function StudyPage() {
    const isDemo = sessionStorage.getItem('isDemo') === 'true';

    const [studyId, setStudyId] = useState(null);
    const [isLoading, setIsLoading] = useState(!isDemo);
    const [errorMessage, setErrorMessage] = useState('');
    

    useEffect(() => {
        if (isDemo) return;

        const loadMyStudy = async () => {
            try {
                const data = await getMyStudy();
                setStudyId(data.studyId);
            } catch (error) {
                if (handleApiError(error)) return;
                setErrorMessage(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        loadMyStudy();
    }, [isDemo]);

    if (isLoading) {
        return null;
    }

    if (errorMessage) {
        return <div className="study-dashboard-message study-page-message">{errorMessage}</div>;
    }

    return (
        <Routes>
            <Route
                index
                element={
                    <Navigate
                        to={isDemo || studyId === null ? 'list' : 'dashboard'}
                        replace
                    />
                }
            />

            <Route
                path="list"
                element={
                    <StudyListPage
                        isJoined={!isDemo && studyId !== null}
                        onJoined={setStudyId}
                    />
                }
            />

            <Route 
                path="dashboard"
                element={
                    !isDemo && studyId !== null
                        ? <StudyDashboardPage studyId={studyId} />
                        : <Navigate to="/study/list" replace />
                } 
            />

            <Route
                path="*"
                element={<Navigate to="/study" replace />}
            />
        </Routes>
    );
}

export default StudyPage;