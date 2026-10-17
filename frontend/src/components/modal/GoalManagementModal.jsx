import { useState } from 'react';
import GoalDeleteModal from './GoalDeleteModal';

import './GoalManagementModal.css';
import CategoryChip from '../CategoryChip';
import GoalEditForm from './GoalEditForm';

import closeIcon from '../../assets/icons/close.svg';
import addIcon from '../../assets/icons/add.svg';
import editIcon from '../../assets/icons/edit.svg';
import trashIcon from '../../assets/icons/trash.svg';
import GoalLinkedWarningModal from './GoalLinkedWarningModal';

function GoalManagementModal({
    goals = [],
    onCreate,
    onUpdate,
    onDelete,
    onClose,
}) {
    const [editingGoalId, setEditingGoalId] = useState(null);
    const [deleteTarget, setDeleteTarget] = useState(null);
    const [isAddingGoal, setIsAddingGoal] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [warningMessage, setWarningMessage] = useState('');

    return (
        <div className="goal-management-overlay">
            <div className="goal-management-modal">
                <div className="goal-management-header">
                    <h2>세부목표 관리</h2>

                    <button
                        type="button"
                        className="goal-management-close"
                        onClick={onClose}
                    >
                        <img src={closeIcon} alt="닫기" />
                    </button>
                </div>

                <button
                    type="button"
                    className="goal-add-button"
                    onClick={() => {
                        if (goals.length >= 3) {
                            setErrorMessage(
                                '세부목표는 최대 3개까지만 설정할 수 있습니다.'
                            );
                            return;
                        }
                        setErrorMessage('');
                        setEditingGoalId(null);
                        setIsAddingGoal((prev) => !prev);
                    }}
                >
                    <span>세부목표 추가</span>
                    <img src={addIcon} alt="" />
                </button>

                {errorMessage && (
                    <p className="goal-management-error">
                        {errorMessage}
                    </p>
                )}

                <div className="goal-management-list">
                    
                    {isAddingGoal && (
                        <GoalEditForm
                            mode="add"
                            onCreate={onCreate}
                            onComplete={() => {
                                setIsAddingGoal(false);
                                setErrorMessage('');
                            }}
                        />
                    )}

                    {goals.map((goal) => {
                        const categoryObject = {
                            value: goal.category,
                            label: goal.category,
                        };

                        const isEditing = editingGoalId === goal.goalId;

                        return (
                            <div
                                key={goal.goalId}
                                className="goal-management-item"
                            >
                                <div className="goal-management-row">
                                    <CategoryChip
                                        category={categoryObject}
                                        label={goal.goalName}
                                        isLinked={goal.isLinked}
                                    />

                                    <div className="goal-management-actions">
                                        <button
                                            type="button"
                                            onClick={() =>
                                                setEditingGoalId((prev) =>
                                                    prev === goal.goalId ? null : goal.goalId
                                                )
                                            }
                                        >
                                            <img src={editIcon} alt="수정" />
                                        </button>

                                        <button
                                            type="button"
                                            onClick={() => {
                                                if (goal.isLinked) {
                                                    setWarningMessage(
                                                        '스터디에 연동된 세부목표는 삭제할 수 없습니다.'
                                                    );
                                                    return;
                                                }
                                                if (goals.length <= 1) {
                                                    setErrorMessage(
                                                        '세부목표는 최소 1개 이상 존재해야 합니다.'
                                                    );
                                                    return;
                                                }
                                                setErrorMessage('');
                                                setDeleteTarget(goal)
                                            }}
                                        >
                                            <img src={trashIcon} alt="삭제" />
                                        </button>
                                    </div>
                                </div>

                                {editingGoalId === goal.goalId && (
                                    <GoalEditForm
                                        goal={goal}
                                        onUpdate={onUpdate}
                                        onComplete={() => setEditingGoalId(null)}
                                    />
                                )}

                                {deleteTarget && (
                                    <GoalDeleteModal
                                        goal={deleteTarget}
                                        onClose={() => setDeleteTarget(null)}
                                        onConfirm={async (goalId) => {
                                            const success = await onDelete(goalId);

                                            if (success) {
                                                setDeleteTarget(null);
                                                setErrorMessage('');
                                            }
                                        }}
                                    />
                                )}

                                {warningMessage && (
                                    <GoalLinkedWarningModal 
                                    message={warningMessage} 
                                    onClose={() => setWarningMessage('')}
                                    />
                                )}
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
}

export default GoalManagementModal;