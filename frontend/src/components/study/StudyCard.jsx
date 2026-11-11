import { CATEGORIES } from '../../categories';
import CategoryChip from '../CategoryChip';
import './StudyCard.css';
import peopleIcon from '../../assets/icons/people.svg';

function StudyCard({ 
  study, 
  isJoined = false, 
  onJoin, 
  isAdmin = false, 
  isDemo = false,
  onManage }) {
    const category = CATEGORIES.find(
        (item) => item.value === study.category
    );

    return (
        <div className="study-card">
            <p className="study-card-name">
                {study.studyName}
            </p>

            <div className="study-card-category">
                <CategoryChip
                  category={category}
                  size="small"
                />
            </div>
            
            <div className="study-card-bottom">
              <div className="study-card-member-info">
                <img
                  src={peopleIcon}
                  alt=""
                  className="study-member-icon"
                />

                <span className="study-member-count">
                    {study.currentMembers}/{study.maxMembers}
                </span>
              </div>

                <button
                    type="button"
                    className="study-join-button"
                    onClick={() => isAdmin ? onManage(study) : onJoin(study)}
                    disabled={!isAdmin && (
                        study.currentMembers >= study.maxMembers || 
                        (!isDemo && (isJoined || !study.canJoin))
                        )
                    }
                >
                    {isAdmin ? '관리' : '참여하기'}
                </button>
            </div>
        </div>
    );
}

export default StudyCard;