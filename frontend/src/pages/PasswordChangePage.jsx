import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import UserHeader from '../components/layout/UserHeader';
import PasswordInput from '../components/common/PasswordInput';
import Button from '../components/common/Button';
import PasswordChangeCompleteModal from '../components/modal/PasswordChangeCompleteModal';

import { getMe, changePassword } from '../api/userApi';
import { profileImages } from '../assets/profile/profileImages';
import { handleApiError } from '../handleApiError';

import './PasswordChangePage.css';

function PasswordChangePage() {
    const navigate = useNavigate();

    const [user, setUser] = useState(null);

    const [currentPinValue, setCurrentPinValue] = useState('');
    const [newPinValue, setNewPinValue] = useState('');

    const [errorState, setErrorState] = useState('none');
    const [isCompletePopupOpen, setIsCompletePopupOpen] = useState(false);

    useEffect(() => {
        const loadUser = async () => {
            try {
                const data = await getMe();
                setUser(data);
            } catch (error) {
                if (handleApiError(error)) return;
                console.error(error);
            }
        };

        loadUser();
    }, []);

    const handleCurrentPinChange = (value) => {
        setCurrentPinValue(value);
    };

    const handleNewPinChange = (value) => {
        setNewPinValue(value);
    };

    const handleInputClick = () => {
        setErrorState('none');
    };

    const handleSubmit = async () => {
        try {
            await changePassword(
                currentPinValue,
                newPinValue
            );  

            setErrorState('none');
            setIsCompletePopupOpen(true);

        } catch (error) {
            if (handleApiError(error)) return;
            setCurrentPinValue('');
            setNewPinValue('');
            
            if (error.message === '현재 비밀번호가 일치하지 않습니다.') {
                setErrorState('mismatch');
                return;
            }

            if (error.message === '새 비밀번호는 현재 비밀번호와 다르게 설정해주세요.') {
                setErrorState('same_as_current');
                return;
            }

            console.error(error);
        }
    };

    const handleComplete = () => {
        setIsCompletePopupOpen(false);
        navigate('/mypage');
    };

    const currentPasswordError =
        errorState === 'mismatch'
            ? '현재 비밀번호가 일치하지 않습니다.'
            : '';

    const newPasswordError =
        errorState === 'same_as_current'
            ? '새 비밀번호는 현재 비밀번호와 다르게 설정해주세요.'
            : '';

    return (
        <>
            <UserHeader />
            <div className="password-change-page">
                <div className="password-change-container">

                    <main className="password-change-content">

                        <img
                            className="password-change-profile-image"
                            src={
                                profileImages[user?.profileImageId]
                                ?? profileImages[1]
                            }
                            alt="프로필"
                        />

                        <div className="password-change-profile-info">
                            <p className="password-change-nickname">
                                {user?.nickname ?? ''}
                            </p>

                            <p className="password-change-email">
                                {user?.email ?? ''}
                            </p>
                        </div>

                        <div className="password-change-form">

                            <PasswordInput
                                value={currentPinValue}
                                onChange={handleCurrentPinChange}
                                onClick={handleInputClick}
                                placeholder="기존 비밀번호 입력"
                                errorMessage={currentPasswordError}
                            />

                            <PasswordInput
                                value={newPinValue}
                                onChange={handleNewPinChange}
                                onClick={handleInputClick}
                                placeholder="새 비밀번호 입력(숫자 4자리)"
                                errorMessage={newPasswordError}
                            />

                            <Button
                                onClick={handleSubmit}
                                disabled={newPinValue.length < 4}
                            >
                                확인
                            </Button>

                        </div>

                    </main>

                    {isCompletePopupOpen && (
                        <PasswordChangeCompleteModal onComplete={handleComplete} />
                    )}

                </div>
            </div>
        </>
    );
}

export default PasswordChangePage;
