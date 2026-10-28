import * as amplitude from '@amplitude/analytics-browser';

// id 명명
const formatUserId = (userId) => {
  return `usr_${String(userId).padStart(6, '0')}`;
};

const formatGoalId = (goalId) => {
  return `gol_${String(goalId).padStart(6, '0')}`;
};

const formatStudyId = (studyId) => {
  return `std_${String(studyId).padStart(6, '0')}`;
};

const formatTaskId = taskId => {
  return `tsk_${String(taskId).padStart(6, '0')}`;
}

// 페이지명
export const getPageName = () => {
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
    '/study/list': 'StudyListPage',
    '/study/dashboard': 'StudyDashboardPage'
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
        sessionTimeout: 10*60*1000,
        autocapture: {
          attribution: true,
          pageViews: false,
          sessions: true,
          elementInteractions: false,
          fileDownloads: false,
          formInteractions: false,
        }
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
    amplitude.setUserId(formatUserId(userId));
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

// 진입 경로 저장
export const initEntrySource = () => {
    const params = new URLSearchParams(window.location.search);

    const entrySource =
        params.get('utm_source') === 'reminder'
            ? 'reminder'
            : 'direct';

    sessionStorage.setItem('entry_source', entrySource);
};

export const getEntrySource = () => {
    return sessionStorage.getItem('entry_source') || 'direct';
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
  
  console.log('서버 createdAt: ', data.createdAt);
  console.log('브라우저 현재 시간: ', new Date().toISOString('sv-SE', {timeZone: 'Asia/Seoul'}));

  const serverTime = new Date(`${data.createdAt}+09:00`).getTime();
console.log('브라우저 - 서버 시간 차이(초):',
  (Date.now() - serverTime) / 1000
);

  trackEvent('Goal_Created', {
    goal_id: formatGoalId(data.goalId),
    goal_category: GOAL_CATEGORY_MAP[data.category],
    is_first_goal: data.isFirstGoal,
    is_signup_session: data.isSignupSession,
    signup_minute_count: data.signupMinuteCount
  });
};

// Goal_Updated
export const trackGoalUpdated = (data, goalCategories) => {
  setUserProperties({
    goal_categories: [
      ...new Set(
        goalCategories.map(category => GOAL_CATEGORY_MAP[category])
      )
    ]
  });

  trackEvent('Goal_Updated', {
    goal_id: formatGoalId(data.goalId),
    goal_category: GOAL_CATEGORY_MAP[data.category],
    is_goal_category_updated: data.isGoalCategoryUpdated,
    is_goal_name_updated: data.isGoalNameUpdated,
    goal_created_day_count: data.goalCreatedDayCount,
    is_study_linked: data.isStudyLinked,
    linked_study_id: data.linkedStudyId == null ? null : formatStudyId(data.linkedStudyId)
  });
};

// Goal_Deleted
export const trackGoalDeleted = (data, goalCategories) => {
  setUserProperties({
    goal_categories: [
      ...new Set(
        goalCategories.map(category => GOAL_CATEGORY_MAP[category])
      )
    ]
  });

  trackEvent('Goal_Deleted', {
    goal_id: formatGoalId(data.goalId),
    goal_category: GOAL_CATEGORY_MAP[data.category],
    goal_created_day_count: data.goalCreatedDayCount,
    task_count: data.taskCount,
    completed_task_count: data.completedTaskCount
  })
};

// Task_Created
export const trackTaskCreated = (data) => {
  if (data.isFirstTask) {
      setUserPropertiesOnce({
          first_task_created_at: data.createdAt
      });
  }

  trackEvent('Task_Created', {
      goal_id: formatGoalId(data.goalId),
      task_id: formatTaskId(data.taskId),
      goal_category: GOAL_CATEGORY_MAP[data.category],
      is_study_linked: data.isStudyLinked,
      linked_study_id: data.linkedStudyId == null
          ? null
          : formatStudyId(data.linkedStudyId),
      is_first_task: data.isFirstTask,
      signup_day_count: data.signupDayCount,
      signup_minute_count: data.signupMinuteCount
  });
};

// Task_Deleted
export const trackTaskDeleted = (data) => {
    trackEvent('Task_Deleted', {
        goal_id: formatGoalId(data.goalId),
        task_id: formatTaskId(data.taskId),
        goal_category: GOAL_CATEGORY_MAP[data.category],
        is_study_linked: data.isStudyLinked,
        linked_study_id: data.linkedStudyId == null
            ? null
            : formatStudyId(data.linkedStudyId),
        is_task_completed: data.isTaskCompleted,
        task_created_day_count: data.taskCreatedDayCount
    });
};

// Task_Updated
export const trackTaskUpdated = (data) => {
    trackEvent('Task_Updated', {
        goal_id: formatGoalId(data.goalId),
        task_id: formatTaskId(data.taskId),
        goal_category: GOAL_CATEGORY_MAP[data.category],
        is_study_linked: data.isStudyLinked,
        linked_study_id: data.linkedStudyId == null
            ? null
            : formatStudyId(data.linkedStudyId),
        is_task_completed: data.isCompleted,
        task_created_day_count: data.taskCreatedDayCount
    });
};

// Task_Completed
export const trackTaskCompleted = (data) => {
    if (data.isFirstCompletion) {
        setUserPropertiesOnce({
            first_task_completed_at: data.completedAt
        });
    }

    trackEvent('Task_Completed', {
        goal_id: formatGoalId(data.goalId),
        task_id: formatTaskId(data.taskId),
        goal_category: GOAL_CATEGORY_MAP[data.category],
        is_study_linked: data.isStudyLinked,
        linked_study_id: data.linkedStudyId == null
            ? null
            : formatStudyId(data.linkedStudyId),
        is_first_completion: data.isFirstCompletion,
        signup_day_count: data.signupDayCount,
        signup_minute_count: data.signupMinuteCount,
        task_completed_date: data.completedAt.split('T')[0],
        entry_source: getEntrySource()
    });
};

// Task_Completed_Canceled
export const trackTaskCompletedCanceled = (data) => {
    trackEvent('Task_Completed_Canceled', {
        task_id: formatTaskId(data.taskId),
        goal_id: formatGoalId(data.goalId),
        goal_category: GOAL_CATEGORY_MAP[data.category],
        is_study_linked: data.isStudyLinked,
        linked_study_id: data.linkedStudyId == null
            ? null
            : formatStudyId(data.linkedStudyId),
        task_completed_date: data.completedAt?.split('T')[0] ?? null,
        completed_minute_count: data.completedMinuteCount
    });
};

// GoalSummaryCard_Viewed
export const trackGoalSummaryCardViewed = () => {
    trackEvent('GoalSummaryCard_Viewed');
};

export const trackStudyJoinSucceeded = (data) => {
    setUserProperties({
        is_study_joined: true,
        study_id: formatStudyId(data.studyId),
        linked_category: GOAL_CATEGORY_MAP[data.category]
    });

    if (data.isFirstStudy) {
        setUserPropertiesOnce({
            first_study_joined_at: data.firstStudyJoinedAt
        });
    }

    trackEvent('StudyJoin_Succeeded', {
        member_count: data.memberCount,
        goal_ids: data.goalIds.map(formatGoalId),
        goal_category: GOAL_CATEGORY_MAP[data.category],
        linked_study_id: formatStudyId(data.studyId),
        is_first_study_join: data.isFirstStudy,
        signup_day_count: data.signupDayCount,
        first_goal_minute_count: data.firstGoalMinuteCount,
        linked_goal_count: data.goalIds.length
    });
};

export const trackStudyJoinFailed = (data) => {
    trackEvent('StudyJoin_Failed', {
        fail_reason: data.failReason,
        goal_ids: data.goalIds.map(formatGoalId),
        goal_category: GOAL_CATEGORY_MAP[data.category],
        linked_study_id: formatStudyId(data.studyId)
    });
};

export const trackStudyExpelled = (data) => {
    trackEvent('Study_Expelled', {
        join_day_count: data.joinDayCount,
        member_count: data.memberCount,
        user_id: data.userId,
        linked_study_id: formatStudyId(data.studyId)
    });
};

export const trackStudyJoinModalCanceled = (data) => {
    trackEvent('StudyJoinModal_Canceled', {
        linked_study_id: formatStudyId(data.studyId),
        cancel_reason: data.cancelReason,
        goal_ids: data.goalIds == null
            ? null
            : data.goalIds.map(formatGoalId)
    });
};

export const trackStudyMemberModalViewed = (data) => {
    trackEvent('StudyMemberModal_Viewed', {
        linked_study_id: formatStudyId(data.studyId),
        viewed_user_id: data.viewedUserId,
        is_self_viewed: data.isSelfViewed
    });
};

export const trackLogoutSucceeded = async () => {
    await amplitude.track('Logout_Succeeded').promise;
    amplitude.reset();
};

export const trackApiRequestFailed = (data) => {
    trackEvent('ApiRequest_Failed', {
        request_name: data.requestName,
        status_code: data.statusCode,
        error_code: data.errorCode
    });
};

export const trackPageViewed = (previousPageName) => {
    trackEvent('Page_Viewed', {
        previous_page_name: previousPageName
    });
};

export const trackPageScrolled = (scrollDepth) => {
    trackEvent('Page_Scrolled', {
        scroll_depth: scrollDepth
    });
};