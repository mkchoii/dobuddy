import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import UserHeader from '../components/layout/UserHeader';
import { logout } from '../api/authApi';
import { getMe, updateProfileImage, deleteUser } from '../api/userApi';
import { profileImages } from '../assets/profile/profileImages';
import ProfileImageModal from '../components/modal/ProfileImageModal';
import WithdrawModal from '../components/modal/WithdrawModal';

import './MyPage.css';

function MyPage() {
    const [user, setUser] = useState(null);
    const [isProfileModalOpen, setIsProfileModalOpen] = useState(false);
    const [isLoggingOut, setIsLoggingOut] = useState(false);
    const [isWithdrawModalOpen, setIsWithdrawModalOpen] = useState(false);
    const [isWithdrawing, setIsWithdrawing] = useState(false);
    const [withdrawError, setWithdrawError] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        const loadUser = async () => {
            try {
                const data = await getMe();
                setUser(data);
            } catch (error) {
                console.error(error);
            }
        };

        loadUser();
    }, []);

    const handleLogout = async () => {
        if (isLoggingOut) return;

        setIsLoggingOut(true);

        try {
            await logout();
            navigate('/login', { replace: true });
        } catch (error) {
            console.error(error);
            setIsLoggingOut(false);
        }
    };

    const handleWithdraw = async () => {
        if (isWithdrawing) return;

        setIsWithdrawing(true);
        setWithdrawError('');

        try {
            await deleteUser();
        } catch (error) {
            console.error(error);
            setWithdrawError('탈퇴에 실패했습니다. 다시 시도해주세요');
            setIsWithdrawing(false);
            return;
        }

        try {
            await logout();
        } catch (error) {
            console.error(error);
        }

        navigate('/login', { replace: true });
    };

    const handleProfileImageChange = async (profileImageId) => {
        try {
            await updateProfileImage(profileImageId);

            setUser((prev) => ({
                ...prev,
                profileImageId,
            }));

            setIsProfileModalOpen(false);
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <>
            <UserHeader />
            <div className="mypage">
                <div className="mypage-container">
                    <main className="mypage-content">

                        <img
                            className="profile-image"
                            src={profileImages[user?.profileImageId] ?? profileImages[1]}
                            alt="프로필"
                        />

                        <button
                            type="button"
                            className="profile-image-change"
                            onClick={() => setIsProfileModalOpen(true)}
                        >
                            프로필 이미지 변경
                        </button>

                        <div className="profile-info">
                            <p className="profile-nickname">
                                {user?.nickname ?? ''}
                            </p>

                            <p className="profile-email">
                                {user?.email ?? ''}
                            </p>
                        </div>

                        <div className="mypage-menu">
                            <button 
                                type="button"
                                onClick={() => navigate('/password-change')}
                            >
                                비밀번호 변경
                            </button>

                            <button
                                type="button"
                                onClick={handleLogout}
                                disabled={isLoggingOut}
                            >
                                로그아웃
                            </button>

                            <button
                                type="button"
                                onClick={() => {
                                    setWithdrawError('');
                                    setIsWithdrawModalOpen(true);
                                }}
                            >
                                탈퇴하기
                            </button>
                        </div>

                    </main>

                    {isWithdrawModalOpen && (
                        <WithdrawModal
                            onWithdraw={handleWithdraw}
                            onClose={() => setIsWithdrawModalOpen(false)}
                            isWithdrawing={isWithdrawing}
                            errorMessage={withdrawError}
                        />
                    )}

                    {isProfileModalOpen && (
                        <ProfileImageModal
                            currentProfileImageId={user?.profileImageId}
                            onClose={() => setIsProfileModalOpen(false)}
                            onComplete={handleProfileImageChange}
                        />
                    )}
                </div>
            </div>
        </>
    );
}

export default MyPage;
