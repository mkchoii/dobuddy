import { useEffect, useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

import { getMyStudy, updateChatPromoSeen } from '../api/studyApi';

import StudyListPage from './StudyListPage';
import StudyDashboardPage from './StudyDashboardPage';

import './StudyDashboardPage.css';

import ChatPromoModal from '../components/modal/ChatPromoModal';
import { handleApiError } from '../handleApiError';
import { trackOpenChatPopupViewed } from '../amplitude';

function StudyPage() {
    const isDemo = sessionStorage.getItem('isDemo') === 'true';

    const [studyId, setStudyId] = useState(null);
    const [isLoading, setIsLoading] = useState(!isDemo);
    const [errorMessage, setErrorMessage] = useState('');

    const [isChatPromoOpen, setIsChatPromoOpen] = useState(false);
    

    useEffect(() => {
        if (isDemo) return;

        const loadMyStudy = async () => {
            try {
                const data = await getMyStudy();
                setStudyId(data.studyId);

                if (data.studyId !== null && !data.chatPromoSeen) {
                    setIsChatPromoOpen(true);
                    trackOpenChatPopupViewed();

                    try {
                        await updateChatPromoSeen();
                    } catch (error) {
                        console.error('홍보 모달 노출 기록 실패: ', error);
                    }
                }
            } catch (error) {
                if (handleApiError(error)) return;
                setErrorMessage(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        loadMyStudy();
    }, [isDemo]);

    const handleJoined = async (newStudyId) => {
        setStudyId(newStudyId);

        try {
            const data = await getMyStudy();

            if (!data.chatPromoSeen) {
                setIsChatPromoOpen(true);
                trackOpenChatPopupViewed();

                try {
                    await updateChatPromoSeen();
                } catch (error) {
                    console.error('홍보 모달 노출 기록 실패:', error);
                }
            }
        } catch (error) {
            console.error('홍보 모달 확인 실패:', error);
        }
};

    if (isLoading) {
        return null;
    }

    if (errorMessage) {
        return <div className="study-dashboard-message study-page-message">{errorMessage}</div>;
    }

    return (
        <>
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
                        onJoined={handleJoined}
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
        {isChatPromoOpen && (
            <ChatPromoModal 
                onClose={() => setIsChatPromoOpen(false)}
            />
        )}
        </>
    );
}

export default StudyPage;