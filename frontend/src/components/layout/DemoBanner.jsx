import { useNavigate } from 'react-router-dom';
import './DemoBanner.css';
import { trackGuestBannerClicked } from '../../amplitude';
import { track } from '@amplitude/analytics-browser';

function DemoBanner() {
    const navigate = useNavigate();

    return (
        <div className="demo-banner">
            <div className="demo-banner-info">
                <h2>서비스 체험 중입니다</h2>
                <p>모든 정보는 저장되지 않습니다</p>
            </div>

            <div className="demo-banner-actions">
                <button
                    type="button"
                    className="demo-banner-login"
                    onClick={() => {
                        trackGuestBannerClicked({ button: 'login' });
                        navigate('/login')
                    }}
                >
                    로그인
                </button>

                <button
                    type="button"
                    className="demo-banner-signup"
                    onClick={() => {
                        trackGuestBannerClicked({ button: 'signup' });
                        navigate('/signup')}}
                >
                    가입하기
                </button>
            </div>
        </div>
    );
}

export default DemoBanner;