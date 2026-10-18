import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { trackEvent } from '../amplitude';

import Input from '../components/common/Input';
import Button from '../components/common/Button';
import { checkEmail } from '../api/authApi';

import './AuthPage.css';

function LoginEmailPage() {
    const [email, setEmail] = useState('');
    const [errorState, setErrorState] = useState('none');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const emailAttemptCount = useRef(0);

    const handleEmailChange = (event) => {
        const value = event.target.value;

        setEmail(value);

        if (value.length > 30) {
            setErrorState('too_long');
        } else {
            setErrorState('none');
        }
    };

    const handleSubmit = async () => {
        if (isLoading) return;
        emailAttemptCount.current += 1;

        if (email.length > 30) {
            setErrorState('too_long');
            return;
        }
        
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(email)) {
            setErrorState('invalid_format');

            trackEvent('Email_Failed', {
                email_attempt_count: emailAttemptCount.current,
                fail_reason: 'email_format_invalid'
            });

            return;
        }

        try {
            setIsLoading(true);

            const data = await checkEmail(email);

            if (data.next === 'LOGIN' || data.next === 'SIGNUP') {
                trackEvent('Email_Succeeded', {
                    email_attempt_count: emailAttemptCount.current
                });

                navigate(
                    data.next === 'LOGIN' 
                    ? '/login/password' 
                    : '/signup/password', 
                    { state : { email: email } }
                );
            }

            if (data.next === 'NOT_INVITED') {
                setErrorState('not_registered');

                trackEvent('Email_Failed', {
                    email_attempt_count: emailAttemptCount.current,
                    fail_reason: 'email_not_registered'
                });
            }

        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    const getErrorMessage = () => {
        if (errorState === 'too_long') {
            return '이메일은 30자 이하로 입력해주세요.';
        }

        if (errorState === 'invalid_format') {
            return '이메일이 올바르지 않습니다.';
        }

        if (errorState === 'not_registered') {
            return '등록되지 않은 이메일입니다.';
        }

        return '';
    };

    return (
        <div className="auth-page">
            <div className="auth-container">

                <div className="auth-logo" role="img" aria-label="로고"/>

                <div className="auth-form">
                    <Input
                        type="email"
                        placeholder="이메일"
                        value={email}
                        onChange={handleEmailChange}
                        errorMessage={getErrorMessage()}
                    />

                    <Button
                        disabled={
                            email.length === 0 ||
                            errorState === 'too_long' ||
                            isLoading
                        }
                        onClick={handleSubmit}
                    >
                        {isLoading ? '처리 중' : '다음'}
                    </Button>
                </div>

            </div>
        </div>
    );
}

export default LoginEmailPage;