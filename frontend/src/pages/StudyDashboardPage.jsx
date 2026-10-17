import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { getMatchingGoals, getStudyDashboard, getStudyMemberDetail, updateStudyGoal } from '../api/studyApi';

import UserHeader from '../components/layout/UserHeader';
import StudyProfileCard from '../components/study/StudyProfileCard';
import StudyMemberList from '../components/study/StudyMemberList';
import StudyMemberDetailModal from '../components/modal/StudyMemberDetailModal';
import StudyGoalManageModal from '../components/modal/StudyGoalManageModal';

import './StudyDashboardPage.css';
import vectorIcon from '../assets/icons/vector.svg';
import linkIcon from '../assets/icons/link.svg';

function StudyDashboardPage({ studyId }) {
    const navigate = useNavigate();

    const [dashboard, setDashboard] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState('');

    const [selectedMember, setSelectedMember] = useState(null);
    const [isMemberLoading, setIsMemberLoading] = useState(false);
    const [memberError, setMemberError] = useState('');

    const [isGoalManageOpen, setIsGoalManageOpen] = useState(false);
    const [matchingGoals, setMatchingGoals] = useState([]);
    const [isMatchingGoalLoading, setIsMatchingGoalLoading] = useState(false);
    const [isGoalManageSaving, setIsGoalManageSaving] = useState(false);

    useEffect(() => {
        const loadDashboard = async () => {
            try {
                setIsLoading(true);
                setErrorMessage('');

                const data = await getStudyDashboard(studyId);
                setDashboard(data);
            } catch (error) {
                setErrorMessage(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        loadDashboard();
    }, [studyId]);

    const handleMemberClick = async (member) => {
        try {
            setIsMemberLoading(true);
            setMemberError('');

            const data = await getStudyMemberDetail(
                studyId,
                member.studyMemberId
            );

            setSelectedMember(data);
        } catch (error) {
            setMemberError(error.message);
        } finally {
            setIsMemberLoading(false);
        }
    };

    const handleOpenGoalManage = async () => {
        try {
            setIsMatchingGoalLoading(true);

            const data = await getMatchingGoals(studyId);

            setMatchingGoals(data.goals);
            setIsGoalManageOpen(true);
        } catch (error) {
            alert(error.message);
        } finally {
            setIsMatchingGoalLoading(false);
        }
    };

    const handleConfirmGoalManage = async (selectedGoalIds) => {
        try {
            setIsGoalManageSaving(true);

            await updateStudyGoal(studyId, selectedGoalIds);

            setIsGoalManageOpen(false);
        } catch (error) {
            alert(error.message);
        } finally {
            setIsGoalManageSaving(false);
        }
    };

    return (
        <div className="study-dashboard-page">
            <div className="study-dashboard-container">
                <UserHeader />

                <main className="study-dashboard-content">

                    <div className="study-dashboard-button-row">
                        <button 
                        className="study-goal-manage-button" 
                        onClick={handleOpenGoalManage} 
                        disabled={isMatchingGoalLoading}
                        >
                            <img src={linkIcon} alt="" className="study-goal-manage-icon" />
                            <span>연동 세부목표 관리</span>
                        </button>

                        <button
                            type="button"
                            className="study-dashboard-list-button"
                            onClick={() => navigate('/study/list')}
                        >
                            스터디 목록
                            <img
                                src={vectorIcon}
                                alt=""
                                className="study-dashboard-list-icon"
                            />
                        </button>
                    </div>
                    

                    {isLoading ? (
                        <div className="study-dashboard-message">
                            스터디 대시보드를 불러오는 중입니다.
                        </div>
                    ) : errorMessage ? (
                        <div className="study-dashboard-message">
                            {errorMessage}
                        </div>
                    ) : dashboard ? (
                        <>
                            <StudyProfileCard study={dashboard} />

                            <StudyMemberList 
                              members={dashboard.members}
                              onMemberClick={handleMemberClick} 
                            />
                        </>
                    ) : null}
                </main>
                {selectedMember && (
                    <StudyMemberDetailModal
                        member={selectedMember}
                        goals={selectedMember.goals}
                        onClose={() => setSelectedMember(null)}
                    />
                )}

                {isGoalManageOpen && (
                    <StudyGoalManageModal 
                    goals={matchingGoals} 
                    studyCategory={dashboard.category}
                    onClose={() => setIsGoalManageOpen(false)} 
                    onConfirm={handleConfirmGoalManage}
                    isSaving={isGoalManageSaving}
                    />
                )}
            </div>
        </div>
    );
}

export default StudyDashboardPage;