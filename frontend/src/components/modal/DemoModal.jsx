import SmallButton from '../common/SmallButton';
import { useNavigate } from 'react-router-dom';
import { trackGuestModalCanceled, trackGuestModalSignupClicked } from '../../amplitude';
import './Demomodal.css';

function DemoModal({ 
  onClose,
  feature
}) {
    const navigate = useNavigate();

    const handleSignupClick = () => {
        trackGuestModalSignupClicked({ feature });
        navigate('/signup');
    };

    const handleCancel = () => {
        trackGuestModalCanceled({ feature });
        onClose();
    }

    return (
        <div className="demo-modal-overlay">
            <div
                className="demo-modal"
                role="dialog"
                aria-modal="true"
            >
                <p className="demo-modal-title">
                    회원가입 후 이용할 수 있습니다.
                </p>

                <p className="demo-modal-description">
                    체험 중 입력한 내용은 저장되지 않습니다.
                </p>

                <div className="demo-modal-actions">
                    <SmallButton onClick={handleSignupClick}>
                        가입하기
                    </SmallButton>
                    <SmallButton onClick={handleCancel}>
                        나중에
                    </SmallButton>
                </div>
            </div>
        </div>
    );
}

export default DemoModal;
