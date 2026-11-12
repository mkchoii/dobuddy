import CategoryChip from '../CategoryChip';
import { CATEGORIES } from '../../categories';
import peopleIcon from '../../assets/icons/people.svg';
import { trackEvent } from '../../amplitude';

function StudyProfileCard({ study }) {
    const category = CATEGORIES.find(
        (item) => item.value === study.category
    );

    return (
        <section className="study-profile-card">
            <h2 className="study-profile-name">
                {study.studyName}
            </h2>

            {study.openChatUrl && (
                <a
                    href={study.openChatUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="study-room-enter-button"
                    onClick={() => trackEvent('KakaoChat_Clicked')}
                >
                    스터디방 입장하기
                </a>
            )}

            <div className="study-profile-bottom">
                {category && (
                    <CategoryChip
                        category={category}
                        size="small"
                    />
                )}

                <div className="study-profile-members">
                    <img src={peopleIcon} alt="" />
                    <span>
                        {study.currentMembers}/{study.maxMembers}
                    </span>
                </div>
            </div>
        </section>
    );
}

export default StudyProfileCard;