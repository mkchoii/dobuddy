import { useState } from 'react';

import SmallButton from '../common/SmallButton';
import { profileImages } from '../../assets/profile/profileImages';
import closeIcon from '../../assets/icons/close.svg';

import './ProfileImageModal.css';

function ProfileImageModal({
    currentProfileImageId,
    onClose,
    onComplete,
}) {
    const [selectedImageId, setSelectedImageId] =
        useState(currentProfileImageId);

    const handleComplete = () => {
        onComplete(selectedImageId);
    };

    return (
        <div className="profile-modal-overlay">
            <div className="profile-image-modal">

                <button
                    type="button"
                    className="profile-modal-close"
                    onClick={onClose}
                    aria-label="닫기"
                >
                    <img src={closeIcon} alt="" />
                </button>

                <p className="profile-modal-title">
                    변경할 이미지를 선택하세요.
                </p>

                <div className="profile-image-options">
                    {[1, 2, 3, 4, 5, 6].map((id) => (
                        <button
                            key={id}
                            type="button"
                            className={`profile-image-option ${
                                selectedImageId === id
                                    ? 'selected'
                                    : ''
                            }`}
                            onClick={() => setSelectedImageId(id)}
                        >
                            <img
                                src={profileImages[id]}
                                alt={`프로필 이미지 ${id}`}
                            />
                        </button>
                    ))}
                </div>

                <SmallButton onClick={handleComplete}>
                    완료
                </SmallButton>

            </div>
        </div>
    );
}

export default ProfileImageModal;