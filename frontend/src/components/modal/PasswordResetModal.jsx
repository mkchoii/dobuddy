import SmallButton from "../common/SmallButton";
import closeIcon from '../../assets/icons/close.svg';
import "./PasswordResetModal.css";

function PasswordResetModal({
  email,
  onConfirm,
  onClose,
  isResetting,
  resetError,
}) {
  return (
    <div className="password-reset-overlay">
      <div 
      className={`password-reset-modal ${resetError ? 'has-error' : ''}`}
      role="dialog" 
      aria-modal="true" 
      aria-label="비밀번호 재설정"
      >
        <button 
        type="button" 
        className="password-reset-close" 
        onClick={onClose}
        disabled={isResetting}
        aria-label="닫기"
        >
          <img src={closeIcon} alt="" />
        </button>

        <div className="password-reset-message">
          <strong>{email}</strong>
          <span>임시 비밀번호를 이메일로 발송합니다.</span>
          <span>비밀번호를 초기화하시겠습니까?</span>

          {resetError && (
            <p className="password-reset-error" role="alert">
            {resetError}
            </p>
          )}
        </div>

        <SmallButton 
        onClick={onConfirm}
        disabled={isResetting || !email}
        >
          확인
        </SmallButton>

      </div>
    </div>
  );
}

export default PasswordResetModal;