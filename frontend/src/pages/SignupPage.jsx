import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

import { trackEvent } from '../amplitude';

import Input from '../components/common/Input';
import Button from '../components/common/Button';
import { checkEmail } from '../api/authApi';

import './AuthPage.css';


function SignupPage() {
  const [email, setEmail] = useState('');
  const [confirmEmail, setConfirmEmail] = useState('');
  const [errorState, setErrorState] = useState('none');
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  useEffect(() => {
      trackEvent('SignupPage_Viewed');
  }, []);

  const handleEmailChange = (event) => {
    const value = event.target.value;
    setEmail(value);
    if (value.length > 30) {
        setErrorState('too_long');
    } else {
        setErrorState('none');
    }
  };

  const handleConfirmEmailChange = (event) => {
    const value = event.target.value;
    setConfirmEmail(value);
    setErrorState('none');
  };

  const handleSubmit = async () => {
    if (isLoading) return;

    if (email.length > 30) {
      setErrorState('too_long');
      return;
    }

    const isEmailValid = emailRegex.test(email);
    const isConfirmEmailValid = emailRegex.test(confirmEmail);

    // 두 이메일 모두 형식 오류
    if (!isEmailValid && !isConfirmEmailValid) {
      setErrorState('invalid_format');
      return;
    }

    // 이메일 불일치 (한쪽만 형식 오류인 경우 포함)
    if (email !== confirmEmail || !isEmailValid || !isConfirmEmailValid) {
      setErrorState('not_matched');
      return;
    }

    // 이메일 중복 확인
    try {
      setIsLoading(true);

      const data = await checkEmail(email);

      if (data.exists) {
        setErrorState('already_registered');
        return;
      }

      trackEvent('Email_Created');

      // 미가입 이메일
      navigate('/signup/password', {
        state: { email },
        replace: true
      });
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  const getErrorMessage = () => {
    if (errorState === 'too_long') {
      return '이메일은 30자 이하로 입력해주세요/';
    }

    if (errorState === 'invalid_format') {
      return '이메일 형식이 올바르지 않습니다.';
    }

    if (errorState === 'not_matched') {
      return '이메일이 동일하지 않습니다.';
    }

    if (errorState === 'already_registered') {
      return '가입된 이메일입니다.';
    }

    return '';
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        <div className="auth-logo" role="img" aria-label="로고"/>

        <div className="auth-form">
          <div className="signup-email-inputs">
            <Input 
              type="email" 
              placeholder="이메일" 
              value={email} 
              onChange={handleEmailChange} 
              errorMessage={getErrorMessage()}
            />

            <Input 
              type="email" 
              placeholder="이메일 확인" 
              value={confirmEmail} 
              onChange={handleConfirmEmailChange}
            />
          </div>
          
          <Button 
              disabled={email.length === 0 || confirmEmail.length === 0 || errorState === 'too_long' || isLoading} 
              onClick={handleSubmit}
          >
            {isLoading ? '처리 중' : '다음'}
          </Button>

          <button
            type="button"
            className="auth-signup-link"
            onClick={() => navigate('/login')}
            >
              가입된 계정이 있으신가요?
            </button>
        </div>
      </div>
    </div>
  );
}

export default SignupPage;