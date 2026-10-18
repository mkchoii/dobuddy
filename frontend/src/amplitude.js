import * as amplitude from '@amplitude/analytics-browser';

// 페이지명
const getPageName = () => {
  const path = window.location.pathname;

  const pageMap = {
    '/': 'LoginEmailPage',
    '/login': 'LoginEmailPage',
    '/signup/password': 'PasswordSetupPage',
    '/login/password': 'PasswordLoginPage',
    '/goal-setting': 'GoalSettingPage',
    '/signup-complete': 'SignupCompletePage',
    '/mypage': 'MyPage',
    '/password-change': 'PasswordChangePage',
    '/dashboard': 'DashboardPage',
    '/study/list': 'StudyListPage'
  };

  return pageMap[path] ?? 'UnknownPage';
};

// 유입 채널
const CHANNEL_MAP = {
    everytime: 'campus_community',
    campuspick: 'campus_community',
    linkareer: 'job_site',
    comento: 'job_site',
    jasoseol: 'job_site',
    naver_cafe: 'naver_cafe',
    kakao_openchat: 'sns',
    threads: 'sns'
};

const STORAGE_KEY = 'channel';

// 세부목표명
const GOAL_CATEGORY_MAP = {
    CERTIFICATE: 'certificate',
    LANGUAGE: 'language',
    INTERVIEW: 'interview',
    PORTFOLIO: 'resume_portfolio',
    CAREER_EXPLORATION: 'job_research',
    ACTIVITY_PROJECT: 'activity_project',
    STUDY: 'study'
};


// SDK 초기화
export const initAmplitude = () => {
    amplitude.init(import.meta.env.VITE_AMPLITUDE_API_KEY, {
        autocapture: {
          attribution: true,
          pageViews: false,
          sessions: false,
          elementInteractions: false,
          fileDownloads: false,
          formInteractions: false,
        },
        minIdLength: 1,
    });
};

// 이벤트 전송
export const trackEvent = (eventName, properties = {}) => {
    amplitude.track(eventName, {
      page_name: getPageName(),
      release_version: import.meta.env.VITE_APP_VERSION,
      ...properties,
    });
};

// 사용자 ID 설정
export const setAmplitudeUserId = (userId) => {
  amplitude.setUserId(String(userId));
};

// 사용자 식별 초기화
export const resetAmplitude = () => {
    amplitude.reset();
};

export const flushAmplitude = async () => {
    await amplitude.flush().promise;
};

// 사용자 속성
export const setUserProperties = (properties) => {
  const identify = new amplitude.Identify();

  Object.entries(properties).forEach(([key, value]) => {
    identify.set(key, value);
  });

  amplitude.identify(identify);
};

export const setUserPropertiesOnce = (properties) => {
    const identify = new amplitude.Identify();

    Object.entries(properties).forEach(([key, value]) => {
        identify.setOnce(key, value);
    });

    amplitude.identify(identify);
};

// 최초 유입 채널 저장
export const saveChannel = () => {
  const savedChannel = localStorage.getItem(STORAGE_KEY);

  if (savedChannel) return;

  const params = new URLSearchParams(window.location.search);
  const source = params.get('utm_source')?.toLowerCase();

  const channel = CHANNEL_MAP[source];

  if (channel) {
    localStorage.setItem(STORAGE_KEY, channel);
  }
};

// 저장된 채널 조회
export const getChannel = () => {
  return localStorage.getItem(STORAGE_KEY);
}

// Goal_Created 이벤트
export const trackGoalCreated = (data, goalCategories) => {
  setUserProperties({
    goal_categories: [
      ...new Set(
        goalCategories.map(category => GOAL_CATEGORY_MAP[category])
      )
    ]
  });

  if (data.isFirstGoal) {
    setUserPropertiesOnce({
      first_goal_created_at: data.createdAt
    });
  }
  
  trackEvent('Goal_Created', {
    goal_id: data.goalId,
    goal_category: GOAL_CATEGORY_MAP[data.category],
    is_first_goal: data.isFirstGoal,
    is_signup_session: data.isSignupSession,
    signup_minute_count: data.signupMinuteCount
  });
};