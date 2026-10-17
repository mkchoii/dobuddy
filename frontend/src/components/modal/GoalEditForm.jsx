import { useState } from 'react';
import { CATEGORIES } from '../../categories';
import CategoryChip from '../CategoryChip';
import './GoalEditForm.css';
import SmallButton from '../common/SmallButton';
import GoalLinkedWarningModal from './GoalLinkedWarningModal';

function GoalEditForm({
    mode = 'edit',
    goal,
    onUpdate,
    onCreate,
    onComplete,
}) {
    const [goalName, setGoalName] = useState(
        goal?.goalName ?? ''
    );
    const isTooLong = goalName.length > 10;

    const errorMessage = isTooLong ? '세부목표명은 10자 이내로 입력해주세요.' : '';
    const [category, setCategory] = useState(
        goal?.category ?? null
    );

    const [isWarningOpen, setIsWarningOpen] = useState(false);

    const handleSubmit = async () => {
        const trimmedGoalName = goalName.trim();

        if (!trimmedGoalName || !category || isTooLong) {
            return;
        }

        if (mode === 'edit' && goal?.isLinked && category !== goal.category) {
            setIsWarningOpen(true);
            return;
        }

        let success;

        if (mode === 'add') {
            success = await onCreate(
                category,
                trimmedGoalName
            );
        } else {
            success = await onUpdate(
                goal.goalId,
                category,
                trimmedGoalName
            );
        }

        if (success) {
            onComplete();
        }
    };

    return (
        <div className="goal-edit-form">
            <div className="goal-edit-categories">
                {CATEGORIES.map((item) => (
                    <CategoryChip
                        key={item.value}
                        category={item}
                        isSelected={category === item.value}
                        onClick={() => setCategory(item.value)}
                    />
                ))}
            </div>

            <div className="goal-edit-input-row">
                <input
                    type="text"
                    className={`goal-edit-input ${isTooLong ? 'input-error' : ''}`}
                    value={goalName}
                    onChange={(e) => setGoalName(e.target.value)}
                    placeholder='세부목표를 입력해주세요.'
                />

                <SmallButton
                    className="goal-edit-submit"
                    onClick={handleSubmit}
                    disabled={!goalName.trim() || !category || isTooLong}
                >
                    {mode === 'add'
                        ? '확인'
                        : '변경하기'}
                </SmallButton>
            </div>
            {errorMessage && (
                <p className="error-message">
                    {errorMessage}
                </p>
            )}

            {isWarningOpen && (
                <GoalLinkedWarningModal 
                message="스터디에 연동된 세부목표는 카테고리를 변경할 수 없습니다." 
                onClose={() => setIsWarningOpen(false)}
                />
            )}
        </div>
    );
}

export default GoalEditForm;