import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getStudies } from '../api/studyApi';
import { handleApiError } from '../handleApiError';

import AdminHeader from '../components/layout/AdminHeader';
import StudyCategoryFilter from '../components/study/StudyCategoryFilter';
import StudyCard from '../components/study/StudyCard';
import addBlackIcon from '../assets/icons/add-black.svg';

import './StudyListPage.css';
import './AdminStudyListPage.css';

function AdminStudyListPage() {
    const navigate = useNavigate();

    const [selectedCategory, setSelectedCategory] = useState('ALL');
    const [studies, setStudies] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        let cancelled = false;

        const loadStudies = async () => {
            try {
                setIsLoading(true);

                const data = await getStudies(selectedCategory);

                if (!cancelled) {
                    setStudies(data.studies ?? []);
                }
            } catch (error) {
                if (handleApiError(error)) return;
                console.error(error);

                if (!cancelled) {
                    setStudies([]);
                }
            } finally {
                if (!cancelled) {
                    setIsLoading(false);
                }
            }
        };

        loadStudies();

        return () => {
            cancelled = true;
        };
    }, [selectedCategory]);

    const handleManageStudy = (study) => {
        navigate(`/admin/studies/${study.studyId}`);
    };

    return (
        <>
            <AdminHeader />
            <div className="study-list-page admin-study-list-page">
                <div className="study-list-container admin-study-list-container">

                    <main className="study-list-content admin-study-list-content">
                        <StudyCategoryFilter
                            selectedCategory={selectedCategory}
                            onSelectCategory={setSelectedCategory}
                        />

                        {isLoading ? (
                            <p className="study-list-message">
                                스터디를 불러오는 중입니다.
                            </p>
                        ) : studies.length === 0 ? (
                            <p className="study-list-message">
                                해당 카테고리의 스터디가 없습니다.
                            </p>
                        ) : (
                            <div className="study-card-list">
                                {studies.map((study) => (
                                    <StudyCard
                                        key={study.studyId}
                                        study={study}
                                        isAdmin
                                        onManage={handleManageStudy}
                                    />
                                ))}
                            </div>
                        )}
                    </main>
                    <button
                        type="button"
                        className="admin-study-add-button"
                        aria-label="스터디 추가"
                        onClick={() => navigate('/admin/studies/new')}
                    >
                        <img
                            src={addBlackIcon}
                            alt=""
                            className="admin-study-add-icon"
                        />
                    </button>
                </div>
            </div>
        </>
    );
}

export default AdminStudyListPage;