import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { 
    getStudyDashboard, 
    updateStudy, 
    getStudyMemberDetail, 
    deleteStudyMember,
    deleteStudy
 } from '../api/studyApi';
 import { handleApiError } from '../handleApiError';

import { CATEGORIES } from '../categories';

import AdminHeader from '../components/layout/AdminHeader';
import CategoryChip from '../components/CategoryChip';
import StudyMemberRow from '../components/study/StudyMemberRow';
import vectorLeftIcon from '../assets/icons/vector-left.svg';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import SmallButton from '../components/common/SmallButton';
import StudyMemberDetailModal from '../components/modal/StudyMemberDetailModal';
import StudyMemberDeleteModal from '../components/modal/StudyMemberDeleteModal';
import AdminStudyDeleteModal from '../components/modal/AdminStudyDeleteModal';

import './AdminStudyManagePage.css';
import './StudyListPage.css';

function AdminStudyManagePage() {
    const { studyId } = useParams();
    const navigate = useNavigate();

    const [study, setStudy] = useState(null);
    const [studyName, setStudyName] = useState('');
    const [category, setCategory] = useState('');
    const [maxMembers, setMaxMembers] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);

    const [error, setError] = useState('');
    const [nameError, setNameError] = useState('');
    const [membersError, setMembersError] = useState('');

    const [selectedMember, setSelectedMember] = useState(null);
    const [isMemberLoading, setIsMemberLoading] = useState(false);
    const [memberError, setMemberError] = useState('');

    const [deleteTarget, setDeleteTarget] = useState(null);
    const [isDeleting, setIsDeleting] = useState(false);

    const [isStudyDeleteModalOpen, setIsStudyDeleteModalOpen] = useState(false);
    const [isStudyDeleting, setIsStudyDeleting] = useState(false);

    useEffect(() => {
        let cancelled = false;

        const loadStudy = async () => {
            try {
                setIsLoading(true);
                setError('');

                const data = await getStudyDashboard(studyId);

                if (cancelled) return;

                setStudy(data);
                setStudyName(data.studyName);
                setCategory(data.category);
                setMaxMembers(String(data.maxMembers));
            } catch (err) {
                if (handleApiError(error)) return;
                if (!cancelled) {
                    setError(err.message);
                }
            } finally {
                if (!cancelled) {
                    setIsLoading(false);
                }
            }
        };

        loadStudy();

        return () => {
            cancelled = true;
        };
    }, [studyId]);

    const handleUpdate = async () => {
        const name = studyName.trim();
        const members = Number(maxMembers);

        setNameError('');
        setMembersError('');

        if (!name) {
            setNameError('스터디 이름을 입력해 주세요.');
            return;
        }

        if (
            maxMembers.trim() === '' ||
            !Number.isInteger(members) ||
            members < 1 ||
            members < (study?.currentMembers ?? 0)
        ) {
            setMembersError('최대 인원은 현재 참여 인원 이상인 정수여야 합니다.');
            return;
        }

        try {
            setIsSaving(true);
            setError('');

            await updateStudy(studyId, {
                studyName: name,
                category,
                maxMembers: members,
            });

            navigate('/admin', { replace: true });
        } catch (err) {
            if (handleApiError(error)) return;
            setError(err.message);
        } finally {
            setIsSaving(false);
        }
    };

    const handleMemberClick = async (member) => {
        try {
            setIsMemberLoading(true);
            setMemberError('');

            const data = await getStudyMemberDetail(
                studyId,
                member.studyMemberId
            );

            setSelectedMember(data);
        } catch (err) {
            if (handleApiError(error)) return;
            setMemberError(err.message);
        } finally {
            setIsMemberLoading(false);
        }
    };

    // 강퇴하기 버튼 클릭 → 강퇴 확인 팝업 표시
    const handleOpenDeleteModal = (member) => {
        setSelectedMember(null);
        setDeleteTarget(member);
    };

    // 강퇴 확인 버튼 클릭 → DELETE API 호출
    const handleDeleteMember = async () => {
        if (!deleteTarget || isDeleting) return;

        try {
            setIsDeleting(true);

            await deleteStudyMember(
                studyId,
                deleteTarget.studyMemberId
            );

            setStudy((prev) => ({
                ...prev,
                currentMembers: prev.currentMembers - 1,
                members: prev.members.filter(
                    (member) =>
                        member.studyMemberId !== deleteTarget.studyMemberId
                ),
            }));

            setDeleteTarget(null);
        } catch (err) {
            if (handleApiError(error)) return;
            alert(err.message);
        } finally {
            setIsDeleting(false);
        }
    };

    const handleDeleteStudy = async () => {
        if (isStudyDeleting) return;

        try {
            setIsStudyDeleting(true);
            await deleteStudy(studyId);
            navigate('/admin', { replace: true });
        } catch (err) {
            if (handleApiError(error)) return;
            alert(err.message);
        } finally {
            setIsStudyDeleting(false);
        }
    };

    const sortedMembers = [...(study?.members ?? [])].sort(
        (a, b) =>
            (b.achievementRate ?? 0) -
            (a.achievementRate ?? 0)
    );

    return (
        <>
            <AdminHeader />
            <div className="study-list-page admin-study-manage-page">
                <div className="study-list-container admin-study-manage-container">

                    <main className="admin-study-manage-content">
                        <div className="admin-study-manage-heading">
                            <button
                                type="button"
                                className="admin-study-back-button"
                                onClick={() => navigate('/admin')}
                                aria-label="스터디 목록으로 돌아가기"
                            >
                                <img
                                src={vectorLeftIcon}
                                alt=""
                                className="admin-study-back-icon"
                                />
                            </button>

                            <h2>스터디 관리</h2>

                            <SmallButton 
                            onClick={() => setIsStudyDeleteModalOpen(true)}
                            >
                                삭제
                            </SmallButton>
                        </div>

                        {isLoading ? (
                            <p className="study-list-message">
                                스터디 정보를 불러오는 중입니다.
                            </p>
                        ) : !study ? (
                            <p className="study-list-message">
                                {error || '스터디 정보를 찾을 수 없습니다.'}
                            </p>
                        ) : (
                            <>
                                <section className="admin-study-edit-card">
                                    <Input
                                        type="text"
                                        placeholder="스터디 이름"
                                        value={studyName}
                                        onChange={(e) => {
                                        setStudyName(e.target.value)
                                        setNameError('');
                                        }}
                                        errorMessage={nameError}
                                        className="admin-study-input"
                                    />

                                    <div className="admin-study-category-list">
                                        {CATEGORIES.map((item) => (
                                            <CategoryChip
                                                key={item.value}
                                                category={item}
                                                isSelected={category === item.value}
                                                onClick={() => setCategory(item.value)}
                                            />
                                        ))}
                                    </div>

                                    <Input
                                        type="number"
                                        placeholder="최대 인원"
                                        value={maxMembers}
                                        onChange={(e) => {
                                        setMaxMembers(e.target.value)
                                        setMembersError('');
                                        }}
                                        errorMessage={membersError}
                                        className="admin-study-input"
                                    />

                                    <Button
                                        onClick={handleUpdate}
                                        disabled={isSaving}
                                    >
                                        {isSaving ? '수정 중...' : '수정하기'}
                                    </Button>

                                    {error && (
                                        <p className="admin-study-form-error">{error}</p>
                                    )}
                                </section>

                                <section className="admin-study-member-card">
                                    {sortedMembers.map((member) => (
                                        <StudyMemberRow
                                            key={member.studyMemberId}
                                            member={member}
                                            onMemberClick={handleMemberClick}
                                        />
                                    ))}
                                </section>
                                {isMemberLoading && (
                                    <p className="study-list-message">
                                        멤버 정보를 불러오는 중입니다.
                                    </p>
                                )}

                                {memberError && (
                                    <p className="admin-study-form-error">
                                        {memberError}
                                    </p>
                                )}
                            </>
                        )}
                    </main>
                    {selectedMember && (
                        <StudyMemberDetailModal
                            member={selectedMember}
                            goals={selectedMember.goals}
                            onClose={() => setSelectedMember(null)}
                            isAdmin
                            onKick={handleOpenDeleteModal}
                        />
                    )}
                    
                    {deleteTarget && (
                        <StudyMemberDeleteModal
                            member={deleteTarget}
                            onConfirm={handleDeleteMember}
                            onClose={() => setDeleteTarget(null)}
                            isDeleting={isDeleting}
                        />
                    )}

                    {isStudyDeleteModalOpen && (
                        <AdminStudyDeleteModal
                            study={study}
                            onConfirm={handleDeleteStudy}
                            onClose={() => setIsStudyDeleteModalOpen(false)}
                            isDeleting={isStudyDeleting}
                        />
                    )}
                </div>
            </div>
        </>
    );
}

export default AdminStudyManagePage;