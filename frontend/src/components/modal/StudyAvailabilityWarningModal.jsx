import SmallButton from "../common/SmallButton";
import './StudyAvailabilityWarningModal.css';

function StudyAvailabilityWarningModal({ studyName, onClose }) {
  return (
    <div className="study-warning-overlay">
      <div className="study-warning-modal">
        <p className="study-warning-title">
          {studyName}
        </p>
        <p className="study-warning-description">
          정원이 초과되었습니다.
        </p>
        <div className="study-warning-action">
          <SmallButton onClick={onClose}>
            확인
          </SmallButton>
        </div>
      </div>
    </div>
  );
}

export default StudyAvailabilityWarningModal;