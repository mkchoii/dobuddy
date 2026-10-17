import SmallButton from "../common/SmallButton";
import './GoalLinkedWarningModal.css';

function GoalLinkedWarningModal({ message, onClose }) {
  return (
    <div className="goal-linked-warning-overlay">
      <div className="goal-linked-warning-modal">
        <p className="goal-linked-warning-title">{message}</p>
        <p className="goal-linked-warning-description">
          팀 스페이스에서 연동을 해제하세요.
        </p>
        <div className="goal-linked-warning-action">
          <SmallButton onClick={onClose}>
            확인
          </SmallButton>
        </div>
      </div>
    </div>
  );
}

export default GoalLinkedWarningModal;