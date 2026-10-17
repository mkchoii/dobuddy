import { useEffect, useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

import { getMyStudy } from '../api/studyApi';

import StudyListPage from './StudyListPage';
import StudyDashboardPage from './StudyDashboardPage';

import './StudyDashboardPage.css';

function StudyPage() {
    const [studyId, setStudyId] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        const loadMyStudy = async () => {
            try {
                const data = await getMyStudy();
                setStudyId(data.studyId);
            } catch (error) {
                setErrorMessage(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        loadMyStudy();
    }, []);

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
                    studyId === null
                        ? <StudyListPage isJoined={false} onJoined={setStudyId} />
                        : <StudyDashboardPage studyId={studyId} />
                }
            />

            <Route
                path="list"
                element={
                    <StudyListPage
                        isJoined={studyId !== null}
                        onJoined={setStudyId}
                    />
                }
            />

            <Route path="*" element={<Navigate to="/study" replace />} />
        </Routes>
    );
}

export default StudyPage;