import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getEntryStatus } from "../api/authApi";
import { clearDemoSession } from "../demoDashboard";


function EntryPage() {
  const navigate = useNavigate();

  useEffect(() => {
    const checkEntryStatus = async () => {
      try {
        const data = await getEntryStatus();

        // 로그인 세션 유효함
        if (data.isLoggedIn) {
          sessionStorage.removeItem('isDemo');
          navigate('/dashboard', { replace: true });
          return;
        }

        // 로그인 세션 없음. 가입/로그인 이력 있음
        if (data.hasAuthHistory) {
          sessionStorage.removeItem('isDemo');
          navigate('/login', { replace: true });
          return;
        }

        // 첫 방문
        clearDemoSession();
        sessionStorage.setItem('isDemo', 'true');
        navigate('/goal-setting', { replace: true });
      } catch (error) {
        console.error('진입 상태 확인 오류: ', error);
      }
    };
    
    checkEntryStatus();
  }, [navigate]);

  return <div>사용자 정보를 확인하고 있습니다...</div>;
}
export default EntryPage;