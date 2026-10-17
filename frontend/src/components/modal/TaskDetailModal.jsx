import './TaskDetailModal.css';

import SmallButton from '../common/SmallButton';
import closeIcon from '../../assets/icons/close.svg';

function TaskDetailModal({
    text,
    onChangeText,
    onEdit,
    onDelete,
    onClose,
}) {
    const isTooLong = text.length > 20;

    return (
        <div className="task-detail-modal-overlay">
            <div className="task-detail-modal">
                <button
                    type="button"
                    className="task-detail-modal-close"
                    onClick={onClose}
                    aria-label="팝업 닫기"
                >
                    <img
                        src={closeIcon}
                        alt=""
                        className="task-detail-modal-close-icon"
                    />
                </button>
                <div className="task-detail-modal-content">
                    <div className="task-detail-modal-input-area">
                        <input
                            type="text"
                            className={`task-detail-modal-input ${isTooLong ? 'error' : ''}`}
                            value={text}
                            onChange={(e) => onChangeText(e.target.value)}
                        />

                        {isTooLong && (
                            <p className="task-detail-modal-error">
                                태스크는 20자 이내로 입력해주세요.
                            </p>
                        )}
                    </div>
                    

                    <div className="task-detail-modal-buttons">
                        <SmallButton onClick={onEdit} disabled={isTooLong}>
                            수정하기
                        </SmallButton>

                        <SmallButton onClick={onDelete}>
                            삭제하기
                        </SmallButton>
                    </div>
                </div>
                
            </div>
        </div>
    );
}

export default TaskDetailModal;