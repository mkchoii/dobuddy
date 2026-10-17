import { useState } from 'react';

import closeIcon from '../../assets/icons/close.svg';
import CategoryChip from '../CategoryChip';
import SmallButton from '../common/SmallButton';

import './StudyGoalManageModal.css';

function StudyGoalManageModal({
  goals,
  studyCategory,
  onClose,
  onConfirm,
  isSaving = false,
}) {
  const [selectedGoalIds, setSelectedGoalIds] = useState(
    () => goals
      .filter((goal) => goal.isLinked)
      .map((goal) => goal.goalId)
  );

  const handleToggleGoal = (goalId) => {
    setSelectedGoalIds((prev) =>
      prev.includes(goalId) 
        ? prev.filter((id) => id !== goalId) 
        : [...prev, goalId]
    );
  };

  return (
    <div className="study-goal-manage-overlay">
      <div className="study-goal-manage-modal">
        <button 
          type="button" 
          className="study-goal-manage-close" 
          onClick={onClose} 
          disabled={isSaving}
          aria-label="닫기"
        >
          <img src={closeIcon} alt="" />
        </button>

        <div className="study-goal-manage-content">
          <p className="study-goal-manage-title">
            연동/해제할 세부목표를 선택하세요.
          </p>
          <p className="study-goal-manage-description">
            최소 1개 이상의 세부목표를 연동해야 합니다.
          </p>
          <p className="study-goal-manage-description-2">
            스터디 탈퇴는 관리자에게 문의해 주세요.
          </p>

          <div className="study-goal-linkable-goals">
              {goals.length === 0 ? (
                <span className="study-goal-manage-message">
                  연동 가능한 세부목표가 없습니다.
                </span>
              ) : (
                  goals.map((goal) => (
                      <CategoryChip
                          key={goal.goalId}
                          category={{
                              value: studyCategory,
                              label: goal.goalName,
                          }}
                          isSelected={selectedGoalIds.includes(goal.goalId)}
                          onClick={isSaving ? undefined : () => handleToggleGoal(goal.goalId)}
                      />
                  ))
              )}
          </div>

          <div className="study-goal-manage-action">
              <SmallButton 
                disabled={selectedGoalIds.length === 0 || isSaving} 
                onClick={() => onConfirm(selectedGoalIds)}
                >
                  {isSaving ? '처리 중' : '완료'}
              </SmallButton>
          </div>
        </div>
      </div>
    </div>
  );
}

export default StudyGoalManageModal;