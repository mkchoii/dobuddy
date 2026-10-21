import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { CATEGORIES } from '../categories';
import CategoryChip from '../components/CategoryChip';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import trashIcon from '../assets/icons/trash.svg';
import { createGoal } from '../api/goalApi';

import './GoalSettingPage.css';
import './AuthPage.css';
import { trackGoalCreated } from '../amplitude';
import { handleApiError } from '../handleApiError';

function GoalSettingPage() {
    const navigate = useNavigate();

    const nickname = location.state?.nickname;

    const [category, setCategory] = useState(null);
    const [goalName, setGoalName] = useState('');
    const [goalNameError, setGoalNameError] = useState('none');
    const selectedCategory = CATEGORIES.find(
        (item) => item.value === category
    );

    const handleGoalNameChange = (e) => {
        const value = e.target.value;

        setGoalName(value);

        if (value.length > 10) {
            setGoalNameError('max_length');
        } else {
            setGoalNameError('none');
        }
    };

    const handleReset = () => {
        setCategory(null);
        setGoalName('');
        setGoalNameError('none');
    };

    const handleComplete = async () => {
        try {
            const data = await createGoal(category, goalName);

            trackGoalCreated(data, [data.category]);

            navigate('/signup-complete', {
                state: {
                    nickname: nickname,
                },
            });
        } catch (error) {
            if (handleApiError(error)) return;
            console.error(error);
        }
    };

    const isDisabled =
        category === null ||
        goalName.trim() === '' ||
        goalNameError !== 'none';

    const errorMessage =
        goalNameError === 'max_length'
            ? '세부목표명은 10자 이내로 입력해주세요.'
            : '';

    return (
        <div className="goal-setting-page">
            <div className="goal-setting-container">

                <div className="auth-logo" role="img" aria-label="로고"/>

                <p className="goal-setting-description">
                    달성하고 싶은 목표를 설정해주세요.
                </p>

                <div className="goal-category-area">
                    <div className="goal-category-header">
                        <p className="goal-category-text">세부 목표 선택</p>

                        <button
                            type="button"
                            className="goal-reset-button"
                            onClick={handleReset}
                        >
                            <img
                                src={trashIcon}
                                alt="초기화"
                                className="trash-icon"
                            />
                        </button>
                    </div>

                    <div className="category-list">
                        {CATEGORIES.map((item) => (
                            <CategoryChip
                                key={item.value}
                                category={item}
                                isSelected={category === item.value}
                                onClick={() => setCategory(item.value)}
                            />
                        ))}
                    </div>
                </div>

                <div className="goal-input-area">
                    <Input
                        value={goalName}
                        onChange={handleGoalNameChange}
                        placeholder={
                            selectedCategory
                                ? selectedCategory.placeholder
                                : '카테고리를 먼저 선택해주세요'
                        }
                        errorMessage={errorMessage}
                        disabled={category === null}
                        className="goal-name-input"
                    />
                </div>

                <div className="goal-button-area">
                    <Button
                        disabled={isDisabled}
                        onClick={handleComplete}
                    >
                        완료
                    </Button>
                </div>

            </div>
        </div>
    );
}

export default GoalSettingPage;