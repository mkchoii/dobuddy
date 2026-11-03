import { useEffect } from 'react';

import StudyMemberRow from '../study/StudyMemberRow';
import './StudyMemberDetailModal.css';
import CategoryChip from '../CategoryChip';
import closeIcon from '../../assets/icons/close.svg';
import checkboxEmptyIcon from '../../assets/icons/checkbox-empty.svg';
import checkboxCheckedIcon from '../../assets/icons/checkbox-checked.svg';
import SmallButton from '../common/SmallButton';

function StudyMemberDetailModal({ 
    member, 
    goals = [], 
    onClose,
    isAdmin = false,
    onKick,
}) {
    useEffect(() => {
        const handleKeyDown = (event) => {
            if (event.key === 'Escape') onClose();
        };

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, [onClose]);

    if (!member) return null;

    return (
        <div
            className="study-member-modal-overlay"
            onMouseDown={(event) => {
                if (event.target === event.currentTarget) {
                    onClose();
                }
            }}
        >
            <div
                className="study-member-modal"
                role="dialog"
                aria-modal="true"
                aria-label={`${member.nickname} 멤버 상세`}
            >
                <button
                    type="button"
                    className="study-member-modal-close"
                    onClick={onClose}
                    aria-label="닫기"
                >
                    <img src={closeIcon} alt="" />
                </button>

                <div className="study-member-modal-profile">
                    <StudyMemberRow member={member} />
                </div>

                <div className="study-member-modal-goals">
                    {goals.map((goal) => (
                        <div
                            className="study-member-modal-goal"
                            key={goal.goalId}
                        >
                            <CategoryChip
                                category={{
                                    value: goal.category,
                                    label: goal.category,
                                }}
                                label={goal.goalName}
                                size='small'
                            />
                            <div className="study-member-modal-tasks">
                                {(goal.tasks || [])
                                .map((task) => (
                                    <div
                                        key={task.taskId}
                                        className={`study-member-modal-task ${
                                            task.completed ? 'completed' : ''
                                        }`}
                                    >
                                        <img
                                            src={
                                                task.completed
                                                    ? checkboxCheckedIcon
                                                    : checkboxEmptyIcon
                                            }
                                            alt=""
                                            className="study-member-modal-checkbox"
                                        />

                                        <span>{task.content}</span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}

                    {goals.length === 0 && (
                        <p className="study-member-modal-empty">
                            표시할 세부 목표가 없습니다.
                        </p>
                    )}
                </div>
                {isAdmin && (
                    <div className="study-member-modal-actions">
                        <SmallButton onClick={() => onKick(member)}>
                            강퇴하기
                        </SmallButton>
                    </div>
                )}
            </div>
        </div>
    );
}

export default StudyMemberDetailModal;