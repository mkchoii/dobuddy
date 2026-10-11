import SmallButton from "../common/SmallButton";
import "./AdminStudyDeleteModal.css";

function AdminStudyDeleteModal({
  study,
  onConfirm,
  onClose,
  isDeleting,
}) {
  return (
    <div className="study-delete-overlay">
      <div 
        className="study-delete-modal"
        role="dialog"
        aria-modal="true"
        aria-label="스터디 삭제 확인"
      >
        <div className="study-delete-message">
          <strong>{study.studyName}</strong>
          <span>정말 삭제하시겠습니까?</span>
        </div>

        <div className="study-delete-buttons">
          <SmallButton 
            onClick={onConfirm}
            disabled={isDeleting}
          >
            {isDeleting ? '삭제 중...' : '삭제'}
          </SmallButton>

          <SmallButton 
            onClick={onClose} 
            disabled={isDeleting}
          >
            취소
          </SmallButton>
        </div>
      </div>
    </div>
  );
}

export default AdminStudyDeleteModal;