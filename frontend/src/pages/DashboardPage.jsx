import { useEffect, useState } from 'react';
import { updateFinalGoal } from '../api/userApi';
import { createGoal, updateGoal, deleteGoal } from '../api/goalApi';

import UserHeader from '../components/layout/UserHeader';
import GoalSummaryCard from '../components/dashboard/GoalSummaryCard';
import PersonalizedProgressCard from '../components/dashboard/PersonalizedProgressCard';
import WeekCalendar from '../components/dashboard/WeekCalendar';
import TaskListCard from '../components/dashboard/TaskListCard';
import FinalGoalEditModal from '../components/modal/FinalGoalEditModal';
import GoalManagementModal from '../components/modal/GoalManagementModal';

import vectorIcon from '../assets/icons/vector.svg';
import goalIcon from '../assets/icons/goal.svg';

import {
    getDashboard,
    updateTaskCompletion,
    updateTask,
    deleteTask,
    createTask,
} from '../api/dashboardApi';

import './DashboardPage.css';
import { trackGoalCreated, trackGoalDeleted, trackGoalSummaryCardViewed, trackGoalUpdated, trackTaskCompleted, trackTaskCompletedCanceled, trackTaskCreated, trackTaskDeleted, trackTaskUpdated } from '../amplitude';

function DashboardPage() {
    const [dashboard, setDashboard] = useState(null);
    const [topCardMode, setTopCardMode] = useState('finalGoal');
    const [isFinalGoalEditModalOpen, setIsFinalGoalEditModalOpen] = useState(false);
    const [isGoalManagementOpen, setIsGoalManagementOpen] = useState(false);
    const [finalGoalInput, setFinalGoalInput] = useState('');
    const [selectedGoalId, setSelectedGoalId] = useState(null);

    // 대시보드 조회 및 날짜 선택
    const loadDashboard = async (date) => {
        try {
            const data = await getDashboard(date);
            setDashboard(data);
        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        loadDashboard();
    }, []);


    const handleSelectDate = (date) => {
        loadDashboard(date);
    };

    // 상단 카드 더보기
    const handleClickMore = () => {
        if (topCardMode === 'finalGoal') {
            trackGoalSummaryCardViewed();
            
            const goals = dashboard.goalProgress;

            if (goals.length > 0) {
                const randomIndex = Math.floor(
                    Math.random() * goals.length
                );

                setSelectedGoalId(
                    goals[randomIndex].goalId
                );
            }

            setTopCardMode('personalized');
        } else {
            setTopCardMode('finalGoal');
        }
    };

    // 최종 목표 수정 팝업 열기
    const handleOpenFinalGoalEditPopup = () => {
        setFinalGoalInput(dashboard.finalGoal ?? '');
        setIsFinalGoalEditModalOpen(true);
    };

    // 최종 목표 수정 팝업 닫기
    const handleCloseFinalGoalEditPopup = () => {
        setFinalGoalInput('');
        setIsFinalGoalEditModalOpen(false);
    };

    // 최종 목표 수정
    const handleConfirmFinalGoal = async () => {
        const trimmedValue = finalGoalInput.trim();

        // 빈 값이면 기존 값 유지하고 팝업만 닫기
        if (!trimmedValue) {
            setFinalGoalInput('');
            setIsFinalGoalEditModalOpen(false);
            return;
        }

        try {
            const updated = await updateFinalGoal(trimmedValue);

            setDashboard((prev) => ({
                ...prev,
                finalGoal: updated.finalGoal,
            }));

            setFinalGoalInput('');
            setIsFinalGoalEditModalOpen(false);
        } catch (error) {
            console.error(error);
        }
    };

    // 태스크 완료
    const handleCheckTask = async (taskId, isCompleted) => {
        try {
            const data = await updateTaskCompletion(
                taskId,
                !isCompleted
            );

            if (data.isNewlyCompleted) {
                trackTaskCompleted(data);
            } else if (data.isNewlyCanceled) {
                trackTaskCompletedCanceled(data);
            }

            setDashboard((prev) => ({
                ...prev,

                // 달성률 갱신
                weeklyProgress: data.weeklyProgress,
                goalProgress: data.goalProgress,

                // 태스크 완료 상태 갱신
                goals: prev.goals.map((goal) => ({
                    ...goal,
                    tasks: goal.tasks.map((task) =>
                        task.taskId === data.taskId
                            ? {
                                ...task,
                                isCompleted: data.isCompleted,
                            }
                            : task
                    ),
                })),
            }));
        } catch (error) {
            console.error(error);
        }
    };

    // 태스크 수정
    const handleEditTask = async (taskId, content) => {
        const originalTask = dashboard.goals
            .flatMap(goal => goal.tasks)
            .find(task => task.taskId === taskId);

        if (originalTask && content === originalTask.content) {
            return true; 
        }

        try {
            const data = await updateTask(taskId, content);

            trackTaskUpdated(data);

            setDashboard((prev) => ({
                ...prev,
                goals: prev.goals.map((goal) => ({
                    ...goal,
                    tasks: goal.tasks.map((task) =>
                        task.taskId === data.taskId
                            ? {
                                ...task,
                                content: data.content,
                            }
                            : task
                    ),
                })),
            }));

            return true;
        } catch (error) {
            console.error(error);
            return false;
        }
    };

    // 태스크 삭제
    const handleDeleteTask = async (taskId) => {
        try {
            const data = await deleteTask(taskId);

            trackTaskDeleted(data);

            setDashboard((prev) => ({
                ...prev,

                // 달성률 갱신
                weeklyProgress: data.weeklyProgress,
                goalProgress: data.goalProgress,

                // 태스크 삭제
                goals: prev.goals.map((goal) => ({
                    ...goal,
                    tasks: goal.tasks.filter(
                        (task) => task.taskId !== taskId
                    ),
                })),
            }));

            return true;
        } catch (error) {
            console.error(error);
            return false;
        }
    };

    // 태스크 추가
    const handleCreateTask = async (
        goalId,
        taskDate,
        content
    ) => {
        try {
            const data = await createTask(
                goalId,
                taskDate,
                content
            );

            trackTaskCreated(data);

            setDashboard((prev) => ({
                ...prev,

                // 달성률 갱신
                weeklyProgress: data.weeklyProgress,
                goalProgress: data.goalProgress,

                // 태스크 추가
                goals: prev.goals.map((goal) =>
                    goal.goalId === data.goalId
                        ? {
                            ...goal,
                            tasks: [
                                data,
                                ...goal.tasks,
                            ],
                        }
                        : goal
                ),
            }));

            return true;
        } catch (error) {
            console.error(error);
            return false;
        }
    };

    // 세부 목표 생성
    const handleCreateGoal = async (category, goalName) => {
        try {
            const createdGoal = await createGoal(category,goalName);

            const goalCategories = [
                ...dashboard.goals.map(goal => goal.category),
                createdGoal.category
            ];

            trackGoalCreated(createdGoal, goalCategories);

            setDashboard((prev) => ({
                ...prev,

                goals: [
                    {
                        ...createdGoal,
                        tasks: [],
                    },
                    ...prev.goals,
                ],

                goalProgress: [
                    {
                        goalId: createdGoal.goalId,
                        goalName: createdGoal.goalName,
                        category: createdGoal.category,
                        achievementRate: null,
                    },
                    ...prev.goalProgress,
                ],
            }));

            return true;
        } catch (error) {
            console.error(error);
            return false;
        }
    };

    // 세부 목표 수정
    const handleUpdateGoal = async (
        goalId,
        category,
        goalName
    ) => {
        try {
            const updatedGoal = await updateGoal(
                goalId, {
                category,
                goalName,
            });

            const goalCategories = dashboard.goals.map(goal => 
                goal.goalId === goalId 
                ? updatedGoal.category
                : goal.category
            );

            trackGoalUpdated(updatedGoal, goalCategories);

            setDashboard((prev) => ({
                ...prev,

                goals: prev.goals.map((goal) =>
                    goal.goalId === goalId
                        ? {
                            ...goal,
                            category: updatedGoal.category,
                            goalName: updatedGoal.goalName,
                        }
                        : goal
                ),

                goalProgress: prev.goalProgress.map((goal) =>
                    goal.goalId === goalId
                        ? {
                            ...goal,
                            category: updatedGoal.category,
                            goalName: updatedGoal.goalName,
                        }
                        : goal
                ),
            }));

            return true;
        } catch (error) {
            console.error(error);
            return false;
        }
    };

    // 세부 목표 삭제
    const handleDeleteGoal = async (goalId) => {
        try {
            const data = await deleteGoal(goalId);

            const remainingGoals = dashboard.goals.filter(
                goal => goal.goalId !== goalId
            );

            trackGoalDeleted(data, remainingGoals.map(goal => goal.category));

            setDashboard((prev) => ({
                ...prev,

                goals: prev.goals.filter(
                    (goal) => goal.goalId !== goalId
                ),

                goalProgress: prev.goalProgress.filter(
                    (goal) => goal.goalId !== goalId
                ),
            }));

            return true;
        } catch (error) {
            console.error(error);
            return false;
        }
    };

    if (!dashboard) {
        return <div className="dashboard-loading">대시보드를 불러오는 중입니다...</div>;
    }

    return (
        <>
            <UserHeader />
       
            <div className="dashboard-page">
                <div className="dashboard-container">

                    <main className="dashboard-content">
                        <button
                            type="button"
                            className="dashboard-more-button"
                            onClick={handleClickMore}
                        >
                            더보기

                            <img
                                src={vectorIcon}
                                alt=""
                                className="dashboard-more-icon"
                            />
                        </button>

                        {topCardMode === 'finalGoal' ? (
                            <GoalSummaryCard
                                finalGoal={dashboard.finalGoal}
                                weeklyProgress={dashboard.weeklyProgress}
                                onClickEditIcon={
                                    handleOpenFinalGoalEditPopup
                                }
                            />
                        ) : (
                            <PersonalizedProgressCard
                                nickname={dashboard.nickname}
                                goalProgress={dashboard.goalProgress}
                                selectedGoalId={selectedGoalId}
                            />
                        )}

                        <WeekCalendar
                            selectedDate={dashboard.selectedDate}
                            onSelectDate={handleSelectDate}
                        />

                        <button
                            type="button"
                            className="goal-manage-button"
                            onClick={() => setIsGoalManagementOpen(true)}
                        >
                            <img src={goalIcon} alt="" className="goal-manage-icon" />
                            <span>세부목표 관리</span>
                        </button>

                        <TaskListCard
                            selectedDate={dashboard.selectedDate}
                            goals={dashboard.goals}
                            onCheckTask={handleCheckTask}
                            onEditTask={handleEditTask}
                            onDeleteTask={handleDeleteTask}
                            onCreateTask={handleCreateTask}
                        />
                    </main>

                    {isFinalGoalEditModalOpen && (
                        <FinalGoalEditModal
                            value={finalGoalInput}
                            onChangeText={setFinalGoalInput}
                            onConfirm={handleConfirmFinalGoal}
                            onClose={handleCloseFinalGoalEditPopup}
                        />
                    )}

                    {isGoalManagementOpen && (
                        <GoalManagementModal
                            goals={dashboard.goals}
                            onCreate={handleCreateGoal}
                            onUpdate={handleUpdateGoal}
                            onDelete={handleDeleteGoal}
                            onClose={() => setIsGoalManagementOpen(false)}
                        />
                    )}
                </div>
            </div>
        </>
    );
}

export default DashboardPage;