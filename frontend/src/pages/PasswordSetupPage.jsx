import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

import PasswordInput from '../components/common/PasswordInput';
import Button from '../components/common/Button';
import { signup } from '../api/authApi';

import './AuthPage.css';

function PasswordSetupPage() {
    const location = useLocation();
    const navigate = useNavigate();

    const email = location.state?.email;
    const [pinValue, setPinValue] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const isValidPin = /^\d{4}$/.test(pinValue);

    const handlePinChange = (value) => {
        setPinValue(value);

        if (value && !/^\d*$/.test(value)) {
            setErrorMessage('비밀번호는 숫자 4자리입니다.');
        } else {
            setErrorMessage('');
        }
    };

    const handleSubmit = async () => {
        try {
            await signup(email, pinValue);

            navigate('/goal-setting');
        } catch (error) {
            setErrorMessage(error.message);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">

                <div className="auth-logo" role="img" aria-label="로고"/>

                <div className="auth-form">
                    <PasswordInput
                        value={pinValue}
                        onChange={handlePinChange}
                        placeholder="비밀번호 생성(숫자 4자리)"
                        errorMessage={errorMessage}
                    />

                    <Button
                        disabled={!isValidPin}
                        onClick={handleSubmit}
                    >
                        다음
                    </Button>
                </div>

            </div>
        </div>
    );
}

export default PasswordSetupPage;