import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';

import Button from '../components/common/Button';
import { getMe } from '../api/userApi';
import { handleApiError } from '../handleApiError';

import './SignupCompletePage.css';
import './AuthPage.css';

function SignupCompletePage() {
    const navigate = useNavigate();

    const [nickname, setNickname] = useState('');

    useEffect(() => {
        const loadUser = async () => {
            try {
                const data = await getMe();
                setNickname(data.nickname);
            } catch (error) {
                if (handleApiError(error)) return;
                console.error(error);
            }
        };

        loadUser();
    }, []);

    const handleLogin = () => {
        navigate('/dashboard', { replace: true });
    };

    return (
        <div className="signup-complete-page">
            <div className="signup-complete-container">

                <div className="auth-logo" role="img" aria-label="로고"/>

                <div className="signup-complete-content">
                    <p className="signup-complete-message">
                        회원가입이 완료되었습니다.
                    </p>

                    <p className="signup-complete-nickname">
                        닉네임 : {nickname}
                    </p>

                    <p className="signup-complete-guide">
                        카카오톡 오픈채팅방 닉네임을 동일하게 변경해주세요.
                    </p>

                    <div className="signup-complete-button">
                        <Button onClick={handleLogin}>
                            로그인
                        </Button>
                    </div>
                </div>

            </div>
        </div>
    );
}

export default SignupCompletePage;