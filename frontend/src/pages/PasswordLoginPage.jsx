import { useState, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { setAmplitudeUserId, trackEvent } from '../amplitude';

import PasswordInput from '../components/common/PasswordInput';
import PasswordResetModal from '../components/modal/PasswordResetModal';
import PasswordResetCompleteModal from '../components/modal/PasswordResetCompleteModal';
import Button from '../components/common/Button';
import { login, resetPassword } from '../api/authApi';

import './AuthPage.css';

function PasswordLoginPage() {
    const location = useLocation();
    const navigate = useNavigate();

    const email = location.state?.email;

    const [pinValue, setPinValue] = useState('');
    const [errorState, setErrorState] = useState('none');
    const [isLoading, setIsLoading] = useState(false);

    const [isPasswordResetModalOpen, setIsPasswordResetModalOpen] = useState(false);
    const [isPasswordResetCompleteModalOpen, setIsPasswordResetCompleteModalOpen] = useState(false);
    const [isResetting, setIsResetting] = useState(false);
    const [resetError, setResetError] = useState('');

    const resetAttemptCount = useRef(0);
    const loginAttemptCount = useRef(0);

    const handlePinChange = (value) => {
        setPinValue(value);
    };

    const handlePinClick = () => {
        if (errorState === 'invalid') {
            setErrorState('none');
        }
    };

    const handleSubmit = async () => {
        if (isLoading) return;
        loginAttemptCount.current += 1;

        try {
            setIsLoading(true);

            const data = await login(email, pinValue);

            // 로그인 성공 후 사용자 ID 설정
            setAmplitudeUserId(data.userId);
            trackEvent('Login_Succeeded', {
                is_first_login: data.isFirstLogin,
                login_attempt_count: loginAttemptCount.current
            });

            if (data.role === 'ADMIN') {
                navigate('/admin', { replace: true });
            } else if (data.hasGoal) {
                navigate('/dashboard', { replace: true });
            } else {
                navigate('/goal-setting', { replace: true });
            }
        } catch (error) {
            const failReason = error.code === 'PASSWORD_MISMATCH' ? 'password_mismatch' : 'server_error';

            trackEvent('Login_Failed', {
                login_attempt_count: loginAttemptCount.current,
                fail_reason: failReason
            });

            setErrorState('invalid');
        } finally {
            setIsLoading(false);
        }
    };

    const handlePasswordReset = async () => {
        if (isResetting) return;
        resetAttemptCount.current += 1;

        if (!email) {
            trackEvent('PasswordReset_Failed', {
                reset_attempt_count: resetAttemptCount.current,
                fail_reason: 'email_missing'
            });
            
            setResetError('이메일 정보가 없습니다. 다시 시도하세요.');
            return;
        }

        try {
            setIsResetting(true);
            setResetError('');

            await resetPassword(email);

            trackEvent('PasswordReset_Succeeded', {
                reset_attempt_count: resetAttemptCount.current
            });

            setIsPasswordResetModalOpen(false);
            setIsPasswordResetCompleteModalOpen(true);
        } catch (error) {
            const failReasonMap = {
                DAILY_LIMIT: 'daily_limit',
                DB_FAILED: 'db_failed',
                MAIL_FAILED: 'mail_failed'
            };

            const failReason = failReasonMap[error.code];

            if (failReason) {
                trackEvent('PasswordReset_Failed', {
                    reset_attempt_count: resetAttemptCount.current,
                    fail_reason: failReason
                });
            }

            if (error.code === 'DAILY_LIMIT') {
                setResetError('비밀번호 재설정은 하루 최대 5회까지 가능합니다.');
            } else {
                setResetError('비밀번호 초기화에 실패했습니다. 다시 시도해 주세요.');
            } 
        } finally {
            setIsResetting(false);
        }
    };

    const handleOpenPasswordResetModal = () => {
        setResetError('');
        setIsPasswordResetModalOpen(true);
    };

    const errorMessage =
        errorState === 'invalid'
            ? '비밀번호가 일치하지 않습니다.'
            : '';

    return (
        <div className="auth-page">
            <div className="auth-container">

                <div className="auth-logo" role="img" aria-label="로고"/>

                <div className="auth-form">
                    <PasswordInput
                        value={pinValue}
                        onChange={handlePinChange}
                        onClick={handlePinClick}
                        placeholder="비밀번호 입력(숫자 4자리)"
                        errorMessage={errorMessage}
                    />

                    <Button
                        disabled={pinValue.length === 0}
                        onClick={handleSubmit}
                    >
                        로그인
                    </Button>

                    <button 
                    type="button"
                    className="password-reset-button"
                    onClick={handleOpenPasswordResetModal}
                    >
                        비밀번호를 잊으셨나요?
                    </button>
                </div>
                {isPasswordResetModalOpen && (
                    <PasswordResetModal 
                    email={email} 
                    onConfirm={() => {
                        if (resetError) {
                            setIsPasswordResetModalOpen(false);
                        } else {
                            handlePasswordReset();
                        }
                    }} 
                    onClose={() => setIsPasswordResetModalOpen(false)}
                    isResetting={isResetting}
                    resetError={resetError}
                    />
                )}

                {isPasswordResetCompleteModalOpen && (
                    <PasswordResetCompleteModal
                    onConfirm={() => setIsPasswordResetCompleteModalOpen(false)}
                    />
                )}

            </div>
        </div>
    );
}

export default PasswordLoginPage;