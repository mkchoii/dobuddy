import { useEffect, useState } from 'react';
import { getMatchingGoals } from '../../api/studyApi';
import { handleApiError } from '../../handleApiError';

import CategoryChip from '../CategoryChip';
import SmallButton from '../common/SmallButton';
import closeIcon from '../../assets/icons/close.svg';

import './StudyGoalLinkModal.css';

function StudyGoalLinkModal({
    study,
    onClose,
    onComplete,
    isSubmitting = false,
    submitError = '',
}) {
    const [selectedGoalIds, setSelectedGoalIds] = useState([]);
    const [goals, setGoals] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState('');

    const handleToggleGoal = (goalId) => {
        setSelectedGoalIds((prev) =>
            prev.includes(goalId)
                ? prev.filter((id) => id !== goalId)
                : [...prev, goalId]
        );
    };

    useEffect(() => {
        let cancelled = false;

        const loadMatchingGoals = async () => {
            try {
                setIsLoading(true);
                setErrorMessage('');

                const data = await getMatchingGoals(study.studyId);

                if (!cancelled) {
                    setGoals(data.goals);
                }
            } catch (error) {
                if (!cancelled) {
                    if (handleApiError(error)) return;
                    setErrorMessage(error.message);
                }
            } finally {
                if (!cancelled) {
                    setIsLoading(false);
                }
            }
        };

        loadMatchingGoals();

        return () => {
            cancelled = true;
        };
    }, [study.studyId]);

    return (
        <div className="study-goal-modal-overlay" 
              onClick={onClose}
        >
            <div
                className="study-goal-modal"
                role="dialog"
                aria-modal="true"
                aria-label="세부목표 연동"
                onClick={(e) => e.stopPropagation()}
            >
                <button
                    type="button"
                    className="study-goal-modal-close"
                    onClick={onClose}
                    aria-label="닫기"
                >
                    <img src={closeIcon} alt="" />
                </button>

                <div className="study-goal-modal-description">
                    <p className="study-goal-modal-title">
                        스터디 가입을 위해서는 세부목표 연동이
                        필요합니다.
                    </p>
                    <p className="study-goal-modal-subtitle">
                        연동할 세부목표를 선택해주세요.
                    </p>
                </div>

                <div className="study-goal-modal-goals">
                    {isLoading ? (
                        <span>불러오는 중...</span>
                    ) : errorMessage ? (
                        <span className="study-goal-modal-message">
                            {errorMessage}
                        </span>
                    ) : goals.length === 0 ? (
                        <span className="study-goal-modal-message">
                            연동 가능한 세부목표가 없습니다.
                        </span>
                    ) : (
                        goals.map((goal) => (
                            <CategoryChip
                                key={goal.goalId}
                                category={{
                                    value: study.category,
                                    label: goal.goalName,
                                }}
                                isSelected={selectedGoalIds.includes(goal.goalId)}
                                onClick={() => handleToggleGoal(goal.goalId)}
                            />
                        ))
                    )}
                </div>

                <div className="study-goal-modal-action">
                    <SmallButton
                        disabled={
                            selectedGoalIds.length === 0 ||
                            isLoading ||
                            isSubmitting ||
                            Boolean(errorMessage)
                        }
                        onClick={() =>
                            onComplete(study.studyId, selectedGoalIds)
                        }
                    >
                        {isSubmitting ? '처리 중' : '완료'}
                    </SmallButton>
                </div>

                {submitError && (
                    <p className="study-goal-modal-error">
                        {submitError}
                    </p>
                )}
            </div>
        </div>
    );
}

export default StudyGoalLinkModal;