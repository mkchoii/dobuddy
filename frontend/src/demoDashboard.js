const STORAGE_KEY = 'demoDashboard';

const toDateString = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const getToday = () => {
    const parts = new Intl.DateTimeFormat('en-US', {
        timeZone: 'Asia/Seoul',
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    }).formatToParts(new Date());

    const getPart = (type) =>
        parts.find(part => part.type === type).value;

    return `${getPart('year')}-${getPart('month')}-${getPart('day')}`;
};

const readData = () => {
    const saved = sessionStorage.getItem(STORAGE_KEY);
    return saved ? JSON.parse(saved) : null;
};

const saveData = (data) => {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(data));
};

const makeId = () => crypto.randomUUID();

// 최초 체험판 대시보드 생성
function initializeDemoDashboard() {
    const existing = readData();
    if (existing) return existing;

    const initialGoal = JSON.parse(
        sessionStorage.getItem('demoGoal') || 'null'
    );

    const goals = initialGoal
        ? [{
            goalId: makeId(),
            category: initialGoal.category,
            goalName: initialGoal.goalName,
            isLinked: false,
            tasks: []
        }]
        : [];

    const data = {
        finalGoal: null,
        nickname: 'Guest',
        goals
    };

    saveData(data);
    return data;
}

// 선택 날짜가 속한 주의 월요일~일요일
function getWeekRange(dateString) {
    const date = new Date(`${dateString}T12:00:00`);
    const day = date.getDay();
    const mondayOffset = (day + 6) % 7;

    const monday = new Date(date);
    monday.setDate(date.getDate() - mondayOffset);

    const sunday = new Date(monday);
    sunday.setDate(monday.getDate() + 6);

    return {
        start: toDateString(monday),
        end: toDateString(sunday)
    };
}

function calculateProgress(goals) {

    const { start, end } = getWeekRange(getToday());

    // 이번 주 전체 태스크
    const weeklyTasks = goals
        .flatMap(goal => goal.tasks)
        .filter(task =>
            task.taskDate >= start &&
            task.taskDate <= end
        );
    
    // 주간 달성률
    const totalCount = weeklyTasks.length;

    const completedCount = weeklyTasks.filter(
        task => task.isCompleted
    ).length;

    const inProgressCount = totalCount - completedCount;

    const achievementRate = totalCount === 0
        ? 0
        : Math.trunc(completedCount * 100 / totalCount);

    const weeklyProgress = {
        achievementRate,
        inProgressCount,
        completedCount
    };

    // 목표별 달성률
    const goalProgress = goals.map(goal => {
        const goalWeeklyTasks = weeklyTasks.filter(
            task => task.goalId === goal.goalId
        );

        const goalTotalCount = goalWeeklyTasks.length;

        const goalCompletedCount = goalWeeklyTasks.filter(
            task => task.isCompleted
        ).length;

        const goalAchievementRate = goalTotalCount === 0
            ? null
            : Math.trunc(
                goalCompletedCount * 100 / goalTotalCount
            );

        return {
            goalId: goal.goalId,
            goalName: goal.goalName,
            category: goal.category,
            achievementRate: goalAchievementRate
        };
    });

    return { weeklyProgress, goalProgress };
}

// 기존 DashboardResponse 구조로 반환
export function getDemoDashboard(date = getToday()) {
    const data = initializeDemoDashboard();
    const { weeklyProgress, goalProgress } =
        calculateProgress(data.goals);

    return {
        finalGoal: data.finalGoal,
        nickname: data.nickname,
        selectedDate: date,
        weeklyProgress,
        goalProgress,
        goals: data.goals.map(goal => ({
            ...goal,
            tasks: goal.tasks.filter(
                task => task.taskDate === date
            )
        }))
    };
}

// 데이터 변경 후 저장
function mutateDemoData(callback) {
    const data = initializeDemoDashboard();
    callback(data);
    saveData(data);
}

// 최종 목표 수정
export function updateDemoFinalGoal(finalGoal) {
    mutateDemoData(data => {
        data.finalGoal = finalGoal;
    });
}

// 세부 목표 생성
export function createDemoGoal(category, goalName) {
    const goal = {
        goalId: makeId(),
        category,
        goalName,
        isLinked: false,
        tasks: []
    };

    mutateDemoData(data => {
        data.goals.unshift(goal);
    });

    return goal;
}

// 세부 목표 수정
export function updateDemoGoal(goalId, category, goalName) {
    mutateDemoData(data => {
        const goal = data.goals.find(
            goal => goal.goalId === goalId
        );

        if (!goal) throw new Error('목표를 찾을 수 없습니다.');

        goal.category = category;
        goal.goalName = goalName;
    });
}

// 세부 목표 삭제
export function deleteDemoGoal(goalId) {
    mutateDemoData(data => {
        data.goals = data.goals.filter(
            goal => goal.goalId !== goalId
        );
    });
}

// 태스크 생성
export function createDemoTask(goalId, taskDate, content) {
    const task = {
        taskId: makeId(),
        goalId,
        taskDate,
        content,
        isCompleted: false,
        createdAt: new Date().toISOString()
    };

    mutateDemoData(data => {
        const goal = data.goals.find(
            goal => goal.goalId === goalId
        );

        if (!goal) throw new Error('목표를 찾을 수 없습니다.');

        goal.tasks.unshift(task);
    });

    return task;
}

// 태스크 완료 상태 변경
export function updateDemoTaskCompletion(taskId, isCompleted) {
    mutateDemoData(data => {
        const task = data.goals
            .flatMap(goal => goal.tasks)
            .find(task => task.taskId === taskId);

        if (!task) throw new Error('태스크를 찾을 수 없습니다.');

        task.isCompleted = isCompleted;
    });
}

// 태스크 내용 수정
export function updateDemoTask(taskId, content) {
    mutateDemoData(data => {
        const task = data.goals
            .flatMap(goal => goal.tasks)
            .find(task => task.taskId === taskId);

        if (!task) throw new Error('태스크를 찾을 수 없습니다.');

        task.content = content;
    });
}

// 태스크 삭제
export function deleteDemoTask(taskId) {
    mutateDemoData(data => {
        data.goals.forEach(goal => {
            goal.tasks = goal.tasks.filter(
                task => task.taskId !== taskId
            );
        });
    });
}

// 데모 데이터 삭제
export const clearDemoSession = () => {
    sessionStorage.removeItem('isDemo');
    sessionStorage.removeItem('demoGoal');
    sessionStorage.removeItem('demoDashboard');
    sessionStorage.removeItem('guestStartedTracked');
};
