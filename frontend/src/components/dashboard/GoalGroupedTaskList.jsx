import { useState } from 'react';
import TaskRow from './TaskRow';

import CategoryChip from '../CategoryChip';
import TaskDetailModal from '../modal/TaskDetailModal';
import NewTaskRow from './NewTaskRow';

import chevronUpIcon from '../../assets/icons/chevron-up.svg';
import chevronDownIcon from '../../assets/icons/chevron-down.svg';
import addTaskIcon from '../../assets/icons/add.svg';

import './GoalGroupedTaskList.css';

function GoalGroupedTaskList({ 
    goal, 
    selectedDate,
    onCheckTask, 
    onEditTask, 
    onDeleteTask,
    onCreateTask
}) {
    const [isExpanded, setIsExpanded] = useState(true);
    const sortedTasks = [...goal.tasks].sort((a, b) => {
        if (a.isCompleted !== b.isCompleted) {
            return a.isCompleted ? 1 : -1;
        }

        return a.createdAt.localeCompare(b.createdAt);
    });

    const [openTaskId, setOpenTaskId] = useState(null);
    const [taskText, setTaskText] = useState('');

    const [isCreatingTask, setIsCreatingTask] = useState(false);

    const handleClickTask = (task) => {
        setOpenTaskId(task.taskId);
        setTaskText(task.content);
    };

    const handleCloseTaskModal = () => {
        setOpenTaskId(null);
        setTaskText('');
    };

    const handleEditTask = async () => {
        const trimmedText = taskText.trim();

        if (!trimmedText) {
            return;
        }

        const success = await onEditTask(
            openTaskId,
            trimmedText
        );

        if (success) {
            setOpenTaskId(null);
            setTaskText('');
        }
    };

    const handleDeleteTask = async () => {
        if (openTaskId === null) {
            return;
        }

        const success = await onDeleteTask(openTaskId);

        if (success) {
            setOpenTaskId(null);
            setTaskText('');
        }
    };

    const handleClickAddTask = () => {
        setIsExpanded(true);
        setIsCreatingTask(true);
    };

    const handleCreateTask = async (content) => {
        const success = await onCreateTask(
            goal.goalId,
            selectedDate,
            content
        );

        if (success) {
            setIsCreatingTask(false);
        }
    };

    const handleCancelCreateTask = () => {
        setIsCreatingTask(false);
    }

    return (
        <div className="goal-task-group">
            <div className="goal-task-header">
                <div className="goal-task-title-area">
                    <CategoryChip
                        category={{
                            value: goal.category,
                            label: goal.category,
                        }}
                        label={goal.goalName}
                        isLinked={goal.isLinked}
                    />

                    <button
                        type="button"
                        className="add-task-button"
                        onClick={handleClickAddTask}
                        aria-label={`${goal.goalName} 태스크 추가`}
                    >
                        <img
                            src={addTaskIcon}
                            alt=""
                            className="add-task-icon"
                        />
                    </button>
                </div>

                <button
                    type="button"
                    className="expand-button"
                    onClick={() => setIsExpanded((prev) => !prev)}
                    aria-label={isExpanded ? '접기' : '펼치기'}
                >
                    <img
                        src={isExpanded ? chevronDownIcon : chevronUpIcon}
                        alt=""
                        className="expand-icon"
                    />
                </button>
            </div>

            {isExpanded && (
                <div className="goal-task-rows">
                    {isCreatingTask && (
                        <NewTaskRow
                            onCreate={handleCreateTask}
                            onCancel={handleCancelCreateTask}
                        />
                    )}

                    {sortedTasks.map((task) => (
                        <TaskRow
                            key={task.taskId}
                            task={task}
                            onCheckTask={onCheckTask}
                            onClickTask={handleClickTask}
                        />
                    ))}
                </div>
            )}

            {openTaskId !== null && (
                <TaskDetailModal
                    text={taskText}
                    onChangeText={setTaskText}
                    onEdit={handleEditTask}
                    onDelete={handleDeleteTask}
                    onClose={handleCloseTaskModal}
                />
            )}
        </div>
    );
}

export default GoalGroupedTaskList;
