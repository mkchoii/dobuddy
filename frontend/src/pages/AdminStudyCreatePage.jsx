import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createStudy } from '../api/studyApi';

import { CATEGORIES } from '../categories';

import AdminHeader from '../components/layout/AdminHeader';
import CategoryChip from '../components/CategoryChip';
import Input from '../components/common/Input';
import Button from '../components/common/Button';

import vectorLeftIcon from '../assets/icons/vector-left.svg';

import './AdminStudyCreatePage.css';

function AdminStudyCreatePage() {
    const navigate = useNavigate();

    const [studyName, setStudyName] = useState('');
    const [category, setCategory] = useState('');
    const [maxMembers, setMaxMembers] = useState('10');

    const [nameError, setNameError] = useState('');
    const [categoryError, setCategoryError] = useState('');
    const [membersError, setMembersError] = useState('');
    const [error, setError] = useState('');
    const [isCreating, setIsCreating] = useState(false);

    const handleCreate = async () => {
        const name = studyName.trim();
        const members = Number(maxMembers);

        setNameError('');
        setCategoryError('');
        setMembersError('');
        setError('');

        let isValid = true;

        if (!name || name.length > 10) {
            setNameError('스터디명은 1~10자로 입력해 주세요.');
            isValid = false;
        }

        if (!category) {
            setCategoryError('카테고리를 선택해 주세요.');
            isValid = false;
        }

        if (
            maxMembers.trim() === '' ||
            !Number.isInteger(members) ||
            members < 2 ||
            members > 20
        ) {
            setMembersError('정원은 2~20명 사이의 정수여야 합니다.');
            isValid = false;
        }

        if (!isValid) return;

                try {
            setIsCreating(true);
            setError('');

            await createStudy(name, category, members);

            navigate('/admin', { replace: true });
        } catch (err) {
            if (handleApiError(error)) return;
            setError(err.message);
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <>
            <AdminHeader />
            <div className="study-list-page admin-study-create-page">
                <div className="study-list-container admin-study-create-container">
                    <main className="admin-study-create-content">
                        <div className="admin-study-create-heading">
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

                            <h2>스터디 개설</h2>
                        </div>

                        <section className="admin-study-create-card">
                            <div className="admin-study-create-field">
                                <label className="admin-study-create-label">
                                    스터디명
                                </label>

                                <Input
                                    type="text"
                                    placeholder="스터디명 입력(최대 10자)"
                                    value={studyName}
                                    onChange={(e) => {
                                        setStudyName(e.target.value);
                                        setNameError('');
                                    }}
                                    errorMessage={nameError}
                                />
                            </div>

                            <div className="admin-study-create-field">
                                <p className="admin-study-create-label">
                                    카테고리 선택
                                </p>

                                <div className="admin-study-create-categories">
                                    {CATEGORIES.map((item) => (
                                        <CategoryChip
                                            key={item.value}
                                            category={item}
                                            isSelected={category === item.value}
                                            onClick={() => {
                                                setCategory(item.value);
                                                setCategoryError('');
                                            }}
                                        />
                                    ))}
                                </div>

                                {categoryError && (
                                    <p className="admin-study-create-error">
                                        {categoryError}
                                    </p>
                                )}
                            </div>

                            <div className="admin-study-create-field">
                                <label className="admin-study-create-label">
                                    정원
                                </label>

                                <Input
                                    type="number"
                                    placeholder="최소 2명~최대 20명, 기본 10명"
                                    value={maxMembers}
                                    onChange={(e) => {
                                        setMaxMembers(e.target.value);
                                        setMembersError('');
                                    }}
                                    errorMessage={membersError}
                                />
                            </div>
                        </section>

                        <div className="admin-study-create-submit">
                            <Button
                                onClick={handleCreate}
                                disabled={isCreating}
                            >
                                {isCreating ? '개설 중...' : '개설하기'}
                            </Button>

                            {error && (
                                <p className="admin-study-create-error">
                                    {error}
                                </p>
                            )}
                        </div>
                    </main>
                </div>
            </div>
        </>
    );
}

export default AdminStudyCreatePage;