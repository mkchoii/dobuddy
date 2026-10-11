import SmallButton from "../common/SmallButton";
import "./PasswordResetModal.css";

function PasswordResetCompleteModal({
  onConfirm,
}) {
  return (
    <div className="password-reset-overlay">
      <div 
      className="password-reset-modal" 
      role="dialog" 
      aria-modal="true" 
      aria-label="비밀번호 재설정 확인"
      >

        <div className="password-reset-complete-message">
          <span>비밀번호 발송이 완료되었습니다.</span>
          <span>이메일을 확인해주세요.</span>
        </div>

        <SmallButton 
        onClick={onConfirm}
        >
          확인
        </SmallButton>

      </div>
    </div>
  );
}

export default PasswordResetCompleteModal;