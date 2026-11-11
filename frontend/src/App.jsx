import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useEffect } from 'react';
import EntryPage from './pages/EntryPage';
import LoginEmailPage from './pages/LoginEmailPage';
import SignupPage from './pages/SignupPage';
import PasswordSetupPage from './pages/PasswordSetupPage';
import PasswordLoginPage from './pages/PasswordLoginPage';
import GoalSettingPage from './pages/GoalSettingPage';
import SignupCompletePage from './pages/SignupCompletePage';
import MyPage from './pages/MyPage';
import PasswordChangePage from './pages/PasswordChangePage';
import DashboardPage from './pages/DashboardPage';
import StudyPage from './pages/StudyPage';
import StudyListPage from './pages/StudyListPage';
import AdminStudyListPage from './pages/AdminStudyListPage';
import AdminStudyManagePage from './pages/AdminStudyManagePage';
import AdminStudyCreatePage from './pages/AdminStudyCreatePage';
import PageTracker from './PageTracker';
import DemoLayout from './components/layout/DemoLayout';

function App() {
    const params = new URLSearchParams(window.location.search);
    const campaign = params.get('utm_campaign');
    
    return (
        <BrowserRouter>
            <PageTracker />
            <Routes>
                <Route path="/" element={campaign === 'testB' 
                    ? <Navigate to={`/entry${window.location.search}`} replace />
                    : <Navigate to={`/login${window.location.search}`} replace />
                    }   
                />
                <Route path="/entry" element={<EntryPage />}/>

                <Route path="/login" element={<LoginEmailPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/signup/password" element={<PasswordSetupPage />} />
                <Route path="/login/password" element={<PasswordLoginPage />} />

                <Route element={<DemoLayout />}>
                    <Route path="/goal-setting" element={<GoalSettingPage />} />
                    <Route path="/dashboard" element={<DashboardPage />} />
                    <Route path="/study/*" element={<StudyPage />} />
                </Route>
                
                <Route path="/signup-complete" element={<SignupCompletePage />} />
                <Route path="/mypage" element={<MyPage />} />
                <Route path="/password-change" element={<PasswordChangePage />} />
                
                <Route path="/study/*" element={<StudyPage />} />
                <Route path="/admin" element={<AdminStudyListPage />} />
                <Route path="/admin/studies/:studyId" element={<AdminStudyManagePage />} />
                <Route path="/admin/studies/new" element={<AdminStudyCreatePage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;