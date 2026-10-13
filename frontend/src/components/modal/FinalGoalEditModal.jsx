import './FinalGoalEditModal.css';

import SmallButton from '../common/SmallButton';
import closeIcon from '../../assets/icons/close.svg';

function FinalGoalEditModal({
    value,
    onChangeText,
    onConfirm,
    onClose,
}) {
    const isTooLong = value.length > 15;

    return (
        <div className="final-goal-modal-overlay">
            <div className="final-goal-edit-modal">
                <button
                    type="button"
                    className="final-goal-modal-close"
                    onClick={onClose}
                    aria-label="팝업 닫기"
                >
                    <img
                        src={closeIcon}
                        alt=""
                        className="final-goal-modal-close-icon"
                    />
                </button>

                <div className="final-goal-input-area">
                    <input
                        type="text"
                        className={`final-goal-edit-input ${isTooLong ? 'error' : ''}`}
                        value={value}
                        onChange={(e) => onChangeText(e.target.value)}
                        aria-invalid={isTooLong}
                    />

                    {isTooLong && (
                        <p className="final-goal-edit-error" role="alert">
                            최종 목표는 15자 이내로 입력해주세요.
                        </p>
                    )}
                </div>
                

                <SmallButton onClick={onConfirm} disabled={isTooLong}>
                    수정하기
                </SmallButton>
            </div>
        </div>
    );
}

export default FinalGoalEditModal;